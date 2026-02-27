package fr.lesideaux.saarasaka.backend.dto;

import fr.lesideaux.saarasaka.backend.data.entity.ReservationEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        Long parkingSpaceId,
        String parkingSpaceLabel,
        Long userId,
        String userFullName,
        ReservationEntity.Status status,
        LocalDateTime checkinTime
) {}