-- Create roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(20) NOT NULL UNIQUE,
    description VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);
-- Create index
CREATE INDEX idx_roles_name ON roles(name);
-- Insert default roles
INSERT INTO roles (name, description)
VALUES ('ADMIN', 'Administrator with full access'),
    ('MANAGER', 'Manager with team management access'),
    ('USER', 'Regular user with limited access');