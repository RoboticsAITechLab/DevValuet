# offline-engine

This module contains the offline-first engine used by DevValuet. It provides a small embedded database, JPA repositories and tooling to export/import snapshots for bootstrapping and sync.

CLI snapshot usage
------------------

This module exposes a small programmatic CLI to export/import snapshots as JSON. It's intended for local bootstrapping and developer workflows (and is covered by an end-to-end test).

Quick examples:

- Export to file (programmatic): `snapshotCli.exportTo(path)`
- Import from file (programmatic): `snapshotCli.importFrom(path)`

Run the offline-engine tests locally (this will run Flyway migrations against an in-memory H2 database):

```powershell
mvn -pl offline-engine test
```

Notes
-----

- The snapshot import clears entity IDs before saving to avoid optimistic locking / merge issues during restores. This produces fresh rows in the local DB.
- Tests register the Jackson `JavaTimeModule` so `Instant` fields are serialized correctly.

If you'd like, I can add a tiny `main()` entrypoint to make the CLI runnable from the command line.
