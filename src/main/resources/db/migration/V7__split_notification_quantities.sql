ALTER TABLE notifications RENAME COLUMN quantity TO quantity_classification;

ALTER TABLE notifications ADD COLUMN quantity_category INTEGER;
ALTER TABLE notifications ADD COLUMN quantity_professional INTEGER;

-- Initialize new columns with the value from classification (migration strategy)
UPDATE notifications SET quantity_category = quantity_classification;
UPDATE notifications SET quantity_professional = quantity_classification;

-- Add Not Null constraints after population
ALTER TABLE notifications ALTER COLUMN quantity_category SET NOT NULL;
ALTER TABLE notifications ALTER COLUMN quantity_professional SET NOT NULL;
