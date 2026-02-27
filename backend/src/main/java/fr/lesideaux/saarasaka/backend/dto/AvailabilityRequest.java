package fr.lesideaux.saarasaka.backend.dto;

import java.time.LocalDate;

public record AvailabilityRequest(
        LocalDate startDate,
        LocalDate endDate,
        boolean needsElectricCharging
) {}