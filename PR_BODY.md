Summary
- Adds programmatic snapshot export/import support and an end-to-end test (`SnapshotCliE2ETest`) that exports to a file and restores it.
- Fixes test transaction scope and snapshot import merge issues by clearing entity IDs before save to ensure fresh inserts.
- Documents snapshot CLI usage in `offline-engine/README.md`.
- Adds sqlite-jdbc dependency and a lightweight SQLite driver smoke test to validate the runtime driver.
- Adds a unit test for `SnapshotService` (mocked repositories).

Files changed (high level)
- offline-engine/src/main/java/.../service/SnapshotService.java — import logic updated to clear IDs
- offline-engine/src/main/java/.../cli/SnapshotCli.java — programmatic CLI
- offline-engine/src/test/java/.../SnapshotCliE2ETest.java — E2E test (transaction scope fixed)
- offline-engine/src/test/java/.../SnapshotIntegrationTest2.java — integration test
- offline-engine/src/test/java/.../SqliteDriverSmokeTest.java — new test to validate sqlite driver
- offline-engine/src/test/java/.../service/SnapshotServiceUnitTest.java — unit test for import behavior
- offline-engine/pom.xml — added sqlite-jdbc runtime dependency
- offline-engine/README.md — documentation for snapshot CLI
- CHANGELOG.md — root changelog entry

Notes for reviewers / CI
- Snapshot restore behavior: current implementation clears entity IDs on import to avoid merge/stale state issues. If preserving original IDs is required for your use-case, we should implement an ID-mapping strategy and re-link dependent entities.
- The snapshot tests use Flyway migrations against an in-memory H2 DB in CI/test runs. The SQLite dependency is present for runtime offline mode and we included a smoke test to ensure the JDBC driver is available.
- I did not change any behavior in other modules.

Please run module tests and review the offline-engine changes. If you want me to squash/reword commits before merging, I can do that.