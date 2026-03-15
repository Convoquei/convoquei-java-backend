-- liquibase formatted sql

-- changeset ronal:1773535894629-1
CREATE TABLE organization
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    name       VARCHAR(70)                 NOT NULL,
    slug       VARCHAR(150)                NOT NULL,
    photo_url  TEXT                        NOT NULL,
    status     VARCHAR(50)                 NOT NULL,
    CONSTRAINT pk_organization PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-2
CREATE TABLE organization_event
(
    id                                        UUID                        NOT NULL,
    created_at                                TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at                                TIMESTAMP WITHOUT TIME ZONE,
    organization_id                           UUID                        NOT NULL,
    created_by_member_id                      UUID,
    cancelled_by_member_id                    UUID,
    start_at                                  TIMESTAMP WITH TIME ZONE    NOT NULL,
    end_at                                    TIMESTAMP WITH TIME ZONE    NOT NULL,
    allow_assignments                         BOOLEAN                     NOT NULL,
    status                                    VARCHAR(50)                 NOT NULL,
    name                                      VARCHAR(70)                 NOT NULL,
    description                               VARCHAR(2000)               NOT NULL,
    location                                  VARCHAR(250)                NOT NULL,
    assignment_mode                           VARCHAR(50)                 NOT NULL,
    call_mode                                 VARCHAR(50)                 NOT NULL,
    function_call_mode                        VARCHAR(50),
    availability_lock_before_start_in_minutes INTEGER                     NOT NULL,
    CONSTRAINT pk_organization_event PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-3
CREATE TABLE organization_event_called_function
(
    id              UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    min_members     INTEGER                     NOT NULL,
    max_members     INTEGER,
    assignment_mode VARCHAR(50)                 NOT NULL,
    event_id        UUID                        NOT NULL,
    function_id     UUID                        NOT NULL,
    CONSTRAINT pk_organizationeventcalledfunction PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-4
CREATE TABLE organization_event_called_group
(
    id         UUID                        NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    event_id   UUID                        NOT NULL,
    group_id   UUID                        NOT NULL,
    CONSTRAINT pk_organizationeventcalledgroup PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-5
CREATE TABLE organization_event_participant
(
    id                       UUID                        NOT NULL,
    created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITHOUT TIME ZONE,
    organization_event_id    UUID                        NOT NULL,
    organization_member_id   UUID                        NOT NULL,
    organization_function_id UUID,
    display_name_snapshot    VARCHAR(70),
    avatar_url_snapshot      VARCHAR(255),
    status                   VARCHAR(50)                 NOT NULL,
    availability_status      VARCHAR(50)                 NOT NULL,
    CONSTRAINT pk_organization_event_participant PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-6
CREATE TABLE organization_function
(
    id              UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    organization_id UUID                        NOT NULL,
    name            VARCHAR(70)                 NOT NULL,
    emoji           VARCHAR(10),
    CONSTRAINT pk_organization_function PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-7
CREATE TABLE organization_function_member
(
    id                       UUID                        NOT NULL,
    created_at               TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at               TIMESTAMP WITHOUT TIME ZONE,
    organization_function_id UUID                        NOT NULL,
    organization_id          UUID                        NOT NULL,
    organization_member_id   UUID                        NOT NULL,
    joined_at                TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_organization_function_member PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-8
CREATE TABLE organization_group
(
    id              UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    organization_id UUID                        NOT NULL,
    name            VARCHAR(70)                 NOT NULL,
    emoji           VARCHAR(10),
    CONSTRAINT pk_organization_group PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-9
CREATE TABLE organization_group_participant
(
    id                     UUID                        NOT NULL,
    created_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at             TIMESTAMP WITHOUT TIME ZONE,
    organization_group_id  UUID                        NOT NULL,
    organization_id        UUID                        NOT NULL,
    organization_member_id UUID                        NOT NULL,
    joined_at              TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_organization_group_participant PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-10
CREATE TABLE organization_invite
(
    id              UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    organization_id UUID                        NOT NULL,
    invited_by      UUID                        NOT NULL,
    invited_email   VARCHAR(150)                NOT NULL,
    expires_at      TIMESTAMP WITH TIME ZONE    NOT NULL,
    CONSTRAINT pk_organization_invite PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-11
CREATE TABLE organization_member
(
    id              UUID                        NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    organization_id UUID                        NOT NULL,
    user_id         UUID                        NOT NULL,
    status          VARCHAR(50)                 NOT NULL,
    joined_at       TIMESTAMP WITHOUT TIME ZONE,
    left_at         TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_organization_member PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-12
CREATE TABLE organization_member_role
(
    id                     UUID                        NOT NULL,
    created_at             TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at             TIMESTAMP WITHOUT TIME ZONE,
    organization_id        UUID                        NOT NULL,
    organization_role_id   UUID                        NOT NULL,
    organization_member_id UUID                        NOT NULL,
    CONSTRAINT pk_organization_member_role PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-13
CREATE TABLE organization_role
(
    id               UUID                        NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    organization_id  UUID                        NOT NULL,
    name             VARCHAR(80)                 NOT NULL,
    permissions_mask BIGINT                      NOT NULL,
    system_key       VARCHAR(50),
    CONSTRAINT pk_organization_role PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-14
CREATE TABLE user_auth_provider
(
    id               UUID                        NOT NULL,
    created_at       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITHOUT TIME ZONE,
    user_id          UUID                        NOT NULL,
    provider         VARCHAR(50)                 NOT NULL,
    provider_user_id VARCHAR(200)                NOT NULL,
    email            VARCHAR(150),
    CONSTRAINT pk_user_auth_provider PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-15
CREATE TABLE users
(
    id                UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE,
    name              VARCHAR(70)                 NOT NULL,
    email             VARCHAR(150)                NOT NULL,
    password_hash     TEXT,
    avatar_url        TEXT                        NOT NULL,
    is_email_verified BOOLEAN                     NOT NULL,
    status            VARCHAR(255)                NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset ronal:1773535894629-16
ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

-- changeset ronal:1773535894629-17
ALTER TABLE organization_function_member
    ADD CONSTRAINT uk_organization_function_member UNIQUE (organization_function_id, organization_member_id);

-- changeset ronal:1773535894629-18
ALTER TABLE organization_group_participant
    ADD CONSTRAINT uk_organization_group_participant UNIQUE (organization_group_id, organization_member_id);

-- changeset ronal:1773535894629-19
ALTER TABLE organization_invite
    ADD CONSTRAINT uk_organization_invite_organization_email UNIQUE (organization_id, invited_email);

-- changeset ronal:1773535894629-20
ALTER TABLE organization_member
    ADD CONSTRAINT uk_organization_member_organization_user UNIQUE (organization_id, user_id);

-- changeset ronal:1773535894629-21
ALTER TABLE organization_member_role
    ADD CONSTRAINT uk_organization_member_role UNIQUE (organization_member_id, organization_role_id);

-- changeset ronal:1773535894629-22
ALTER TABLE organization
    ADD CONSTRAINT uk_organization_slug UNIQUE (slug);

-- changeset ronal:1773535894629-23
ALTER TABLE user_auth_provider
    ADD CONSTRAINT uk_provider_provider_user_id UNIQUE (provider, provider_user_id);

-- changeset ronal:1773535894629-24
ALTER TABLE user_auth_provider
    ADD CONSTRAINT uk_user_provider UNIQUE (user_id, provider);

-- changeset ronal:1773535894629-26
ALTER TABLE organization_event_called_function
    ADD CONSTRAINT FK_ORGANIZATIONEVENTCALLEDFUNCTION_ON_FUNCTION FOREIGN KEY (function_id) REFERENCES organization_function (id);

-- changeset ronal:1773535894629-27
ALTER TABLE organization_event
    ADD CONSTRAINT FK_ORGANIZATION_EVENT_CANCELLED_BY_MEMBER FOREIGN KEY (cancelled_by_member_id) REFERENCES organization_member (id);

-- changeset ronal:1773535894629-28
ALTER TABLE organization_event
    ADD CONSTRAINT FK_ORGANIZATION_EVENT_CREATED_BY_MEMBER FOREIGN KEY (created_by_member_id) REFERENCES organization_member (id);

-- changeset ronal:1773535894629-29
ALTER TABLE organization_event
    ADD CONSTRAINT FK_ORGANIZATION_EVENT_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id);

-- changeset ronal:1773535894629-30
ALTER TABLE organization_event_participant
    ADD CONSTRAINT FK_ORGANIZATION_EVENT_PARTICIPANT_EVENT FOREIGN KEY (organization_event_id) REFERENCES organization_event (id);

-- changeset ronal:1773535894629-31
ALTER TABLE organization_event_participant
    ADD CONSTRAINT FK_ORGANIZATION_EVENT_PARTICIPANT_FUNCTION FOREIGN KEY (organization_function_id) REFERENCES organization_function (id);

-- changeset ronal:1773535894629-32
ALTER TABLE organization_event_called_function
    ADD CONSTRAINT FK_ORGANIZATION_EVENT_PARTICIPANT_FUNCTION_EVENT FOREIGN KEY (event_id) REFERENCES organization_event (id);

-- changeset ronal:1773535894629-33
ALTER TABLE organization_event_called_group
    ADD CONSTRAINT FK_ORGANIZATION_EVENT_PARTICIPANT_GROUP_EVENT FOREIGN KEY (event_id) REFERENCES organization_event (id);

-- changeset ronal:1773535894629-34
ALTER TABLE organization_event_called_group
    ADD CONSTRAINT FK_ORGANIZATION_EVENT_PARTICIPANT_GROUP_GROUP FOREIGN KEY (group_id) REFERENCES organization_group (id);

-- changeset ronal:1773535894629-35
ALTER TABLE organization_event_participant
    ADD CONSTRAINT FK_ORGANIZATION_EVENT_PARTICIPANT_MEMBER FOREIGN KEY (organization_member_id) REFERENCES organization_member (id);

-- changeset ronal:1773535894629-36
ALTER TABLE organization_function_member
    ADD CONSTRAINT FK_ORGANIZATION_FUNCTION_MEMBER_FUNCTION FOREIGN KEY (organization_function_id) REFERENCES organization_function (id);

-- changeset ronal:1773535894629-37
ALTER TABLE organization_function_member
    ADD CONSTRAINT FK_ORGANIZATION_FUNCTION_MEMBER_MEMBER FOREIGN KEY (organization_member_id) REFERENCES organization_member (id);

-- changeset ronal:1773535894629-38
ALTER TABLE organization_function_member
    ADD CONSTRAINT FK_ORGANIZATION_FUNCTION_MEMBER_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id);

-- changeset ronal:1773535894629-39
ALTER TABLE organization_function
    ADD CONSTRAINT FK_ORGANIZATION_FUNCTION_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id);

-- changeset ronal:1773535894629-40
ALTER TABLE organization_group
    ADD CONSTRAINT FK_ORGANIZATION_GROUP_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id);

-- changeset ronal:1773535894629-41
ALTER TABLE organization_group_participant
    ADD CONSTRAINT FK_ORGANIZATION_GROUP_PARTICIPANT_GROUP FOREIGN KEY (organization_group_id) REFERENCES organization_group (id);

-- changeset ronal:1773535894629-42
ALTER TABLE organization_group_participant
    ADD CONSTRAINT FK_ORGANIZATION_GROUP_PARTICIPANT_MEMBER FOREIGN KEY (organization_member_id) REFERENCES organization_member (id);

-- changeset ronal:1773535894629-43
ALTER TABLE organization_group_participant
    ADD CONSTRAINT FK_ORGANIZATION_GROUP_PARTICIPANT_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id);

-- changeset ronal:1773535894629-44
ALTER TABLE organization_invite
    ADD CONSTRAINT FK_ORGANIZATION_INVITE_INVITED_BY_MEMBER FOREIGN KEY (invited_by) REFERENCES organization_member (id);

-- changeset ronal:1773535894629-45
ALTER TABLE organization_invite
    ADD CONSTRAINT FK_ORGANIZATION_INVITE_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id);

-- changeset ronal:1773535894629-46
ALTER TABLE organization_member
    ADD CONSTRAINT FK_ORGANIZATION_MEMBER_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id);

-- changeset ronal:1773535894629-47
ALTER TABLE organization_member_role
    ADD CONSTRAINT FK_ORGANIZATION_MEMBER_ROLE_MEMBER FOREIGN KEY (organization_member_id) REFERENCES organization_member (id);

-- changeset ronal:1773535894629-48
ALTER TABLE organization_member_role
    ADD CONSTRAINT FK_ORGANIZATION_MEMBER_ROLE_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id);

-- changeset ronal:1773535894629-49
ALTER TABLE organization_member_role
    ADD CONSTRAINT FK_ORGANIZATION_MEMBER_ROLE_ROLE FOREIGN KEY (organization_role_id) REFERENCES organization_role (id);

-- changeset ronal:1773535894629-50
ALTER TABLE organization_member
    ADD CONSTRAINT FK_ORGANIZATION_MEMBER_USER FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset ronal:1773535894629-51
ALTER TABLE organization_role
    ADD CONSTRAINT FK_ORGANIZATION_ROLE_ORGANIZATION FOREIGN KEY (organization_id) REFERENCES organization (id);

-- changeset ronal:1773535894629-52
ALTER TABLE user_auth_provider
    ADD CONSTRAINT FK_USER_AUTH_PROVIDERS_USER FOREIGN KEY (user_id) REFERENCES users (id);

