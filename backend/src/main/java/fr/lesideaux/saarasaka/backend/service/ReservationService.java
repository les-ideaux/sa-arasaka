package fr.lesideaux.saarasaka.backend.service;

import fr.lesideaux.saarasaka.backend.data.entity.ParkingSpaceEntity;
import fr.lesideaux.saarasaka.backend.data.entity.ReservationEntity;
import fr.lesideaux.saarasaka.backend.data.entity.UserEntity;
import fr.lesideaux.saarasaka.backend.data.repository.ParkingSpaceRepository;
import fr.lesideaux.saarasaka.backend.data.repository.ReservationRepository;
import fr.lesideaux.saarasaka.backend.data.repository.UserRepository;
import fr.lesideaux.saarasaka.backend.dto.ReservationRequest;
import fr.lesideaux.saarasaka.backend.dto.ReservationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ReservationService {

    private static final int MAX_DAYS_EMPLOYEE = 5;
    private static final int MAX_DAYS_MANAGER = 30;

    private final ReservationRepository reservationRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserRepository userRepository;

    public ReservationService(ReservationRepository reservationRepository,
                               ParkingSpaceRepository parkingSpaceRepository,
                               UserRepository userRepository) {
        this.reservationRepository = reservationRepository;
        this.parkingSpaceRepository = parkingSpaceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));

        // Validation des dates
        LocalDate today = LocalDate.now();
        if (request.startDate().isBefore(today)) {
            throw new IllegalArgumentException("La date de début ne peut pas être dans le passé");
        }
        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        // Validation durée selon rôle
        long days = ChronoUnit.DAYS.between(request.startDate(), request.endDate()) + 1;
        int maxDays = user.getRole() == UserEntity.Role.MANAGER ? MAX_DAYS_MANAGER : MAX_DAYS_EMPLOYEE;
        if (days > maxDays) {
            throw new IllegalArgumentException(
                "Durée maximum dépassée : " + maxDays + " jour(s) pour votre profil (" + days + " demandés)"
            );
        }

        // Validation de la place
        ParkingSpaceEntity space = parkingSpaceRepository.findById(request.parkingSpaceId())
                .orElseThrow(() -> new IllegalArgumentException("Place de parking introuvable"));

        if (request.needsElectricCharging() && !space.isEquippedWithElectricCharging()) {
            throw new IllegalArgumentException("Cette place n'est pas équipée d'une borne électrique");
        }

        // Vérification conflits sur la place
        if (!reservationRepository.findConflictingReservations(
                space.getId(), request.startDate(), request.endDate()).isEmpty()) {
            throw new IllegalStateException("Cette place est déjà réservée sur cette période");
        }

        // Vérification que l'user n'a pas déjà une place sur cette période
        if (!reservationRepository.findUserConflictingReservations(
                userId, request.startDate(), request.endDate()).isEmpty()) {
            throw new IllegalStateException("Vous avez déjà une réservation sur cette période");
        }

        ReservationEntity saved = reservationRepository.save(
                new ReservationEntity(request.startDate(), request.endDate(), space.getId(), userId)
        );

        return toResponse(saved, space, user);
    }

    @Transactional
    public ReservationResponse checkIn(Long reservationId, Long userId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));

        if (!reservation.getUserId().equals(userId)) {
            throw new SecurityException("Cette réservation ne vous appartient pas");
        }
        if (reservation.getStatus() != ReservationEntity.Status.PENDING) {
            throw new IllegalStateException("Le check-in n'est possible que sur une réservation en attente");
        }
        if (reservation.getStartDate().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Le check-in n'est possible que le jour de la réservation");
        }

        reservation.setStatus(ReservationEntity.Status.CONFIRMED);
        reservation.setCheckinTime(LocalDateTime.now());

        ParkingSpaceEntity space = parkingSpaceRepository.findById(reservation.getParkingSpaceId()).orElseThrow();
        UserEntity user = userRepository.findById(userId).orElseThrow();

        return toResponse(reservationRepository.save(reservation), space, user);
    }

    @Transactional
    public ReservationResponse cancelReservation(Long reservationId, Long userId) {
        ReservationEntity reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));

        UserEntity requester = userRepository.findById(userId).orElseThrow();
        boolean isSecretary = requester.getRole() == UserEntity.Role.SECRETARY;

        if (!isSecretary && !reservation.getUserId().equals(userId)) {
            throw new SecurityException("Vous ne pouvez pas annuler cette réservation");
        }
        if (reservation.getStatus() == ReservationEntity.Status.CONFIRMED) {
            throw new IllegalStateException("Impossible d'annuler une réservation déjà confirmée");
        }

        reservation.setStatus(ReservationEntity.Status.CANCELLED);

        ParkingSpaceEntity space = parkingSpaceRepository.findById(reservation.getParkingSpaceId()).orElseThrow();
        UserEntity owner = userRepository.findById(reservation.getUserId()).orElseThrow();

        return toResponse(reservationRepository.save(reservation), space, owner);
    }

    @Transactional
    public int expireUnconfirmedReservations() {
        List<ReservationEntity> pending = reservationRepository.findPendingReservationsForToday(LocalDate.now());
        pending.forEach(r -> r.setStatus(ReservationEntity.Status.EXPIRED));
        reservationRepository.saveAll(pending);
        return pending.size();
    }

    public List<ReservationResponse> getUserReservations(Long userId) {
        return reservationRepository.findByUserIdOrderByStartDateDesc(userId).stream()
                .map(r -> toResponse(r,
                        parkingSpaceRepository.findById(r.getParkingSpaceId()).orElseThrow(),
                        userRepository.findById(r.getUserId()).orElseThrow()))
                .toList();
    }

    public List<ReservationResponse> getAllActiveReservations() {
        return reservationRepository.findActiveReservations(LocalDate.now()).stream()
                .map(r -> toResponse(r,
                        parkingSpaceRepository.findById(r.getParkingSpaceId()).orElseThrow(),
                        userRepository.findById(r.getUserId()).orElseThrow()))
                .toList();
    }

    public List<ParkingSpaceEntity> getAvailableSpaces(LocalDate startDate, LocalDate endDate, boolean needsElectric) {
        List<ParkingSpaceEntity> spaces = needsElectric
                ? parkingSpaceRepository.findByIsEquippedWithElectricCharging(true)
                : parkingSpaceRepository.findAll();

        return spaces.stream()
                .filter(s -> reservationRepository.findConflictingReservations(
                        s.getId(), startDate, endDate).isEmpty())
                .toList();
    }

    private ReservationResponse toResponse(ReservationEntity r, ParkingSpaceEntity space, UserEntity user) {
        return new ReservationResponse(
                r.getId(),
                r.getStartDate(),
                r.getEndDate(),
                space.getId(),
                space.getRow() + space.getNumber(),
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                r.getStatus(),
                r.getCheckinTime()
        );
    }
}