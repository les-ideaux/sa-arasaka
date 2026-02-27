package fr.lesideaux.saarasaka.backend.data.entity;
import jakarta.persistence.GenerationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reservations")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Long parkingSpaceId;

    @Column(nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.PENDING;

    private LocalDateTime checkinTime;

    public enum Status {
        PENDING,    // Réservée, en attente de check-in
        CONFIRMED,  // Check-in effectué
        CANCELLED,  // Annulée manuellement
        EXPIRED     // Non confirmée avant 11h, libérée automatiquement
    }

    public ReservationEntity() {}

    public ReservationEntity(LocalDate startDate, LocalDate endDate, Long parkingSpaceId, Long userId) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.parkingSpaceId = parkingSpaceId;
        this.userId = userId;
        this.status = Status.PENDING;
    }
}