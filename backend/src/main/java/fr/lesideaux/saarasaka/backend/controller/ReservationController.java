package fr.lesideaux.saarasaka.backend.controller;

import fr.lesideaux.saarasaka.backend.data.entity.ParkingSpaceEntity;
import fr.lesideaux.saarasaka.backend.dto.AvailabilityRequest;
import fr.lesideaux.saarasaka.backend.dto.ReservationRequest;
import fr.lesideaux.saarasaka.backend.dto.ReservationResponse;
import fr.lesideaux.saarasaka.backend.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody ReservationRequest request) {
        try {
            return ResponseEntity.ok(reservationService.createReservation(request, getCurrentUserId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/checkin")
    public ResponseEntity<?> checkIn(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservationService.checkIn(id, getCurrentUserId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelReservation(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(reservationService.cancelReservation(id, getCurrentUserId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @GetMapping("/my")
    public List<ReservationResponse> getMyReservations() {
        return reservationService.getUserReservations(getCurrentUserId());
    }

    @GetMapping
    public List<ReservationResponse> getAllActiveReservations() {
        return reservationService.getAllActiveReservations();
    }

    @GetMapping("/available-spaces")
    public List<ParkingSpaceEntity> getAvailableSpaces(@ModelAttribute AvailabilityRequest request) {
        return reservationService.getAvailableSpaces(
                request.startDate(), request.endDate(), request.needsElectricCharging()
        );
    }

    @PostMapping("/checkin-by-space/{spaceLabel}")
    public ResponseEntity<?> checkInBySpaceLabel(@PathVariable String spaceLabel) {
        try {
            return ResponseEntity.ok(reservationService.checkInBySpaceLabel(spaceLabel, getCurrentUserId()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (SecurityException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (Long) auth.getPrincipal();
    }
}
