-- V8__add_full_text_search.sql
-- Adds full-text search (FTS) support for extracted text

-- 1) Add tsvector column (stored indexable representation)
ALTER TABLE documents
    ADD COLUMN IF NOT EXISTS search_vector tsvector;

-- 2) Backfill existing rows
UPDATE documents
SET search_vector = to_tsvector('english', COALESCE(extracted_text, ''))
WHERE search_vector IS NULL;

-- 3) Create a GIN index for fast searching
CREATE INDEX IF NOT EXISTS idx_documents_search_vector_gin
    ON documents
    USING GIN (search_vector);

-- 4) Trigger function to keep search_vector always updated
CREATE OR REPLACE FUNCTION documents_search_vector_update() RETURNS trigger AS $$
BEGIN
  NEW.search_vector := to_tsvector('english', COALESCE(NEW.extracted_text, ''));
RETURN NEW;
END
$$ LANGUAGE plpgsql;

-- 5) Attach trigger to documents table
DROP TRIGGER IF EXISTS trg_documents_search_vector_update ON documents;

CREATE TRIGGER trg_documents_search_vector_update
    BEFORE INSERT OR UPDATE OF extracted_text
                     ON documents
                         FOR EACH ROW
                         EXECUTE FUNCTION documents_search_vector_update();