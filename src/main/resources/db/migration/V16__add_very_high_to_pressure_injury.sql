ALTER TABLE pressure_injury_risk_assessments 
ADD COLUMN very_high INTEGER NOT NULL DEFAULT 0,
ADD COLUMN very_high_percentage DECIMAL(5,2) NOT NULL DEFAULT 0.00;
