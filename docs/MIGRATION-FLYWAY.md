# Flyway migration rollout guidance

This document explains a safe process to apply the new Flyway migration `V1__add_provider_email_and_scopes.sql` that adds a `provider_email` column to `github_connections` and creates a normalized `github_connection_scopes` table.

Prerequisites
- Backup your production database (logical dump and/or filesystem snapshot) before applying any migration.
- Have a staging environment that closely mirrors production where you can run the migration first.
- Ensure you have the correct Flyway configuration and credentials.

High-level steps
1. Create a full backup of the production database.
2. Apply migration on staging:
   - Restore a recent backup to staging.
   - Start your application in staging with Flyway enabled (default) or run the Flyway CLI against the staging DB.
   - Verify the migration ran successfully and that the application functions correctly.
3. Prepare production maintenance window (if needed):
   - If you cannot run live migrations safely during normal traffic, schedule a short maintenance window.
4. Run Flyway migrate on production:
   - If you use the application startup to run Flyway, deploy the new application version with migrations included.
   - Or run Flyway CLI: `flyway -url=jdbc:... -user=... -password=... migrate`
5. Verify after migration:
   - Check Flyway history table (`flyway_schema_history`) for the new version entry and success state.
   - Spot-check application flows that use `github_connections` (create/update connection, OAuth flow)
   - Ensure no errors in application logs related to the new columns or schema.

Handling failed/partially applied migrations
- If Flyway reports a failed migration:
  1. Do NOT manually alter `flyway_schema_history` without understanding the partial changes.
  2. Restore the database from backup if the schema is in an inconsistent state.
  3. Once restored, run `flyway repair` only if you have inspected the database and are confident about the partially applied migration. `flyway repair` will remove failed rows from the schema history so you can re-run `migrate`.

Idempotency and existing data concerns
- The migration adds a nullable `provider_email` column and a new table for normalized scopes. It's designed to be safe for existing rows (no NOT NULL constraint).
- The normalized `github_connection_scopes` table will start empty; existing connections will not be automatically backfilled by the migration. Use the provided TokenMigrationRunner or a follow-up script to populate scopes if you need them for queries.

Rolling back
- Flyway migrations are not automatically reversible. To rollback you must restore the pre-migration backup.

Quick checklist (production run)
- [ ] Full DB backup completed
- [ ] Migration tested in staging
- [ ] Maintenance window scheduled (if required)
- [ ] Apply migration (Flyway migrate)
- [ ] Verify Flyway history shows success
- [ ] Smoke test critical flows
- [ ] Monitor logs for errors

Contact
If you're unsure or this is a critical production system, coordinate with your DBAs and ops team before applying migrations. If you want, I can prepare an idempotent backfill script to populate `github_connection_scopes` from existing `github_connections` entries.
