-- V8__extract_self_notification.sql

CREATE TABLE self_notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    period_id UUID NOT NULL,
    sector_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    percentage DECIMAL(5, 2) NOT NULL,
    created_by UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_self_notifications_period FOREIGN KEY (period_id) REFERENCES periods(id),
    CONSTRAINT fk_self_notifications_sector FOREIGN KEY (sector_id) REFERENCES sectors(id),
    CONSTRAINT fk_self_notifications_user FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT uq_self_notification_period UNIQUE (period_id)
);

-- Remove is_self_notification from notifications table
ALTER TABLE notifications DROP COLUMN is_self_notification;
