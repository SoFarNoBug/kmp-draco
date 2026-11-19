//! H5: wasm-bindgen decode surface (not the C header).

use js_sys::{Float32Array, Int32Array, Uint8Array};
use wasm_bindgen::prelude::*;

use crate::decode::decode_mesh;

#[wasm_bindgen]
pub struct JsDecodedAttribute {
    unique_id: i32,
    attr_type: i32,
    num_components: i32,
    values: Float32Array,
}

#[wasm_bindgen]
impl JsDecodedAttribute {
    #[wasm_bindgen(getter, js_name = uniqueId)]
    pub fn unique_id(&self) -> i32 {
        self.unique_id
    }

    #[wasm_bindgen(getter, js_name = attrType)]
    pub fn attr_type(&self) -> i32 {
        self.attr_type
    }

    #[wasm_bindgen(getter, js_name = numComponents)]
    pub fn num_components(&self) -> i32 {
        self.num_components
    }

    #[wasm_bindgen(getter)]
    pub fn values(&self) -> Float32Array {
        self.values.clone()
    }
}

#[wasm_bindgen]
pub struct JsDecodedMesh {
    num_points: i32,
    indices: Int32Array,
    attributes: js_sys::Array,
}

#[wasm_bindgen]
impl JsDecodedMesh {
    #[wasm_bindgen(getter, js_name = numPoints)]
    pub fn num_points(&self) -> i32 {
        self.num_points
    }

    #[wasm_bindgen(getter)]
    pub fn indices(&self) -> Int32Array {
        self.indices.clone()
    }

    #[wasm_bindgen(getter)]
    pub fn attributes(&self) -> js_sys::Array {
        self.attributes.clone()
    }
}

/// Decode Draco bitstream; throws JS Error on failure.
#[wasm_bindgen(js_name = kmpDracoDecode)]
pub fn kmp_draco_decode_js(bytes: Uint8Array) -> Result<JsDecodedMesh, JsValue> {
    let mut buf = vec![0u8; bytes.length() as usize];
    bytes.copy_to(&mut buf);
    let mesh = decode_mesh(&buf).map_err(|e| JsValue::from_str(&e.0))?;

    let indices = Int32Array::new_with_length(mesh.indices.len() as u32);
    for (i, v) in mesh.indices.iter().enumerate() {
        indices.set_index(i as u32, *v);
    }

    let attrs = js_sys::Array::new();
    for a in mesh.attributes {
        let values = Float32Array::new_with_length(a.values.len() as u32);
        for (i, v) in a.values.iter().enumerate() {
            values.set_index(i as u32, *v);
        }
        let attr = JsDecodedAttribute {
            unique_id: a.unique_id as i32,
            attr_type: a.attr_type,
            num_components: a.num_components,
            values,
        };
        attrs.push(&JsValue::from(attr));
    }

    Ok(JsDecodedMesh {
        num_points: mesh.num_points,
        indices,
        attributes: attrs,
    })
}

#[wasm_bindgen(js_name = kmpDracoAbiVersion)]
pub fn kmp_draco_abi_version_js() -> u32 {
    1
}
