-- liquibase formatted sql

-- changeset ronal:1772833550168-3
CREATE TABLE organization_event
(
    id                                        UUID                                   NOT NULL,
    created_at                                TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at                                TIMESTAMP WITHOUT TIME ZONE,
    organization_id                           UUID                                   NOT NULL,
    name                                      VARCHAR(70)                            NOT NULL,
    description                               VARCHAR(2000)                          NOT NULL,
    location                                  VARCHAR(250)                           NOT NULL,
    start_at                                  TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    end_at                                    TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    availability_lock_before_start_in_minutes INTEGER                                NOT NULL,
    allow_assignments                         BOOLEAN                                NOT NULL,
    assignment_mode                           VARCHAR(30)                            NOT NULL,
    status                                    VARCHAR(30)                            NOT NULL,
    CONSTRAINT pk_organization_event PRIMARY KEY (id),
    CONSTRAINT FK_ORGANIZATION_EVENT_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id)
);

-- changeset ronal:1772833550168-4
CREATE TABLE organization_event_participant
(
    id                     UUID                                   NOT NULL,
    created_at             TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at             TIMESTAMP WITHOUT TIME ZONE,
    organization_event_id  UUID                                   NOT NULL,
    organization_member_id UUID                                   NOT NULL,
    display_name_snapshot  VARCHAR(70),
    avatar_url_snapshot    VARCHAR(255),
    availability_status    VARCHAR(20)                            NOT NULL,
    source_group_id        UUID,
    CONSTRAINT pk_organization_event_participant PRIMARY KEY (id),
    CONSTRAINT FK_EVENT_PARTICIPANT_SOURCE_GROUP FOREIGN KEY (source_group_id) REFERENCES organization_group (id),
    CONSTRAINT FK_ORGANIZATION_EVENT_PARTICIPANT_EVENT FOREIGN KEY (organization_event_id) REFERENCES organization_event (id),
        CONSTRAINT FK_ORGANIZATION_EVENT_PARTICIPANT_MEMBER FOREIGN KEY (organization_member_id) REFERENCES organization_member (id)
);

