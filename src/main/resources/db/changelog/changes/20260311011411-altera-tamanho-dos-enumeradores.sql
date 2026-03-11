-- liquibase formatted sql

-- changeset ronal:1773202451330-1
ALTER TABLE organization_event
    ALTER COLUMN assignment_mode TYPE VARCHAR(50) USING (assignment_mode::VARCHAR(50));

-- changeset ronal:1773202451330-2
ALTER TABLE organization_event_called_function
    ALTER COLUMN assignment_mode TYPE VARCHAR(50) USING (assignment_mode::VARCHAR(50));

-- changeset ronal:1773202451330-3
ALTER TABLE organization_event_participant
    ALTER COLUMN availability_status TYPE VARCHAR(50) USING (availability_status::VARCHAR(50));

-- changeset ronal:1773202451330-4
ALTER TABLE organization_event
    ALTER COLUMN call_mode TYPE VARCHAR(50) USING (call_mode::VARCHAR(50));

-- changeset ronal:1773202451330-5
ALTER TABLE organization_event
    ALTER COLUMN function_call_mode TYPE VARCHAR(50) USING (function_call_mode::VARCHAR(50));

-- changeset ronal:1773202451330-6
ALTER TABLE organization
    ALTER COLUMN status TYPE VARCHAR(50) USING (status::VARCHAR(50));

-- changeset ronal:1773202451330-7
ALTER TABLE organization_event
    ALTER COLUMN status TYPE VARCHAR(50) USING (status::VARCHAR(50));

-- changeset ronal:1773202451330-8
ALTER TABLE organization_event_participant
    ALTER COLUMN status TYPE VARCHAR(50) USING (status::VARCHAR(50));

-- changeset ronal:1773202451330-9
ALTER TABLE organization_member
    ALTER COLUMN status TYPE VARCHAR(50) USING (status::VARCHAR(50));

