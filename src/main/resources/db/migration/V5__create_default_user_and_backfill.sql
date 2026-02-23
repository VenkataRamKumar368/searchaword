-- =====================================
-- V5: Create default user & backfill
-- =====================================

-- 1. Insert default user if not exists
INSERT INTO users (username, password, role)
SELECT 'legacy_user',
       '$2a$10$7bE0gXvVnXx1vFjY2uFZ2uFZ2uFZ2uFZ2uFZ2uFZ2uFZ2uFZ2uFZ2', -- dummy bcrypt
       'ROLE_USER'
    WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE username = 'legacy_user'
);

-- 2. Assign all existing documents to this user
UPDATE documents
SET user_id = (SELECT id FROM users WHERE username = 'legacy_user')
WHERE user_id IS NULL;