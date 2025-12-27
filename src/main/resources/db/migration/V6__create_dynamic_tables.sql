-- V6__create_dynamic_tables.sql

-- 1. Create new tables
CREATE TABLE notification_classifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE notification_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE professional_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. Alter notifications table
-- Remove old columns
ALTER TABLE notifications DROP COLUMN classification;
ALTER TABLE notifications DROP COLUMN category;
ALTER TABLE notifications DROP COLUMN subcategory;
ALTER TABLE notifications DROP COLUMN description;
ALTER TABLE notifications DROP COLUMN is_self_notification;
ALTER TABLE notifications DROP COLUMN professional_category;
ALTER TABLE notifications DROP COLUMN professional_name;
ALTER TABLE notifications DROP COLUMN notification_date;

-- Add new columns
ALTER TABLE notifications ADD COLUMN classification_id UUID NOT NULL REFERENCES notification_classifications(id);
ALTER TABLE notifications ADD COLUMN category_id UUID NOT NULL REFERENCES notification_categories(id);
ALTER TABLE notifications ADD COLUMN professional_category_id UUID REFERENCES professional_categories(id);
ALTER TABLE notifications ADD COLUMN is_self_notification BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE notifications ADD COLUMN quantity INTEGER NOT NULL DEFAULT 0;

-- Create Indexes
CREATE INDEX idx_notifications_classification_id ON notifications(classification_id);
CREATE INDEX idx_notifications_category_id ON notifications(category_id);
CREATE INDEX idx_notifications_prof_cat_id ON notifications(professional_category_id);

-- 3. Alter compliance_indicators table
ALTER TABLE compliance_indicators DROP COLUMN total_patients;
