CREATE TYPE job_title_type AS ENUM (
    'NURSE',
    'ADMINISTRATIVE_TECHNICIAN',
    'RESIDENT',
    'INTERN',
    'PHYSIOTHERAPIST',
    'DOCTOR',
    'PHARMACIST',
    'NURSING_TECHNICIAN'
);

ALTER TABLE users ADD COLUMN job_title job_title_type;
ALTER TABLE adverse_events ADD COLUMN created_by UUID;
ALTER TABLE notifications ADD COLUMN created_by UUID;
