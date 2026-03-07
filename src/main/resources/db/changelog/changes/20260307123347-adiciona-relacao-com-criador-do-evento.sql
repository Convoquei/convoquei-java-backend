-- liquibase formatted sql

-- changeset ronal:1772897626887-3
ALTER TABLE organization_event
    ADD created_by_member_id UUID;

-- changeset ronal:1772897626887-4
ALTER TABLE organization_event
    ADD CONSTRAINT FK_ORGANIZATION_EVENT_CREATED_BY_MEMBER FOREIGN KEY (created_by_member_id) REFERENCES organization_member (id);

