-- Add provider_email column to existing github_connections table and create normalized scopes table
ALTER TABLE github_connections ADD COLUMN provider_email VARCHAR(512);

-- Create table to store normalized scopes per connection
CREATE TABLE IF NOT EXISTS github_connection_scopes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    connection_id BIGINT NOT NULL,
    scope VARCHAR(255) NOT NULL,
    CONSTRAINT fk_conn FOREIGN KEY (connection_id) REFERENCES github_connections(id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_conn_scope_unique ON github_connection_scopes(connection_id, scope);
