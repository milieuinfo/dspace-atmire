DROP FUNCTION getnextid( VARCHAR );
CREATE FUNCTION getnextid(t VARCHAR(40))
  RETURNS INTEGER AS
  $$ SELECT
       CAST(nextval(
                ( SELECT
                cast(relname AS TEXT)
                FROM pg_class
                WHERE relname = $1 || '_seq'
                      OR relname = $1 || '_' || $1 || '_id_seq'))
         AS INTEGER) AS RESULT; $$ LANGUAGE SQL;


CREATE OR REPLACE FUNCTION create_table_type()
  RETURNS VOID AS
  $$
  BEGIN

    IF EXISTS(
        SELECT
          *
        FROM pg_catalog.pg_tables
        WHERE tablename = 'Type'
    )
    THEN
      RAISE NOTICE 'Table "type" already exists.';
    ELSE
      CREATE TABLE IF NOT EXISTS Type
      (
        type_id               SERIAL PRIMARY KEY,
        left_type             VARCHAR(256),
        right_type            VARCHAR(256),
        left_label            TEXT,
        right_label           TEXT,
        left_min_cardinality  INTEGER,
        right_min_cardinality INTEGER,
        left_max_cardinality  INTEGER,
        right_max_cardinality INTEGER,
        semantic_ruleset      VARCHAR(256)
      );
    END IF;

  END;
  $$ LANGUAGE SQL;


CREATE OR REPLACE FUNCTION create_table_relationship()
  RETURNS VOID AS
  $$
  BEGIN

    IF EXISTS(
        SELECT
          *
        FROM pg_catalog.pg_tables
        WHERE tablename = 'Relationship'
    )
    THEN
      RAISE NOTICE 'Table "relationship" already exists.';
    ELSE
      CREATE TABLE IF NOT EXISTS Relationship
      (
        relationship_id SERIAL PRIMARY KEY,
        left_id         INTEGER REFERENCES Item ON DELETE CASCADE,
        type_id         INTEGER REFERENCES Type ON DELETE RESTRICT,
        right_id        INTEGER REFERENCES Item ON DELETE CASCADE,
        CONSTRAINT relationship_left_type_right_unique UNIQUE (left_id, type_id, right_id)
      );
    END IF;

  END;
  $$ LANGUAGE SQL;


SELECT create_table_type();
SELECT create_table_relationship();