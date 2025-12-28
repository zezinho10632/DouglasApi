ALTER TABLE adverse_events 
ADD COLUMN quantity_cases INTEGER NOT NULL DEFAULT 1,
ADD COLUMN quantity_notifications INTEGER NOT NULL DEFAULT 0;
