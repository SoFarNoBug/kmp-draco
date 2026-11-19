package com.jlj.kmp.draco

/**
 * @CreateDate: 2026/11/18
 * @Author: jlj
 * @Description: Draco 解码失败（空缓冲 / 非 mesh / 无 POSITION 等）。
 */
class DracoDecodeException(message: String, cause: Throwable? = null) :
    RuntimeException(message, cause)
