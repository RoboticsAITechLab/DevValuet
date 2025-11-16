-- Initial database schema creation
-- Create github_connections table first

CREATE TABLE IF NOT EXISTS github_connections (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(255) NOT NULL,
    access_token VARCHAR(512) NOT NULL,
    refresh_token VARCHAR(512),
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    provider_name VARCHAR(100) DEFAULT 'github',
    status VARCHAR(50) DEFAULT 'active'
);

CREATE INDEX IF NOT EXISTS idx_github_conn_user_id ON github_connections(user_id);
CREATE INDEX IF NOT EXISTS idx_github_conn_status ON github_connections(status);

-- Create setup_wizard_results table
CREATE TABLE IF NOT EXISTS setup_wizard_results (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    setup_id VARCHAR(255) NOT NULL UNIQUE,
    mode VARCHAR(50) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    completion_time TIMESTAMP,
    successful BOOLEAN DEFAULT FALSE,
    message TEXT,
    setup_data JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create system_requirements table
CREATE TABLE IF NOT EXISTS system_requirements (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    all_requirements_met BOOLEAN DEFAULT FALSE,
    analysis_report TEXT,
    last_analyzed TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    java_version VARCHAR(50),
    os_name VARCHAR(100),
    os_version VARCHAR(100),
    architecture VARCHAR(50),
    total_memory_mb BIGINT,
    available_memory_mb BIGINT,
    cpu_cores INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create user_preferences table
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(255) NOT NULL,
    experience_level VARCHAR(50),
    setup_mode VARCHAR(50),
    enable_telemetry BOOLEAN DEFAULT TRUE,
    enable_automatic_updates BOOLEAN DEFAULT TRUE,
    enable_beta_features BOOLEAN DEFAULT FALSE,
    preferred_theme VARCHAR(100) DEFAULT 'default',
    custom_preferences JSON,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_user_preferences_user_id ON user_preferences(user_id);