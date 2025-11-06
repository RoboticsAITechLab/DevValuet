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
# DevValuet — The 2100 Cockpit (Prototype)

Welcome to the DevValuet 2100 Cockpit prototype — an offline-first, modular, local-first project cockpit with a passive AI security bridge (Aegis-2100).

High-level goals
- Offline-first operation (no cloud dependency required for basic workflows)
- Local security and privacy-first defaults (AI passive by default, encryption disabled during early development)
- Modular architecture so features (plugins, backups, AI) can be enabled incrementally

AI name: Aegis-2100 (default)

Repository scaffold (important folders)
- `cockpit-core` — Java Spring Boot core services (REST APIs, Git integration, storage layer)
- `cockpit-ui` — JavaFX desktop UI (standalone application, 2100 aesthetic)
- `cockpit-ai` — Python FastAPI AI bridge (passive by default, kill-switch endpoint)
- `common`, `backend-core`, `desktop-ui`, `plugins`, `backup-engine` — existing modules and prototypes

Quick start (development) — Windows PowerShell

1) Build core module (skip tests during rapid iteration):

```powershell
cd "C:\Users\ankit\Desktop\DevValuet"
mvn -pl cockpit-core -am -DskipTests package
```

2) Run the core service (Spring Boot):

```powershell
cd "C:\Users\ankit\Desktop\DevValuet\cockpit-core"
mvn spring-boot:run
```

3) Run the JavaFX desktop app (from another shell):

```powershell
cd "C:\Users\ankit\Desktop\DevValuet\cockpit-ui"
mvn javafx:run
```

4) (Optional) Run the AI FastAPI scaffold (`cockpit-ai`):

```powershell
cd "C:\Users\ankit\Desktop\DevValuet\cockpit-ai"
python -m venv .venv
.\.venv\Scripts\Activate.ps1
python -m pip install -r requirements.txt
uvicorn app:app --port 8001
```

Notes
- The encryption manager is intentionally disabled during early development: see `devvault.encryption.disabled` system property. Keep it disabled until the persistence bug is resolved.
- The AI bridge `Aegis-2100` is passive by default and will not read project files or datasets. Use `/ai/disable` to toggle the kill-switch.
- The Git integration currently uses the system `git` CLI for prototyping; later we can switch to a pure-Java JGit implementation behind a feature flag.

Developer guidance
- To run unit tests for the `common` module (encryption disabled during tests):

```powershell
mvn -pl common -am -Dtest=EncryptionManagerTest test
```

- To run the full multi-module test/build:

```powershell
mvn -DskipTests=false verify
```

What's next (roadmap)
- Implement embedded storage (H2 or SQLite + Hibernate) for Projects, Datasets, Snapshots, Plugins, Tasks
- Harden Git integration and add UI-driven clone/status/commit workflows
- Build plugin framework (SPI + plugin loader + sample plugin)
- Implement incremental backup engine and re-enable encryption after fixing persistence
- Add packaging scripts and native installer guidance

If you want me to continue, I'll follow the project TODOs and execute tasks in order (start with finalizing README + storage layer). If you'd like a different priority, tell me which high-priority features to do first.
