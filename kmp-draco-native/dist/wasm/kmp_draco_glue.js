(function (global) {
  function api() { return global.wasm_bindgen; }
  function ready() {
    var w = api();
    return !!w && typeof w.kmpDracoDecode === "function";
  }
  global.__kmpDraco = {
    abiVersion: function () {
      if (!ready()) throw new Error("kmp-draco wasm not loaded");
      return api().kmpDracoAbiVersion();
    },
    decode: function (u8) {
      if (!ready()) throw new Error("kmp-draco wasm not loaded");
      return api().kmpDracoDecode(u8);
    },
  };
})(typeof self !== "undefined" ? self : this);
