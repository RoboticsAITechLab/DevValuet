# Aegis-2100 — AI Security Bridge (scaffold)

This is a passive FastAPI scaffold used by the DevValuet Cockpit to host local AI security services.

Run (dev):

```powershell
cd cockpit-ai
python -m venv .venv
.\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
uvicorn app:app --port 8001
```

By default the AI is passive and disables file access. Use the `/ai/disable` endpoint to toggle the kill-switch flag.
