//! kmp_draco_native · Draco bitstream → per-attribute f32 + indices (ADR 0001 / 0002).

mod decode;

pub use decode::{decode_mesh, DecodedAttr, DecodedMeshNative, DecodeError};

#[cfg(target_os = "android")]
mod android_jni;

#[cfg(not(target_arch = "wasm32"))]
mod c_api;

#[cfg(target_arch = "wasm32")]
mod wasm_exports;
