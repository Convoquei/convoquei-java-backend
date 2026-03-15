-- liquibase formatted sql

-- changeset ronal:20260312000000-1
ALTER TABLE organization_event
    ADD COLUMN cancelled_by_member_id UUID NULL,
    ADD CONSTRAINT fk_organization_event_cancelled_by_member
        FOREIGN KEY (cancelled_by_member_id) REFERENCES organization_member (id);
