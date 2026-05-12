package br.com.convoquei.backend.core.organization.event.valueobject;

import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventAssignmentMode;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventCallMode;
import br.com.convoquei.backend.core.organization.event.enums.OrganizationEventFunctionCallMode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class OrganizationEventConfiguration {
    @Column(name = "name", nullable = false, length = 70)
    private String name;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "location", nullable = false, length = 250)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "assignment_mode", nullable = false, length = 50)
    private OrganizationEventAssignmentMode assignmentMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "call_mode", nullable = false, length = 50)
    private OrganizationEventCallMode callMode;

    @Enumerated(EnumType.STRING)
    @Column(name = "function_call_mode", nullable = true, length = 50)
    private OrganizationEventFunctionCallMode functionCallMode;

    @Column(name = "availability_lock_before_start_in_minutes", nullable = false)
    private int availabilityLockBeforeStartInMinutes;

    protected OrganizationEventConfiguration() {}

    public OrganizationEventConfiguration(
            String name,
            String description,
            String location,
            OrganizationEventAssignmentMode assignmentMode,
            OrganizationEventCallMode callMode,
            OrganizationEventFunctionCallMode functionCallMode,
            int availabilityLockBeforeStartInMinutes
    ) {
        this.name = name;
        this.description = description;
        this.location = location;
        this.assignmentMode = assignmentMode;
        this.callMode = callMode;
        this.functionCallMode = functionCallMode;
        this.availabilityLockBeforeStartInMinutes = availabilityLockBeforeStartInMinutes;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public OrganizationEventAssignmentMode getAssignmentMode() {
        return assignmentMode;
    }

    public OrganizationEventCallMode getCallMode() {
        return callMode;
    }

    public OrganizationEventFunctionCallMode getFunctionCallMode() {
        return functionCallMode;
    }

    public int getAvailabilityLockBeforeStartInMinutes() {
        return availabilityLockBeforeStartInMinutes;
    }

    public void setAvailabilityLockBeforeStartInMinutes(int availabilityLockBeforeStartInMinutes) {
        this.availabilityLockBeforeStartInMinutes = availabilityLockBeforeStartInMinutes;
    }
}
