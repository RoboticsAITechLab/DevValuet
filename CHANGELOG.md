# Changelog

## Unreleased (feature/enterprise-scaffold) - 2025-11-06

- offline-engine: add snapshot export/import support and programmatic CLI (`SnapshotCli`)
- offline-engine: add end-to-end test `SnapshotCliE2ETest` and integration test for snapshot roundtrip
- offline-engine: fix test transaction scope and import behavior (clear entity IDs before save to avoid merge/stale state)
- offline-engine: document CLI usage and snapshot restore notes in module README
- Build/test: various CI/test stability fixes (encoding, Jackson JavaTimeModule registration during tests)

(For full details see module READMEs and commit history.)
