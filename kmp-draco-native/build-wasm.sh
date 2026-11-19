#!/usr/bin/env bash
# Build kmp_draco_native.js + _bg.wasm via wasm-bindgen into dist/wasm
# Requires: rustup target wasm32-unknown-unknown, wasm-bindgen-cli matching Cargo.lock
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
DIST="$ROOT/dist/wasm"
TARGET="wasm32-unknown-unknown"
OUT_NAME="kmp_draco_native"

export PATH="${HOME}/.cargo/bin:${PATH}"
# shellcheck disable=SC1090
[ -f "${HOME}/.cargo/env" ] && source "${HOME}/.cargo/env"

if ! command -v cargo >/dev/null 2>&1; then
  echo "cargo not found; install rustup first" >&2
  exit 1
fi
if ! command -v wasm-bindgen >/dev/null 2>&1; then
  echo "wasm-bindgen not found; cargo install wasm-bindgen-cli --version 0.2.100" >&2
  echo "(pin to Cargo.lock wasm-bindgen version)" >&2
  exit 1
fi

rustup target add "$TARGET" >/dev/null

cd "$ROOT"
echo "==> cargo build --release ($TARGET)"
cargo build --release --target "$TARGET"

WASM="$ROOT/target/$TARGET/release/${OUT_NAME}.wasm"
# cdylib may be named with underscores
if [ ! -f "$WASM" ]; then
  WASM="$ROOT/target/$TARGET/release/kmp_draco_native.wasm"
fi
if [ ! -f "$WASM" ]; then
  echo "missing wasm artifact under target/$TARGET/release/" >&2
  ls -la "$ROOT/target/$TARGET/release/" | head -40 >&2
  exit 1
fi

rm -rf "$DIST"
mkdir -p "$DIST"
echo "==> wasm-bindgen"
wasm-bindgen "$WASM" \
  --target no-modules \
  --no-typescript \
  --out-dir "$DIST" \
  --out-name "$OUT_NAME"

# Host glue stub: window.__kmpDraco.decode(Uint8Array) → JsDecodedMesh
# no-modules: exports live on wasm_bindgen after init (not a separate kmp_draco_native global).
cat > "$DIST/kmp_draco_glue.js" <<'EOF'
/* Host: load kmp_draco_native.js then this file; await wasm_bindgen(bytes|url) before decode. */
(function (global) {
  function api() {
    return global.wasm_bindgen;
  }
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
EOF

echo "OK → $DIST"
ls -la "$DIST"
du -sh "$DIST"/*
