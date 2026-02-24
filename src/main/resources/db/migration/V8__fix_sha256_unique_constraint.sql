ALTER TABLE documents
DROP CONSTRAINT IF EXISTS idx_documents_sha256;

CREATE UNIQUE INDEX IF NOT EXISTS idx_documents_sha256_owner
    ON documents (sha256, user_id);