-- Insert test users for development/testing using H2-compatible MERGE syntax
MERGE INTO users (id, username, email, password_hash, created_at, updated_at) 
KEY(id)
VALUES 
    (1, 'testuser1', 'testuser1@example.com', '$2a$10$dummyhash1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, 'testuser2', 'testuser2@example.com', '$2a$10$dummyhash2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
