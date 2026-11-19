//! MeshDecoder wrapper → flat attribute arrays (unique_id keyed).

use draco_core::{
    DecoderBuffer, FaceIndex, GeometryAttributeType, Mesh, MeshDecoder,
};

#[derive(Debug, Clone)]
pub struct DecodedAttr {
    pub unique_id: u32,
    pub attr_type: i32,
    pub num_components: i32,
    pub values: Vec<f32>,
}

#[derive(Debug, Clone)]
pub struct DecodedMeshNative {
    pub num_points: i32,
    pub indices: Vec<i32>,
    pub attributes: Vec<DecodedAttr>,
}

#[derive(Debug)]
pub struct DecodeError(pub String);

impl std::fmt::Display for DecodeError {
    fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
        write!(f, "{}", self.0)
    }
}

impl std::error::Error for DecodeError {}

fn attr_type_code(t: GeometryAttributeType) -> i32 {
    match t {
        GeometryAttributeType::Position => 0,
        GeometryAttributeType::Normal => 1,
        GeometryAttributeType::Color => 2,
        GeometryAttributeType::TexCoord => 3,
        GeometryAttributeType::Generic => 4,
        GeometryAttributeType::Invalid => -1,
    }
}

/// Decode a Draco triangle-mesh bitstream into per-attribute f32 buffers + face indices.
pub fn decode_mesh(bytes: &[u8]) -> Result<DecodedMeshNative, DecodeError> {
    if bytes.is_empty() {
        return Err(DecodeError("empty Draco buffer".into()));
    }
    let mut buffer = DecoderBuffer::new(bytes);
    let mut mesh = Mesh::new();
    MeshDecoder::new()
        .decode(&mut buffer, &mut mesh)
        .map_err(|e| DecodeError(format!("MeshDecoder failed: {e}")))?;

    let num_points = mesh.num_points();
    if num_points == 0 {
        return Err(DecodeError("decoded mesh has 0 points".into()));
    }

    let pos_id = mesh.named_attribute_id(GeometryAttributeType::Position);
    if pos_id < 0 {
        return Err(DecodeError("decoded mesh has no POSITION attribute".into()));
    }

    let num_faces = mesh.num_faces();
    let mut indices = Vec::with_capacity(num_faces * 3);
    for fi in 0..num_faces {
        let face = mesh.face(FaceIndex(fi as u32));
        for corner in face {
            indices.push(corner.0 as i32);
        }
    }

    let mut attributes = Vec::with_capacity(mesh.num_attributes() as usize);
    for att_id in 0..mesh.num_attributes() {
        let attr = mesh.attribute(att_id);
        let comps = attr.num_components() as usize;
        if comps == 0 {
            continue;
        }
        let values = attr.read_f32s(num_points, comps);
        attributes.push(DecodedAttr {
            unique_id: attr.unique_id(),
            attr_type: attr_type_code(attr.attribute_type()),
            num_components: comps as i32,
            values,
        });
    }

    Ok(DecodedMeshNative {
        num_points: num_points as i32,
        indices,
        attributes,
    })
}

#[cfg(test)]
mod tests {
    use super::*;
    use std::path::PathBuf;

    #[test]
    fn tokyo_prim0_has_position() {
        let path = PathBuf::from(env!("CARGO_MANIFEST_DIR")).join("testdata/tokyo_prim0.drc");
        let bytes = std::fs::read(&path).expect("fixture");
        let mesh = decode_mesh(&bytes).expect("decode");
        assert!(mesh.num_points > 0);
        assert!(!mesh.indices.is_empty());
        let pos = mesh
            .attributes
            .iter()
            .find(|a| a.unique_id == 2 || a.attr_type == 0)
            .expect("POSITION attr");
        assert_eq!(pos.num_components, 3);
        assert_eq!(pos.values.len(), mesh.num_points as usize * 3);
    }

    #[test]
    fn empty_fails() {
        assert!(decode_mesh(&[]).is_err());
    }
}
