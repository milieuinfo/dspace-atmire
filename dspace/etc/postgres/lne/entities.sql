CREATE TABLE IF NOT EXISTS Type
(
  type_id                    SERIAL PRIMARY KEY,
  left_item_type             VARCHAR(256),
  right_item_type            VARCHAR(256),
  left_item_label            TEXT,
  right_item_label           TEXT,
  left_item_min_cardinality  INTEGER,
  right_item_min_cardinality INTEGER,
  left_item_max_cardinality  INTEGER,
  right_item_max_cardinality INTEGER,
  semantic_ruleset           VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS Item2Type
(
  left_item_id  INTEGER REFERENCES Item,
  type_id       INTEGER REFERENCES Type,
  right_item_id INTEGER REFERENCES Item,
  CONSTRAINT item2type_primary_key PRIMARY KEY (left_item_id, type_id, right_item_id),
  CONSTRAINT item2type_left_and_type_unique UNIQUE (left_item_id, type_id)
);