DELETE FROM relationship
WHERE relationship_id = 2147483647
      OR relationship_id = 2147483646;

DELETE FROM Type
WHERE type_id = 2147483645
      OR type_id = 2147483646
      OR type_id = 2147483647;

DELETE FROM item
WHERE item_id = 2147483644
      OR item_id = 2147483645
      OR item_id = 2147483646
      OR item_id = 2147483647;

DELETE FROM eperson
WHERE eperson_id = 2147483647;