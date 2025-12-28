ALTER TABLE notifications ADD COLUMN description VARCHAR(255);

UPDATE notifications n
SET description = c.name
FROM notification_categories c
WHERE n.category_id = c.id;

ALTER TABLE notifications ALTER COLUMN description SET NOT NULL;

ALTER TABLE notifications DROP COLUMN category_id CASCADE;
