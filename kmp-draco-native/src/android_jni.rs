//! JNI: com.jlj.kmp.draco.AndroidDracoPlatform

use jni::objects::{JClass, JFloatArray, JIntArray, JObject, JObjectArray, JValue};
use jni::sys::{jbyteArray, jobject};
use jni::JNIEnv;

use crate::decode::decode_mesh;

#[no_mangle]
pub extern "system" fn Java_com_jlj_kmp_draco_AndroidDracoPlatform_nativeDecode(
    mut env: JNIEnv,
    _class: JClass,
    bytes: jbyteArray,
) -> jobject {
    let result = (|| -> Result<jobject, String> {
        let arr = unsafe { jni::objects::JByteArray::from_raw(bytes) };
        let data = env
            .convert_byte_array(&arr)
            .map_err(|e| format!("byte array: {e}"))?;
        let mesh = decode_mesh(&data).map_err(|e| e.0)?;

        let decoded_cls = env
            .find_class("com/jlj/kmp/draco/DecodedMesh")
            .map_err(|e| format!("find DecodedMesh: {e}"))?;
        let attr_cls = env
            .find_class("com/jlj/kmp/draco/DecodedAttribute")
            .map_err(|e| format!("find DecodedAttribute: {e}"))?;

        let indices: JIntArray = env
            .new_int_array(mesh.indices.len() as i32)
            .map_err(|e| format!("new_int_array: {e}"))?;
        env.set_int_array_region(&indices, 0, &mesh.indices)
            .map_err(|e| format!("set indices: {e}"))?;

        let attr_array: JObjectArray = env
            .new_object_array(mesh.attributes.len() as i32, &attr_cls, JObject::null())
            .map_err(|e| format!("new attr array: {e}"))?;

        for (i, a) in mesh.attributes.iter().enumerate() {
            let values: JFloatArray = env
                .new_float_array(a.values.len() as i32)
                .map_err(|e| format!("new_float_array: {e}"))?;
            env.set_float_array_region(&values, 0, &a.values)
                .map_err(|e| format!("set floats: {e}"))?;
            let attr_obj = env
                .new_object(
                    &attr_cls,
                    "(III[F)V",
                    &[
                        JValue::Int(a.unique_id as i32),
                        JValue::Int(a.attr_type),
                        JValue::Int(a.num_components),
                        JValue::Object(&values),
                    ],
                )
                .map_err(|e| format!("new DecodedAttribute: {e}"))?;
            env.set_object_array_element(&attr_array, i as i32, attr_obj)
                .map_err(|e| format!("set attr[{i}]: {e}"))?;
        }

        let decoded = env
            .new_object(
                &decoded_cls,
                "(I[I[Lcom/jlj/kmp/draco/DecodedAttribute;)V",
                &[
                    JValue::Int(mesh.num_points),
                    JValue::Object(&indices),
                    JValue::Object(&attr_array),
                ],
            )
            .map_err(|e| format!("new DecodedMesh: {e}"))?;
        Ok(decoded.into_raw())
    })();

    match result {
        Ok(obj) => obj,
        Err(msg) => {
            let _ = env.throw_new("com/jlj/kmp/draco/DracoDecodeException", msg);
            std::ptr::null_mut()
        }
    }
}
