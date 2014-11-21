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

CREATE TABLE IF NOT EXISTS Relationship
(
  relationship_id SERIAL PRIMARY KEY,
  left_id         INTEGER REFERENCES Item on DELETE CASCADE ,
  type_id         INTEGER REFERENCES Type on DELETE RESTRICT ,
  right_id        INTEGER REFERENCES Item on DELETE CASCADE ,
  CONSTRAINT relationship_left_type_right_unique UNIQUE (left_id, type_id, right_id)
);