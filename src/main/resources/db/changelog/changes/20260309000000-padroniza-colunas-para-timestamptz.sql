-- liquibase formatted sql

-- changeset ronal:20260309000000-1
-- Padroniza todas as colunas de data/hora para TIMESTAMP WITH TIME ZONE (TIMESTAMPTZ).
-- Colunas que usavam TIMESTAMP WITHOUT TIME ZONE são convertidas assumindo que os valores
-- existentes já estão em UTC.

-- organization
ALTER TABLE organization
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- organization_invite
ALTER TABLE organization_invite
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE organization_invite
    ALTER COLUMN expires_at TYPE TIMESTAMP WITH TIME ZONE;

-- organization_member
ALTER TABLE organization_member
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- organization_member_role
ALTER TABLE organization_member_role
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- organization_role
ALTER TABLE organization_role
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- user_auth_provider
ALTER TABLE user_auth_provider
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- users
ALTER TABLE users
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- organization_group
ALTER TABLE organization_group
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- organization_group_participant
ALTER TABLE organization_group_participant
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- organization_event
ALTER TABLE organization_event
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- organization_event_participant
ALTER TABLE organization_event_participant
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- organization_event_called_function
ALTER TABLE organization_event_called_function
    ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE organization_event_called_function
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- organization_event_called_group
ALTER TABLE organization_event_called_group
    ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE;

ALTER TABLE organization_event_called_group
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- organization_function
ALTER TABLE organization_function
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- organization_function_member
ALTER TABLE organization_function_member
    ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE;

-- changeset ronal:20260309000000-2
-- Remove o SET TIMEZONE da sessão que foi definido na primeira migration.
-- O timezone do banco deve ser UTC (padrão do PostgreSQL).
SET TIMEZONE = 'UTC';
