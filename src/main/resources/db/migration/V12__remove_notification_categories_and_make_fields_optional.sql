DROP TABLE IF EXISTS notification_categories CASCADE;

ALTER TABLE notifications ALTER COLUMN classification_id DROP NOT NULL;
ALTER TABLE notifications ALTER COLUMN professional_category_id DROP NOT NULL;
