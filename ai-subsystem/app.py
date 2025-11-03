from fastapi import FastAPI, Body
from pydantic import BaseModel
from datetime import datetime
from typing import Optional

app = FastAPI(title="DevVault AI Subsystem")


class IntegrityReport(BaseModel):
    path: str
    sha256: str
    timestamp: Optional[str] = None


@app.get("/health")
async def health():
    return {"status": "online", "timestamp": datetime.utcnow().isoformat()}


@app.get("/ai/status")
async def ai_status():
    return {"model": "stub", "state": "idle", "timestamp": datetime.utcnow().isoformat()}


@app.post("/integrity/report")
async def integrity_report(report: IntegrityReport = Body(...)):
    # In the real system we'd persist and analyze. Here we just echo and timestamp.
    report.timestamp = datetime.utcnow().isoformat()
    return {"received": True, "report": report.dict()}


@app.post("/ai/disable")
async def ai_disable(timeout_seconds: Optional[int] = Body(0)):
    # kill-switch placeholder: in production require auth and write to a local state store
    disabled_until = None
    if timeout_seconds and timeout_seconds > 0:
        disabled_until = (datetime.utcnow().timestamp() + timeout_seconds)
    return {"disabled": True, "disabled_until": disabled_until}

