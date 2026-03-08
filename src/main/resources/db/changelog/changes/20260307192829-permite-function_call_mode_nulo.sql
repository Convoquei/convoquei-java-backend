-- liquibase formatted sql

-- changeset ronal:1772922509870-1
ALTER TABLE organization_event
    ALTER COLUMN function_call_mode DROP NOT NULL;

