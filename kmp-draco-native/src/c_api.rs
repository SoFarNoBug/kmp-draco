//! C ABI for iOS / Ohos (opaque mesh). Android keeps JNI.

use std::cell::RefCell;
use std::os::raw::{c_char, c_float, c_int};
use std::ptr;
use std::slice;

use crate::decode::{decode_mesh, DecodedAttr, DecodedMeshNative};

thread_local! {
    static LAST_ERROR: RefCell<String> = RefCell::new(String::new());
}

fn set_error(msg: impl Into<String>) {
    LAST_ERROR.with(|e| *e.borrow_mut() = msg.into());
}

fn clear_error() {
    LAST_ERROR.with(|e| e.borrow_mut().clear());
}

fn with_attr<R>(
    mesh: *const DecodedMeshNative,
    index: u32,
    f: impl FnOnce(&DecodedAttr) -> R,
    default: R,
) -> R {
    if mesh.is_null() {
        return default;
    }
    unsafe {
        match (&(*mesh).attributes).get(index as usize) {
            Some(a) => f(a),
            None => default,
        }
    }
}

#[no_mangle]
pub extern "C" fn kmp_draco_abi_version() -> u32 {
    1
}

#[no_mangle]
pub extern "C" fn kmp_draco_last_error(out_buf: *mut c_char, out_len: u32) -> u32 {
    if out_buf.is_null() || out_len == 0 {
        return 0;
    }
    let msg = LAST_ERROR.with(|e| e.borrow().clone());
    let bytes = msg.as_bytes();
    let max = (out_len as usize).saturating_sub(1);
    let n = bytes.len().min(max);
    unsafe {
        ptr::copy_nonoverlapping(bytes.as_ptr(), out_buf as *mut u8, n);
        *out_buf.add(n) = 0;
    }
    n as u32
}

#[no_mangle]
pub extern "C" fn kmp_draco_decode(bytes: *const u8, len: usize) -> *mut DecodedMeshNative {
    clear_error();
    if bytes.is_null() && len > 0 {
        set_error("null bytes pointer");
        return ptr::null_mut();
    }
    let result = std::panic::catch_unwind(std::panic::AssertUnwindSafe(|| {
        let slice = if len == 0 {
            &[][..]
        } else {
            unsafe { slice::from_raw_parts(bytes, len) }
        };
        decode_mesh(slice)
    }));
    match result {
        Ok(Ok(mesh)) => Box::into_raw(Box::new(mesh)),
        Ok(Err(e)) => {
            set_error(e.0);
            ptr::null_mut()
        }
        Err(_) => {
            set_error("panic in kmp_draco_decode");
            ptr::null_mut()
        }
    }
}

#[no_mangle]
pub extern "C" fn kmp_draco_mesh_free(mesh: *mut DecodedMeshNative) {
    if !mesh.is_null() {
        unsafe {
            drop(Box::from_raw(mesh));
        }
    }
}

#[no_mangle]
pub extern "C" fn kmp_draco_mesh_num_points(mesh: *const DecodedMeshNative) -> c_int {
    if mesh.is_null() {
        return 0;
    }
    unsafe { (*mesh).num_points }
}

#[no_mangle]
pub extern "C" fn kmp_draco_mesh_index_count(mesh: *const DecodedMeshNative) -> u32 {
    if mesh.is_null() {
        return 0;
    }
    unsafe { (*mesh).indices.len() as u32 }
}

#[no_mangle]
pub extern "C" fn kmp_draco_mesh_indices(mesh: *const DecodedMeshNative) -> *const c_int {
    if mesh.is_null() {
        return ptr::null();
    }
    unsafe {
        let m = &*mesh;
        if m.indices.is_empty() {
            ptr::null()
        } else {
            m.indices.as_ptr()
        }
    }
}

#[no_mangle]
pub extern "C" fn kmp_draco_mesh_attr_count(mesh: *const DecodedMeshNative) -> u32 {
    if mesh.is_null() {
        return 0;
    }
    unsafe { (*mesh).attributes.len() as u32 }
}

#[no_mangle]
pub extern "C" fn kmp_draco_attr_unique_id(mesh: *const DecodedMeshNative, index: u32) -> c_int {
    with_attr(mesh, index, |a| a.unique_id as c_int, -1)
}

#[no_mangle]
pub extern "C" fn kmp_draco_attr_type(mesh: *const DecodedMeshNative, index: u32) -> c_int {
    with_attr(mesh, index, |a| a.attr_type, -1)
}

#[no_mangle]
pub extern "C" fn kmp_draco_attr_num_components(mesh: *const DecodedMeshNative, index: u32) -> c_int {
    with_attr(mesh, index, |a| a.num_components, 0)
}

#[no_mangle]
pub extern "C" fn kmp_draco_attr_value_count(mesh: *const DecodedMeshNative, index: u32) -> u32 {
    with_attr(mesh, index, |a| a.values.len() as u32, 0)
}

#[no_mangle]
pub extern "C" fn kmp_draco_attr_values(mesh: *const DecodedMeshNative, index: u32) -> *const c_float {
    with_attr(
        mesh,
        index,
        |a| {
            if a.values.is_empty() {
                ptr::null()
            } else {
                a.values.as_ptr()
            }
        },
        ptr::null(),
    )
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::path::PathBuf;

    #[test]
    fn c_api_tokyo_roundtrip() {
        let path = PathBuf::from(env!("CARGO_MANIFEST_DIR")).join("testdata/tokyo_prim0.drc");
        let bytes = std::fs::read(&path).expect("fixture");
        let mesh = kmp_draco_decode(bytes.as_ptr(), bytes.len());
        assert!(!mesh.is_null());
        assert!(kmp_draco_mesh_num_points(mesh) > 0);
        assert!(kmp_draco_mesh_attr_count(mesh) > 0);
        kmp_draco_mesh_free(mesh);
        assert_eq!(kmp_draco_abi_version(), 1);
    }
}
