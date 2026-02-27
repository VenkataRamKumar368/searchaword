CREATE TABLE IF NOT EXISTS search_history (
                                              id           BIGSERIAL PRIMARY KEY,
                                              user_id      BIGINT NOT NULL,
                                              document_id  BIGINT NULL,
                                              query_text   VARCHAR(255) NOT NULL,
    query_type   VARCHAR(30)  NOT NULL,  -- WORD, LETTER
    match_count  INTEGER      NOT NULL DEFAULT 0,

    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_search_history_user
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT fk_search_history_document
    FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE SET NULL
    );

CREATE INDEX IF NOT EXISTS idx_search_history_user_created
    ON search_history (user_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_search_history_user_query
    ON search_history (user_id, query_text);