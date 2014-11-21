INSERT INTO eperson (eperson_id, email, firstname, lastname)
VALUES (2147483647, 'test@unit.test', 'unit', 'test');

INSERT INTO item (item_id, submitter_id, owning_collection)
VALUES (2147483647, 2147483647, 2147483647),
  (2147483646, 2147483647, 2147483647),
  (2147483645, 2147483647, 2147483647),
  (2147483644, 2147483647, 2147483647);

INSERT INTO collection(collection_id)
    VALUES (2147483647);

INSERT INTO Type (type_id, left_type, right_type, left_label, right_label, left_min_cardinality, left_max_cardinality, right_min_cardinality, right_max_cardinality, semantic_ruleset)
VALUES (2147483645, 'test type a', 'type b', 'label a', 'label b', 1, 1, 1, 5, 'Rules I'),
  (2147483646, 'test type a', 'type c ', 'label a', 'label c', 1, 1, 1, 5, 'Rules II'),
  (2147483647, 'test type a', 'type d', 'label a', 'label d', 1, 1, 1, 5, 'Rules II');

INSERT INTO relationship (relationship_id, left_id, type_id, right_id)
VALUES (2147483647, 2147483646, 2147483647, 2147483647),
  (2147483646, 2147483646, 2147483646, 2147483645);