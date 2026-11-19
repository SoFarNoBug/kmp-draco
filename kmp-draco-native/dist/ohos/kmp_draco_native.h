/* kmp_draco_native — C ABI for iOS / Ohos (Android keeps JNI). Opaque mesh + getters. */
#ifndef KMP_DRACO_NATIVE_H
#define KMP_DRACO_NATIVE_H

#include <stddef.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct KmpDracoMesh KmpDracoMesh;

/** ABI version (bump on breaking C layout/semantics). */
uint32_t kmp_draco_abi_version(void);

/**
 * Decode Draco triangle-mesh bitstream.
 * On failure returns NULL; call kmp_draco_last_error for message.
 */
KmpDracoMesh *kmp_draco_decode(const uint8_t *bytes, size_t len);

/** Thread-local last error; writes NUL-terminated string; returns bytes written (excl NUL). */
uint32_t kmp_draco_last_error(char *out_buf, uint32_t out_len);

void kmp_draco_mesh_free(KmpDracoMesh *mesh);

int32_t kmp_draco_mesh_num_points(const KmpDracoMesh *mesh);

uint32_t kmp_draco_mesh_index_count(const KmpDracoMesh *mesh);
const int32_t *kmp_draco_mesh_indices(const KmpDracoMesh *mesh);

uint32_t kmp_draco_mesh_attr_count(const KmpDracoMesh *mesh);
int32_t kmp_draco_attr_unique_id(const KmpDracoMesh *mesh, uint32_t index);
int32_t kmp_draco_attr_type(const KmpDracoMesh *mesh, uint32_t index);
int32_t kmp_draco_attr_num_components(const KmpDracoMesh *mesh, uint32_t index);
uint32_t kmp_draco_attr_value_count(const KmpDracoMesh *mesh, uint32_t index);
const float *kmp_draco_attr_values(const KmpDracoMesh *mesh, uint32_t index);

#ifdef __cplusplus
}
#endif

#endif /* KMP_DRACO_NATIVE_H */
