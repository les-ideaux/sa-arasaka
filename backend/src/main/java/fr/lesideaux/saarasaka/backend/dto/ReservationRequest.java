package fr.lesideaux.saarasaka.backend.dto;

import java.time.LocalDate;

public record ReservationRequest(
        LocalDate startDate,
        LocalDate endDate,
        Long parkingSpaceId,
        boolean needsElectricCharging
) {}