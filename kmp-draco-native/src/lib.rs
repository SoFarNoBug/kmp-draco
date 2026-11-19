//! kmp_draco_native · Draco bitstream → per-attribute f32 + indices (ADR 0001).

mod decode;

pub use decode::{decode_mesh, DecodedAttr, DecodedMeshNative, DecodeError};

#[cfg(target_os = "android")]
mod android_jni;
