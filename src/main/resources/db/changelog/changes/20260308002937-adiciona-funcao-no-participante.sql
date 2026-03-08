-- liquibase formatted sql

-- changeset ronal:1772940577832-1
ALTER TABLE organization_event_participant
    ADD organization_function_id UUID;

-- changeset ronal:1772940577832-2
ALTER TABLE organization_event_participant
    ADD CONSTRAINT FK_ORGANIZATION_EVENT_PARTICIPANT_FUNCTION FOREIGN KEY (organization_function_id) REFERENCES organization_function (id);

