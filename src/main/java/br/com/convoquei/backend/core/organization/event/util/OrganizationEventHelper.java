package br.com.convoquei.backend.core.organization.event.util;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public final class OrganizationEventHelper {

    private OrganizationEventHelper() {
    }

    public static long secondsUntilAvailabilityLock(OffsetDateTime startAt, Integer lockBeforeStartInMinutes) {
        if (lockBeforeStartInMinutes == null || lockBeforeStartInMinutes <= 0) return 0L;
        return Math.max(0L, OffsetDateTime.now().until(
                startAt.minusMinutes(lockBeforeStartInMinutes),
                ChronoUnit.SECONDS));
    }
}
