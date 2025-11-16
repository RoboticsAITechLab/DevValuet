from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI(title="DevVault AI Bridge", version="0.1.0")

class AnalyzeRequest(BaseModel):
    text: str

class AnalyzeResponse(BaseModel):
    summary: str

@app.get("/health")
async def health():
    return {"status": "ok"}

@app.post("/analyze", response_model=AnalyzeResponse)
async def analyze(req: AnalyzeRequest):
    # Placeholder: in production this would call a model or sandboxed python logic
    text = req.text or ""
    # Very small heuristic summary: return first 120 chars
    summary = text.strip()[:120]
    return AnalyzeResponse(summary=summary)
