-- liquibase formatted sql

-- changeset ronal:1773200025159-3
ALTER TABLE organization_event_called_function
    ADD assignment_mode VARCHAR(30) NOT NULL DEFAULT 'MANUAL';
ALTER TABLE organization_event_called_function
    ADD max_members INTEGER;
ALTER TABLE organization_event_called_function
    ADD min_members INTEGER NOT NULL DEFAULT 1;

