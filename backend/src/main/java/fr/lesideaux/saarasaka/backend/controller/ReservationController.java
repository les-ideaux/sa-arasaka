package fr.lesideaux.saarasaka.backend.controller;

import fr.lesideaux.saarasaka.backend.data.entity.ReservationEntity;
import fr.lesideaux.saarasaka.backend.data.repository.ReservationRepository;
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
    public ReservationEntity createReservation(ReservationEntity reservation) {




        return reservationRepository.save(reservation);
    }
}
