-- V10__remove_observations_from_hand_hygiene.sql

ALTER TABLE hand_hygiene_assessments DROP COLUMN total_observations;
ALTER TABLE hand_hygiene_assessments DROP COLUMN compliant_observations;
