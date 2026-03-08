-- liquibase formatted sql

-- changeset ronal:1772938853567-6
ALTER TABLE organization_event_participant
    DROP CONSTRAINT fk_event_participant_source_group;

-- changeset ronal:1772938853567-4
ALTER TABLE organization_event_participant
    ADD status VARCHAR(30) DEFAULT 'PENDING';

-- changeset ronal:1772938853567-5
ALTER TABLE organization_event_participant
    ALTER COLUMN status SET NOT NULL;

-- changeset ronal:1772938853567-7
ALTER TABLE organization_event_participant
    DROP COLUMN source_group_id;

-- changeset ronal:1772938853567-1
ALTER TABLE organization_event_participant
    ALTER COLUMN availability_status TYPE VARCHAR(30) USING (availability_status::VARCHAR(30));

