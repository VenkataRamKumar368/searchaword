-- =====================================
-- V4: Add user_id to documents
-- =====================================

ALTER TABLE documents
    ADD COLUMN IF NOT EXISTS user_id BIGINT;

CREATE INDEX IF NOT EXISTS idx_documents_user_id
    ON documents(user_id);