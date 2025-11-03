import importlib

_fastapi_mod = None
try:
    _fastapi_mod = importlib.import_module('fastapi')
    FastAPI = getattr(_fastapi_mod, 'FastAPI', None)
except Exception:
    # FastAPI not available in this environment (linter/runtime). Provide a lightweight stub
    FastAPI = None
from datetime import datetime

if FastAPI is not None:
    app = FastAPI(title="DevVault AI Subsystem (stub)")
else:
    # minimal stub object to avoid import/runtime errors when FastAPI is absent
    class _StubApp:
        def get(self, *a, **k):
            def decorator(f):
                return f
            return decorator

        def post(self, *a, **k):
            def decorator(f):
                return f
            return decorator

    app = _StubApp()

@app.get("/health")
async def health():
    return {"status":"online","timestamp":datetime.utcnow().isoformat()}

@app.get("/ai/status")
async def ai_status():
    return {"model":"stub","state":"idle","timestamp":datetime.utcnow().isoformat()}

# Kill-switch endpoint (local-only). In real deployment, require auth and explicit consent.
@app.post("/ai/kill-switch")
async def kill_switch():
    return {"result":"AI disabled (stub)"}
