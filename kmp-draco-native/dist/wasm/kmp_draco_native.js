let wasm_bindgen = (function(exports) {
    let script_src;
    if (typeof document !== 'undefined' && document.currentScript !== null) {
        script_src = new URL(document.currentScript.src, location.href).toString();
    }

    class JsDecodedAttribute {
        static __wrap(ptr) {
            const obj = Object.create(JsDecodedAttribute.prototype);
            obj.__wbg_ptr = ptr;
            JsDecodedAttributeFinalization.register(obj, obj.__wbg_ptr, obj);
            return obj;
        }
        __destroy_into_raw() {
            const ptr = this.__wbg_ptr;
            this.__wbg_ptr = 0;
            JsDecodedAttributeFinalization.unregister(this);
            return ptr;
        }
        free() {
            const ptr = this.__destroy_into_raw();
            wasm.__wbg_jsdecodedattribute_free(ptr, 0);
        }
        /**
         * @returns {number}
         */
        get attrType() {
            const ret = wasm.jsdecodedattribute_attrType(this.__wbg_ptr);
            return ret;
        }
        /**
         * @returns {number}
         */
        get numComponents() {
            const ret = wasm.jsdecodedattribute_numComponents(this.__wbg_ptr);
            return ret;
        }
        /**
         * @returns {number}
         */
        get uniqueId() {
            const ret = wasm.jsdecodedattribute_uniqueId(this.__wbg_ptr);
            return ret;
        }
        /**
         * @returns {Float32Array}
         */
        get values() {
            const ret = wasm.jsdecodedattribute_values(this.__wbg_ptr);
            return ret;
        }
    }
    if (Symbol.dispose) JsDecodedAttribute.prototype[Symbol.dispose] = JsDecodedAttribute.prototype.free;
    exports.JsDecodedAttribute = JsDecodedAttribute;

    class JsDecodedMesh {
        static __wrap(ptr) {
            const obj = Object.create(JsDecodedMesh.prototype);
            obj.__wbg_ptr = ptr;
            JsDecodedMeshFinalization.register(obj, obj.__wbg_ptr, obj);
            return obj;
        }
        __destroy_into_raw() {
            const ptr = this.__wbg_ptr;
            this.__wbg_ptr = 0;
            JsDecodedMeshFinalization.unregister(this);
            return ptr;
        }
        free() {
            const ptr = this.__destroy_into_raw();
            wasm.__wbg_jsdecodedmesh_free(ptr, 0);
        }
        /**
         * @returns {Array<any>}
         */
        get attributes() {
            const ret = wasm.jsdecodedmesh_attributes(this.__wbg_ptr);
            return ret;
        }
        /**
         * @returns {Int32Array}
         */
        get indices() {
            const ret = wasm.jsdecodedmesh_indices(this.__wbg_ptr);
            return ret;
        }
        /**
         * @returns {number}
         */
        get numPoints() {
            const ret = wasm.jsdecodedmesh_numPoints(this.__wbg_ptr);
            return ret;
        }
    }
    if (Symbol.dispose) JsDecodedMesh.prototype[Symbol.dispose] = JsDecodedMesh.prototype.free;
    exports.JsDecodedMesh = JsDecodedMesh;

    /**
     * @returns {number}
     */
    function kmpDracoAbiVersion() {
        const ret = wasm.kmpDracoAbiVersion();
        return ret >>> 0;
    }
    exports.kmpDracoAbiVersion = kmpDracoAbiVersion;

    /**
     * Decode Draco bitstream; throws JS Error on failure.
     * @param {Uint8Array} bytes
     * @returns {JsDecodedMesh}
     */
    function kmpDracoDecode(bytes) {
        try {
            const retptr = wasm.__wbindgen_add_to_stack_pointer(-16) >>> 0;
            wasm.kmpDracoDecode(retptr, bytes);
            var r0 = getDataViewMemory0().getInt32(retptr + 4 * 0, true);
            var r1 = getDataViewMemory0().getInt32(retptr + 4 * 1, true);
            var r2 = getDataViewMemory0().getInt32(retptr + 4 * 2, true);
            if (r2) {
                throw takeFromExternrefTable0(r1);
            }
            return JsDecodedMesh.__wrap(r0);
        } finally {
            wasm.__wbindgen_add_to_stack_pointer(16);
        }
    }
    exports.kmpDracoDecode = kmpDracoDecode;
    function __wbg_get_imports() {
        const import0 = {
            __proto__: null,
            __wbg___wbindgen_throw_4c88d8bf264fd984: function(arg0, arg1) {
                throw new Error(getStringFromWasm0(arg0, arg1));
            },
            __wbg_jsdecodedattribute_new: function(arg0) {
                const ret = JsDecodedAttribute.__wrap(arg0);
                return ret;
            },
            __wbg_length_186163212ffe79c3: function(arg0) {
                const ret = arg0.length;
                return ret;
            },
            __wbg_new_37eb45aaf5eeb820: function() {
                const ret = new Array();
                return ret;
            },
            __wbg_new_with_length_5c0c9043ebd9ec32: function(arg0) {
                const ret = new Float32Array(arg0 >>> 0);
                return ret;
            },
            __wbg_new_with_length_861de131a23a8038: function(arg0) {
                const ret = new Int32Array(arg0 >>> 0);
                return ret;
            },
            __wbg_prototypesetcall_13162965d1fe7aa3: function(arg0, arg1, arg2) {
                Uint8Array.prototype.set.call(getArrayU8FromWasm0(arg0, arg1), arg2);
            },
            __wbg_push_c9f4893fc209f768: function(arg0, arg1) {
                const ret = arg0.push(arg1);
                return ret;
            },
            __wbg_set_index_1f5f4a7566f05fa1: function(arg0, arg1, arg2) {
                arg0[arg1 >>> 0] = arg2;
            },
            __wbg_set_index_839acc297b0f8e94: function(arg0, arg1, arg2) {
                arg0[arg1 >>> 0] = arg2;
            },
            __wbindgen_generic_0000000000000001: function(arg0, arg1) {
                // Cast intrinsic for `Ref(String) -> Externref`.
                const ret = getStringFromWasm0(arg0, arg1);
                return ret;
            },
            __wbindgen_init_externref_table: function() {
                const table = wasm.__wbindgen_externrefs;
                table.set(0, undefined);
                table.set(1024, undefined);
                table.set(1025, null);
                table.set(1026, true);
                table.set(1027, false);
            },
        };
        return {
            __proto__: null,
            "./kmp_draco_native_bg.js": import0,
        };
    }

    const JsDecodedAttributeFinalization = (typeof FinalizationRegistry === 'undefined')
        ? { register: () => {}, unregister: () => {} }
        : new FinalizationRegistry(ptr => wasm.__wbg_jsdecodedattribute_free(ptr, 1));
    const JsDecodedMeshFinalization = (typeof FinalizationRegistry === 'undefined')
        ? { register: () => {}, unregister: () => {} }
        : new FinalizationRegistry(ptr => wasm.__wbg_jsdecodedmesh_free(ptr, 1));

    function getArrayU8FromWasm0(ptr, len) {
        ptr = ptr >>> 0;
        return getUint8ArrayMemory0().subarray(ptr / 1, ptr / 1 + len);
    }

    let cachedDataViewMemory0 = null;
    function getDataViewMemory0() {
        if (cachedDataViewMemory0 === null || cachedDataViewMemory0.buffer.detached === true || (cachedDataViewMemory0.buffer.detached === undefined && cachedDataViewMemory0.buffer !== wasm.memory.buffer)) {
            cachedDataViewMemory0 = new DataView(wasm.memory.buffer);
        }
        return cachedDataViewMemory0;
    }

    function getStringFromWasm0(ptr, len) {
        ptr = ptr >>> 0;
        return decodeText(getUint8ArrayMemory0().subarray(ptr, ptr + len));
    }

    let cachedUint8ArrayMemory0 = null;
    function getUint8ArrayMemory0() {
        if (cachedUint8ArrayMemory0 === null || cachedUint8ArrayMemory0.byteLength === 0) {
            cachedUint8ArrayMemory0 = new Uint8Array(wasm.memory.buffer);
        }
        return cachedUint8ArrayMemory0;
    }

    function takeFromExternrefTable0(idx) {
        const value = wasm.__wbindgen_externrefs.get(idx);
        wasm.__wbindgen_export(idx);
        return value;
    }

    let cachedTextDecoder = new TextDecoder('utf-8', { ignoreBOM: true });
    cachedTextDecoder.decode();
    function decodeText(bytes) {
        return cachedTextDecoder.decode(bytes);
    }

    let wasmModule, wasmInstance, wasm;
    function __wbg_finalize_init(instance, module) {
        wasmInstance = instance;
        wasm = instance.exports;
        wasmModule = module;
        cachedDataViewMemory0 = null;
        cachedUint8ArrayMemory0 = null;
        wasm.__wbindgen_start();
        return wasm;
    }

    async function __wbg_load(module, imports) {
        if (typeof Response === 'function' && module instanceof Response) {
            if (!module.ok) {
                throw new Error(`failed to fetch Wasm: ${module.status} ${module.statusText} fetching '${module.url}'`);
            }

            if (typeof WebAssembly.instantiateStreaming === 'function') {
                try {
                    return await WebAssembly.instantiateStreaming(module, imports);
                } catch (e) {
                    const validResponse = expectedResponseType(module.type);

                    if (validResponse && module.headers.get('Content-Type') !== 'application/wasm') {
                        console.warn("`WebAssembly.instantiateStreaming` failed because your server does not serve Wasm with `application/wasm` MIME type. Falling back to `WebAssembly.instantiate` which is slower. Original error:\n", e);

                    } else { throw e; }
                }
            }

            const bytes = await module.arrayBuffer();
            return await WebAssembly.instantiate(bytes, imports);
        } else {
            const instance = await WebAssembly.instantiate(module, imports);

            if (instance instanceof WebAssembly.Instance) {
                return { instance, module };
            } else {
                return instance;
            }
        }

        function expectedResponseType(type) {
            switch (type) {
                case 'basic': case 'cors': case 'default': return true;
            }
            return false;
        }
    }

    function initSync(module) {
        if (wasm !== undefined) return wasm;


        if (module !== undefined) {
            if (Object.getPrototypeOf(module) === Object.prototype) {
                ({module} = module)
            } else {
                console.warn('using deprecated parameters for `initSync()`; pass a single object instead')
            }
        }

        const imports = __wbg_get_imports();
        if (!(module instanceof WebAssembly.Module)) {
            module = new WebAssembly.Module(module);
        }
        const instance = new WebAssembly.Instance(module, imports);
        return __wbg_finalize_init(instance, module);
    }

    let __wbg_init_promise;
    async function __wbg_init(module_or_path) {
        if (wasm !== undefined) return wasm;
        if (__wbg_init_promise === undefined) {
            __wbg_init_promise = __wbg_init_once(module_or_path);
            __wbg_init_promise.catch(() => { __wbg_init_promise = undefined; });
        }
        return __wbg_init_promise;
    }

    async function __wbg_init_once(module_or_path) {

        if (module_or_path !== undefined) {
            if (Object.getPrototypeOf(module_or_path) === Object.prototype) {
                ({module_or_path} = module_or_path)
            } else {
                console.warn('using deprecated parameters for the initialization function; pass a single object instead')
            }
        }

        if (module_or_path === undefined && script_src !== undefined) {
            module_or_path = script_src.replace(/\.js$/, "_bg.wasm");
        }
        const imports = __wbg_get_imports();

        if (typeof module_or_path === 'string' || (typeof Request === 'function' && module_or_path instanceof Request) || (typeof URL === 'function' && module_or_path instanceof URL)) {
            module_or_path = fetch(module_or_path);
        }

        const { instance, module } = await __wbg_load(await module_or_path, imports);

        return __wbg_finalize_init(instance, module);
    }

    return Object.assign(__wbg_init, { initSync }, exports);
})({ __proto__: null });
