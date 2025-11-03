# AI Subsystem (FastAPI Stub)

This folder contains a minimal FastAPI app used as an AI subsystem stub.

Run:

```powershell
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
uvicorn app:app --port 8001 --reload
```

The stub exposes `/health`, `/ai/status` and a `/ai/kill-switch` endpoint (no real effect).
