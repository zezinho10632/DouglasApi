-- ENUMS
CREATE TYPE role_type AS ENUM ('ADMIN', 'MANAGER', 'OPERATOR');
CREATE TYPE period_status AS ENUM ('OPEN', 'CLOSED', 'VALIDATED');
CREATE TYPE notification_classification AS ENUM (
    'INCIDENT_WITH_MINOR_HARM',
    'INCIDENT_WITH_MODERATE_HARM',
    'INCIDENT_WITHOUT_HARM',
    'RISK_CIRCUMSTANCE'
);
CREATE TYPE adverse_event_type AS ENUM (
    'PRESSURE_INJURY',
    'CVC_LOSS',
    'ENTERAL_TUBE',
    'CARDIORESPIRATORY_ARREST',
    'FALL',
    'ACCIDENTAL_EXTUBATION'
);

-- TABELA: users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role role_type NOT NULL DEFAULT 'OPERATOR',
    sector_id UUID,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_sector_id ON users(sector_id);

-- TABELA: sectors
CREATE TABLE sectors (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sectors_code ON sectors(code);
CREATE INDEX idx_sectors_active ON sectors(active);

-- TABELA: periods
CREATE TABLE periods (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    month INTEGER NOT NULL CHECK (month >= 1 AND month <= 12),
    year INTEGER NOT NULL CHECK (year >= 2020 AND year <= 2100),
    sector_id UUID NOT NULL REFERENCES sectors(id) ON DELETE CASCADE,
    status period_status NOT NULL DEFAULT 'OPEN',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(month, year, sector_id)
);

CREATE INDEX idx_periods_sector_id ON periods(sector_id);
CREATE INDEX idx_periods_status ON periods(status);

-- TABELA: compliance_indicators (6 metas principais)
CREATE TABLE compliance_indicators (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    period_id UUID NOT NULL UNIQUE REFERENCES periods(id) ON DELETE CASCADE,
    sector_id UUID NOT NULL REFERENCES sectors(id),
    
    -- As 6 metas (percentuais 0-100)
    complete_wristband NUMERIC(5,2) NOT NULL CHECK (complete_wristband BETWEEN 0 AND 100),
    patient_communication NUMERIC(5,2) NOT NULL CHECK (patient_communication BETWEEN 0 AND 100),
    medication_identified NUMERIC(5,2) NOT NULL CHECK (medication_identified BETWEEN 0 AND 100),
    hand_hygiene_adherence NUMERIC(5,2) NOT NULL CHECK (hand_hygiene_adherence BETWEEN 0 AND 100),
    fall_risk_assessment NUMERIC(5,2) NOT NULL CHECK (fall_risk_assessment BETWEEN 0 AND 100),
    pressure_injury_risk_assessment NUMERIC(5,2) NOT NULL CHECK (pressure_injury_risk_assessment BETWEEN 0 AND 100),
    
    total_patients INTEGER NOT NULL CHECK (total_patients > 0),
    observations TEXT,
    
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_compliance_period_id ON compliance_indicators(period_id);
CREATE INDEX idx_compliance_sector_id ON compliance_indicators(sector_id);

-- TABELA: notifications
CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    period_id UUID NOT NULL REFERENCES periods(id) ON DELETE CASCADE,
    sector_id UUID NOT NULL REFERENCES sectors(id),
    notification_date DATE NOT NULL,
    
    classification notification_classification NOT NULL,
    category VARCHAR(255) NOT NULL,
    subcategory VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    
    is_self_notification BOOLEAN NOT NULL DEFAULT FALSE,
    professional_category VARCHAR(100) NOT NULL,
    professional_name VARCHAR(255),
    
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_period_id ON notifications(period_id);
CREATE INDEX idx_notifications_sector_id ON notifications(sector_id);
CREATE INDEX idx_notifications_classification ON notifications(classification);
CREATE INDEX idx_notifications_category ON notifications(category);
CREATE INDEX idx_notifications_date ON notifications(notification_date);

-- TABELA: hand_hygiene_assessments
CREATE TABLE hand_hygiene_assessments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    period_id UUID NOT NULL UNIQUE REFERENCES periods(id) ON DELETE CASCADE,
    sector_id UUID NOT NULL REFERENCES sectors(id),
    
    total_observations INTEGER NOT NULL CHECK (total_observations > 0),
    compliant_observations INTEGER NOT NULL CHECK (compliant_observations >= 0),
    compliance_percentage NUMERIC(5,2) NOT NULL CHECK (compliance_percentage BETWEEN 0 AND 100),
    
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- TABELA: fall_risk_assessments
CREATE TABLE fall_risk_assessments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    period_id UUID NOT NULL UNIQUE REFERENCES periods(id) ON DELETE CASCADE,
    sector_id UUID NOT NULL REFERENCES sectors(id),
    
    total_patients INTEGER NOT NULL CHECK (total_patients > 0),
    assessed_on_admission INTEGER NOT NULL,
    assessment_percentage NUMERIC(5,2) NOT NULL CHECK (assessment_percentage BETWEEN 0 AND 100),
    
    -- Distribuição por nível de risco (valores absolutos)
    high_risk INTEGER NOT NULL DEFAULT 0,
    medium_risk INTEGER NOT NULL DEFAULT 0,
    low_risk INTEGER NOT NULL DEFAULT 0,
    not_assessed INTEGER NOT NULL DEFAULT 0,
    
    -- Percentuais calculados
    high_risk_percentage NUMERIC(5,2) NOT NULL CHECK (high_risk_percentage BETWEEN 0 AND 100),
    medium_risk_percentage NUMERIC(5,2) NOT NULL CHECK (medium_risk_percentage BETWEEN 0 AND 100),
    low_risk_percentage NUMERIC(5,2) NOT NULL CHECK (low_risk_percentage BETWEEN 0 AND 100),
    not_assessed_percentage NUMERIC(5,2) NOT NULL CHECK (not_assessed_percentage BETWEEN 0 AND 100),
    
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- TABELA: pressure_injury_risk_assessments (LPP)
CREATE TABLE pressure_injury_risk_assessments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    period_id UUID NOT NULL UNIQUE REFERENCES periods(id) ON DELETE CASCADE,
    sector_id UUID NOT NULL REFERENCES sectors(id),
    
    total_patients INTEGER NOT NULL CHECK (total_patients > 0),
    assessed_on_admission INTEGER NOT NULL,
    assessment_percentage NUMERIC(5,2) NOT NULL CHECK (assessment_percentage BETWEEN 0 AND 100),
    
    -- Distribuição por nível de risco
    high_risk INTEGER NOT NULL DEFAULT 0,
    medium_risk INTEGER NOT NULL DEFAULT 0,
    low_risk INTEGER NOT NULL DEFAULT 0,
    not_assessed INTEGER NOT NULL DEFAULT 0,
    
    -- Percentuais
    high_risk_percentage NUMERIC(5,2) NOT NULL CHECK (high_risk_percentage BETWEEN 0 AND 100),
    medium_risk_percentage NUMERIC(5,2) NOT NULL CHECK (medium_risk_percentage BETWEEN 0 AND 100),
    low_risk_percentage NUMERIC(5,2) NOT NULL CHECK (low_risk_percentage BETWEEN 0 AND 100),
    not_assessed_percentage NUMERIC(5,2) NOT NULL CHECK (not_assessed_percentage BETWEEN 0 AND 100),
    
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- TABELA: adverse_events
CREATE TABLE adverse_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    period_id UUID NOT NULL REFERENCES periods(id) ON DELETE CASCADE,
    sector_id UUID NOT NULL REFERENCES sectors(id),
    event_date DATE NOT NULL,
    
    event_type adverse_event_type NOT NULL,
    description TEXT NOT NULL,
    was_notified BOOLEAN NOT NULL DEFAULT FALSE,
    
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_adverse_events_period_id ON adverse_events(period_id);
CREATE INDEX idx_adverse_events_sector_id ON adverse_events(sector_id);
CREATE INDEX idx_adverse_events_type ON adverse_events(event_type);

-- Foreign key de users → sectors
ALTER TABLE users ADD CONSTRAINT fk_users_sector FOREIGN KEY (sector_id) REFERENCES sectors(id);
