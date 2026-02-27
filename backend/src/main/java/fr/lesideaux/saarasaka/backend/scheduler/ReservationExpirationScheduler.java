package fr.lesideaux.saarasaka.backend.scheduler;

import fr.lesideaux.saarasaka.backend.service.ReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReservationExpirationScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReservationExpirationScheduler.class);

    private final ReservationService reservationService;

    public ReservationExpirationScheduler(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Scheduled(cron = "0 0 11 * * MON-FRI")
    public void expireUnconfirmedReservations() {
        int expired = reservationService.expireUnconfirmedReservations();
        log.info("Scheduler 11h : {} réservation(s) expirée(s) faute de check-in", expired);
    }
}