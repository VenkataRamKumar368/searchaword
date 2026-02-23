-- =====================================
-- V6: Enforce FK & NOT NULL
-- =====================================

-- Add foreign key
ALTER TABLE documents
    ADD CONSTRAINT fk_documents_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;

-- Make user_id NOT NULL
ALTER TABLE documents
    ALTER COLUMN user_id SET NOT NULL;