-- liquibase formatted sql

-- changeset ronal:20260308002938-1
CREATE INDEX idx_org_event_org_start
    ON organization_event (organization_id, start_at);

-- changeset ronal:20260308002938-2
CREATE INDEX idx_participant_member_status
    ON organization_event_participant (organization_member_id, status);

-- changeset ronal:20260308002938-3
CREATE INDEX idx_participant_event_id
    ON organization_event_participant (organization_event_id);
