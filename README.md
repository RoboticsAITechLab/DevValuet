# DevVault Pro X — Prototype

This workspace contains a minimal prototype scaffold for DevVault Pro X:
- `desktop-ui` — JavaFX frontend (FXML)
- `backend-core` — Spring Boot backend (REST)
- `ai-subsystem` — Python FastAPI stub

Goals for this prototype:
- Provide a minimal JavaFX window that queries backend `/health`
- Provide a Spring Boot REST endpoint at `/health`
- Provide a FastAPI AI stub at `ai-subsystem/app.py`

Run instructions (Windows PowerShell):

1) Build backend and UI (requires Maven and JDK 17+):

```powershell
cd "c:\Users\ankit\Desktop\DevValuet"
mvn -v
mvn -DskipTests package
```

2) Run backend:

```powershell
cd "c:\Users\ankit\Desktop\DevValuet\backend-core"
mvn spring-boot:run
```

3) Run JavaFX desktop app (from another shell):

```powershell
cd "c:\Users\ankit\Desktop\DevValuet\desktop-ui"
mvn javafx:run
```

4) (Optional) Run the AI FastAPI stub:

```powershell
cd "c:\Users\ankit\Desktop\DevValuet\ai-subsystem"
python -m venv .venv; .\.venv\Scripts\Activate.ps1
pip install -r requirements.txt
uvicorn app:app --port 8001 --reload
```

Notes and assumptions:
- Using Maven and Java 17 by default. Change `pom.xml` properties to use Java 21.
- The prototype uses a simple network call to `http://localhost:8080/health` from the UI.
- AI subsystem will never read files without explicit consent.
