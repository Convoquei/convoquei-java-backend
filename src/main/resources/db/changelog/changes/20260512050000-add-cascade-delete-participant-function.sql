-- liquibase formatted sql

-- changeset ronal:1778929200000-1
ALTER TABLE organization_event_participant
    DROP CONSTRAINT fk_organization_event_participant_function;

-- changeset ronal:1778929200000-2
ALTER TABLE organization_event_participant
    ADD CONSTRAINT fk_organization_event_participant_function
        FOREIGN KEY (organization_function_id) REFERENCES organization_function (id) ON DELETE CASCADE;
