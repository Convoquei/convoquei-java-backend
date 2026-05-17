package br.com.convoquei.backend.core.organization.member.api.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public class MemberAvailabilityRequest {

    @NotNull(message = "start é obrigatório")
    private OffsetDateTime start;

    @NotNull(message = "end é obrigatório")
    private OffsetDateTime end;

    @AssertTrue(message = "end deve ser posterior a start")
    public boolean isEndAfterStart() {
        return start == null || end == null || end.isAfter(start);
    }

    public OffsetDateTime getStart() { return start; }
    public void setStart(OffsetDateTime start) { this.start = start; }

    public OffsetDateTime getEnd() { return end; }
    public void setEnd(OffsetDateTime end) { this.end = end; }
}
