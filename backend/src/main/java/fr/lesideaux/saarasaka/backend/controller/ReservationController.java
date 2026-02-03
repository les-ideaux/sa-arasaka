package fr.lesideaux.saarasaka.backend.controller;

import fr.lesideaux.saarasaka.backend.data.entity.ReservationEntity;
import fr.lesideaux.saarasaka.backend.data.repository.ReservationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationRepository reservationRepository;

    public ReservationController(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @GetMapping
    public List<ReservationEntity> getAllReservations() {
        return reservationRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createReservation(ReservationEntity reservation) {
        if (reservation.getStartDate().after(reservation.getEndDate())) {
            return ResponseEntity.badRequest().body("Invalid date range: Start date must be before end date.");
        }

        if (reservation.getParkingSpaceId() == null) {
            return ResponseEntity.badRequest().body("Invalid reservation: Parking space ID must be provided.");
        }

        if (reservation.getStartDate().before(new java.util.Date())) {
            return ResponseEntity.badRequest().body("Invalid start date: Start date cannot be in the past.");
        }

        if (reservation.getEndDate().before(new java.util.Date())) {
            return ResponseEntity.badRequest().body("Invalid end date: End date cannot be in the past.");
        }

        List<ReservationEntity> conflictingReservations = reservationRepository.findByParkingSpaceIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                reservation.getParkingSpaceId(),
                reservation.getStartDate(),
                reservation.getEndDate()
        );

        if (!conflictingReservations.isEmpty()) {
            return ResponseEntity.status(409).body("Conflict: The parking space is already reserved for the selected time period.");
        }

        return ResponseEntity.ok(reservationRepository.save(reservation));
    }
}
