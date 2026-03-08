-- liquibase formatted sql

-- changeset ronal:1772919155624-3
CREATE TABLE organization_event_called_function
(
    id          UUID                        NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE,
    event_id    UUID                        NOT NULL,
    function_id UUID                        NOT NULL,
    CONSTRAINT pk_organizationeventcalledfunction PRIMARY KEY (id),
    CONSTRAINT FK_ORGANIZATIONEVENTCALLEDFUNCTION_ON_FUNCTION FOREIGN KEY (function_id) REFERENCES organization_function (id),
    CONSTRAINT FK_ORGANIZATION_EVENT_PARTICIPANT_FUNCTION_EVENT FOREIGN KEY (event_id) REFERENCES organization_event (id)
);

-- changeset ronal:1772919155624-4
CREATE TABLE organization_event_called_group
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    event_id   UUID                        NOT NULL,
    group_id   UUID                        NOT NULL,
    CONSTRAINT pk_organizationeventcalledgroup PRIMARY KEY (id),
    CONSTRAINT FK_ORGANIZATION_EVENT_PARTICIPANT_GROUP_EVENT FOREIGN KEY (event_id) REFERENCES organization_event (id),
        CONSTRAINT FK_ORGANIZATION_EVENT_PARTICIPANT_GROUP_GROUP FOREIGN KEY (group_id) REFERENCES organization_group (id)
);

-- changeset ronal:1772919155624-5
ALTER TABLE organization_event
    ADD call_mode VARCHAR(30) NOT NULL;
ALTER TABLE organization_event
    ADD function_call_mode VARCHAR(30) NOT NULL;

