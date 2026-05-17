-- liquibase formatted sql

-- changeset ronal:1779026400000-1 splitStatements:false
CREATE EXTENSION IF NOT EXISTS btree_gist;

-- changeset ronal:1779026400000-2
CREATE INDEX idx_org_event_period_gist
    ON organization_event
    USING GIST (organization_id, tstzrange(start_at, end_at));

-- changeset ronal:1779026400000-3
CREATE INDEX idx_org_event_start_btree
    ON organization_event (organization_id, start_at);
