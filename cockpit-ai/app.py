from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import os

app = FastAPI(title="Aegis-2100 AI Bridge", version="0.1.0")

# Passive mode by default
AI_DISABLED_FILE = "./.aegis_disabled"

def is_disabled():
    return os.path.exists(AI_DISABLED_FILE)

class DisableRequest(BaseModel):
    disable: bool

@app.get("/ai/status")
async def status():
    return {"name": "Aegis-2100", "disabled": is_disabled()}

@app.post("/ai/disable")
async def disable(req: DisableRequest):
    # kill-switch: when disabled=true create the flag file
    try:
        if req.disable:
            open(AI_DISABLED_FILE, "w").close()
            return {"ok": True, "disabled": True}
        else:
            if os.path.exists(AI_DISABLED_FILE):
                os.remove(AI_DISABLED_FILE)
            return {"ok": True, "disabled": False}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# IMPORTANT: This scaffold intentionally never reads project files or datasets.
# All operations are constrained to memory and status flags. The AI is passive by default.
