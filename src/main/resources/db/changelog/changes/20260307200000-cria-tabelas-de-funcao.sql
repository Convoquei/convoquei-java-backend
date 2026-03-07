-- liquibase formatted sql

-- changeset ronal:20260307200000-1
CREATE TABLE organization_function
(
    id              UUID                                   NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    organization_id UUID                                   NOT NULL,
    name            VARCHAR(70)                            NOT NULL,
    emoji           VARCHAR(10),
    CONSTRAINT pk_organization_function PRIMARY KEY (id)
);

-- changeset ronal:20260307200000-2
CREATE TABLE organization_function_member
(
    id                         UUID                                   NOT NULL,
    created_at                 TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    updated_at                 TIMESTAMP WITHOUT TIME ZONE,
    organization_function_id   UUID                                   NOT NULL,
    organization_id            UUID                                   NOT NULL,
    organization_member_id     UUID                                   NOT NULL,
    joined_at                  TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_organization_function_member PRIMARY KEY (id)
);

-- changeset ronal:20260307200000-3
ALTER TABLE organization_function_member
    ADD CONSTRAINT uk_organization_function_member UNIQUE (organization_function_id, organization_member_id);

-- changeset ronal:20260307200000-4
ALTER TABLE organization_function
    ADD CONSTRAINT fk_organization_function_organization FOREIGN KEY (organization_id) REFERENCES organization (id);

-- changeset ronal:20260307200000-5
ALTER TABLE organization_function_member
    ADD CONSTRAINT fk_organization_function_member_function FOREIGN KEY (organization_function_id) REFERENCES organization_function (id);

-- changeset ronal:20260307200000-6
ALTER TABLE organization_function_member
    ADD CONSTRAINT fk_organization_function_member_member FOREIGN KEY (organization_member_id) REFERENCES organization_member (id);

-- changeset ronal:20260307200000-7
ALTER TABLE organization_function_member
    ADD CONSTRAINT fk_organization_function_member_organization FOREIGN KEY (organization_id) REFERENCES organization (id);
