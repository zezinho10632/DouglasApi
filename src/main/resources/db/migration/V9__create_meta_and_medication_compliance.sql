-- V9__create_meta_and_medication_compliance.sql

CREATE TABLE meta_compliance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    period_id UUID NOT NULL,
    sector_id UUID NOT NULL,
    goal_value DECIMAL(5, 2) NOT NULL,
    percentage DECIMAL(5, 2) NOT NULL,
    created_by UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_meta_compliance_period FOREIGN KEY (period_id) REFERENCES periods(id),
    CONSTRAINT fk_meta_compliance_sector FOREIGN KEY (sector_id) REFERENCES sectors(id),
    CONSTRAINT fk_meta_compliance_user FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT uq_meta_compliance_period UNIQUE (period_id)
);

CREATE TABLE medication_compliance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    period_id UUID NOT NULL,
    sector_id UUID NOT NULL,
    percentage DECIMAL(5, 2) NOT NULL,
    created_by UUID,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_medication_compliance_period FOREIGN KEY (period_id) REFERENCES periods(id),
    CONSTRAINT fk_medication_compliance_sector FOREIGN KEY (sector_id) REFERENCES sectors(id),
    CONSTRAINT fk_medication_compliance_user FOREIGN KEY (created_by) REFERENCES users(id),
    CONSTRAINT uq_medication_compliance_period UNIQUE (period_id)
);
