package fr.lesideaux.saarasaka.backend.service;

import fr.lesideaux.saarasaka.backend.data.entity.ParkingSpaceEntity;
import fr.lesideaux.saarasaka.backend.data.entity.ReservationEntity;
import fr.lesideaux.saarasaka.backend.data.entity.UserEntity;
import fr.lesideaux.saarasaka.backend.data.repository.ParkingSpaceRepository;
import fr.lesideaux.saarasaka.backend.data.repository.ReservationRepository;
import fr.lesideaux.saarasaka.backend.data.repository.UserRepository;
import fr.lesideaux.saarasaka.backend.dto.ReservationRequest;
import fr.lesideaux.saarasaka.backend.dto.ReservationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private ParkingSpaceRepository parkingSpaceRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ReservationService reservationService;

    private UserEntity employee;
    private UserEntity manager;
    private UserEntity secretary;
    private ParkingSpaceEntity normalSpace;
    private ParkingSpaceEntity electricSpace;

    @BeforeEach
    void setUp() {
        employee  = makeUser(1L, UserEntity.Role.EMPLOYEE);
        manager   = makeUser(2L, UserEntity.Role.MANAGER);
        secretary = makeUser(3L, UserEntity.Role.SECRETARY);
        normalSpace   = makeSpace(10L, "B", "01", false);
        electricSpace = makeSpace(11L, "A", "01", true);
    }

    // -----------------------------------------------------------------------
    // Règle : durée max selon le rôle
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Employee peut réserver jusqu'à 5 jours")
    void employee_canReserveUpTo5Days() {
        mockUserAndSpace(employee, normalSpace);
        mockNoConflicts();

        ReservationEntity saved = makeReservation(today(), today().plusDays(4), normalSpace.getId(), employee.getId());
        when(reservationRepository.save(any())).thenReturn(saved);

        ReservationResponse response = reservationService.createReservation(
                new ReservationRequest(today(), today().plusDays(4), normalSpace.getId(), false),
                employee.getId()
        );

        assertThat(response).isNotNull();
        assertThat(response.parkingSpaceId()).isEqualTo(normalSpace.getId());
    }

    @Test
    @DisplayName("Employee ne peut pas réserver plus de 5 jours")
    void employee_cannotReserveMoreThan5Days() {
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> reservationService.createReservation(
                new ReservationRequest(today(), today().plusDays(5), normalSpace.getId(), false),
                employee.getId()
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Durée maximum dépassée");
    }

    @Test
    @DisplayName("Manager peut réserver jusqu'à 30 jours")
    void manager_canReserveUpTo30Days() {
        mockUserAndSpace(manager, normalSpace);
        mockNoConflicts();

        ReservationEntity saved = makeReservation(today(), today().plusDays(29), normalSpace.getId(), manager.getId());
        when(reservationRepository.save(any())).thenReturn(saved);

        ReservationResponse response = reservationService.createReservation(
                new ReservationRequest(today(), today().plusDays(29), normalSpace.getId(), false),
                manager.getId()
        );

        assertThat(response).isNotNull();
    }

    @Test
    @DisplayName("Manager ne peut pas réserver plus de 30 jours")
    void manager_cannotReserveMoreThan30Days() {
        when(userRepository.findById(manager.getId())).thenReturn(Optional.of(manager));

        assertThatThrownBy(() -> reservationService.createReservation(
                new ReservationRequest(today(), today().plusDays(30), normalSpace.getId(), false),
                manager.getId()
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Durée maximum dépassée");
    }

    // -----------------------------------------------------------------------
    // Règle : pas de double réservation sur la même place
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Impossible de réserver une place déjà occupée sur la période")
    void cannotReserveAlreadyBookedSpace() {
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(parkingSpaceRepository.findById(normalSpace.getId())).thenReturn(Optional.of(normalSpace));
        when(reservationRepository.findConflictingReservations(any(), any(), any()))
                .thenReturn(List.of(new ReservationEntity()));

        assertThatThrownBy(() -> reservationService.createReservation(
                new ReservationRequest(today(), today().plusDays(1), normalSpace.getId(), false),
                employee.getId()
        ))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("déjà réservée");
    }

    // -----------------------------------------------------------------------
    // Règle : un utilisateur ne peut pas avoir deux réservations en même temps
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Un utilisateur ne peut pas avoir deux réservations qui se chevauchent")
    void userCannotHaveTwoOverlappingReservations() {
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(parkingSpaceRepository.findById(normalSpace.getId())).thenReturn(Optional.of(normalSpace));
        when(reservationRepository.findConflictingReservations(any(), any(), any())).thenReturn(List.of());
        when(reservationRepository.findUserConflictingReservations(any(), any(), any()))
                .thenReturn(List.of(new ReservationEntity()));

        assertThatThrownBy(() -> reservationService.createReservation(
                new ReservationRequest(today(), today().plusDays(1), normalSpace.getId(), false),
                employee.getId()
        ))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("déjà une réservation");
    }

    // -----------------------------------------------------------------------
    // Règle : borne électrique
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Impossible de demander une borne sur une place sans chargeur")
    void cannotRequestElectricOnNonElectricSpace() {
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(parkingSpaceRepository.findById(normalSpace.getId())).thenReturn(Optional.of(normalSpace));

        assertThatThrownBy(() -> reservationService.createReservation(
                new ReservationRequest(today(), today().plusDays(1), normalSpace.getId(), true),
                employee.getId()
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("borne électrique");
    }

    @Test
    @DisplayName("On peut réserver une place électrique avec demande de borne")
    void canReserveElectricSpace() {
        mockUserAndSpace(employee, electricSpace);
        mockNoConflicts();

        ReservationEntity saved = makeReservation(today(), today().plusDays(1), electricSpace.getId(), employee.getId());
        when(reservationRepository.save(any())).thenReturn(saved);

        ReservationResponse response = reservationService.createReservation(
                new ReservationRequest(today(), today().plusDays(1), electricSpace.getId(), true),
                employee.getId()
        );

        assertThat(response).isNotNull();
    }

    // -----------------------------------------------------------------------
    // Règle : dates invalides
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Impossible de réserver dans le passé")
    void cannotReserveInThePast() {
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> reservationService.createReservation(
                new ReservationRequest(today().minusDays(1), today(), normalSpace.getId(), false),
                employee.getId()
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("passé");
    }

    @Test
    @DisplayName("La date de fin doit être après la date de début")
    void endDateMustBeAfterStartDate() {
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> reservationService.createReservation(
                new ReservationRequest(today().plusDays(3), today().plusDays(1), normalSpace.getId(), false),
                employee.getId()
        ))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("après la date de début");
    }

    // -----------------------------------------------------------------------
    // Règle : check-in
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Check-in valide le jour de la réservation")
    void checkIn_succeedsOnReservationDay() {
        ReservationEntity reservation = makeReservation(today(), today().plusDays(1), normalSpace.getId(), employee.getId());
        reservation.setStatus(ReservationEntity.Status.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(parkingSpaceRepository.findById(any())).thenReturn(Optional.of(normalSpace));
        when(userRepository.findById(any())).thenReturn(Optional.of(employee));

        ReservationResponse response = reservationService.checkIn(1L, employee.getId());

        assertThat(response.status()).isEqualTo(ReservationEntity.Status.CONFIRMED);
    }

    @Test
    @DisplayName("Check-in impossible si réservation déjà confirmée")
    void checkIn_failsIfAlreadyConfirmed() {
        ReservationEntity reservation = makeReservation(today(), today().plusDays(1), normalSpace.getId(), employee.getId());
        reservation.setStatus(ReservationEntity.Status.CONFIRMED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.checkIn(1L, employee.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("en attente");
    }

    @Test
    @DisplayName("Check-in impossible si la réservation appartient à un autre utilisateur")
    void checkIn_failsIfWrongUser() {
        ReservationEntity reservation = makeReservation(today(), today().plusDays(1), normalSpace.getId(), 99L);
        reservation.setStatus(ReservationEntity.Status.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.checkIn(1L, employee.getId()))
                .isInstanceOf(SecurityException.class);
    }

    // -----------------------------------------------------------------------
    // Règle : annulation
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Un employee peut annuler sa propre réservation PENDING")
    void employee_canCancelOwnReservation() {
        ReservationEntity reservation = makeReservation(today(), today().plusDays(1), normalSpace.getId(), employee.getId());
        reservation.setStatus(ReservationEntity.Status.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(parkingSpaceRepository.findById(any())).thenReturn(Optional.of(normalSpace));

        ReservationResponse response = reservationService.cancelReservation(1L, employee.getId());

        assertThat(response.status()).isEqualTo(ReservationEntity.Status.CANCELLED);
    }

    @Test
    @DisplayName("Un employee ne peut pas annuler la réservation d'un autre")
    void employee_cannotCancelOtherReservation() {
        ReservationEntity reservation = makeReservation(today(), today().plusDays(1), normalSpace.getId(), 99L);
        reservation.setStatus(ReservationEntity.Status.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> reservationService.cancelReservation(1L, employee.getId()))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    @DisplayName("Un secretary peut annuler n'importe quelle réservation")
    void secretary_canCancelAnyReservation() {
        ReservationEntity reservation = makeReservation(today(), today().plusDays(1), normalSpace.getId(), employee.getId());
        reservation.setStatus(ReservationEntity.Status.PENDING);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(userRepository.findById(secretary.getId())).thenReturn(Optional.of(secretary));
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(reservationRepository.save(any())).thenReturn(reservation);
        when(parkingSpaceRepository.findById(any())).thenReturn(Optional.of(normalSpace));

        ReservationResponse response = reservationService.cancelReservation(1L, secretary.getId());

        assertThat(response.status()).isEqualTo(ReservationEntity.Status.CANCELLED);
    }

    @Test
    @DisplayName("Impossible d'annuler une réservation déjà confirmée par check-in")
    void cannotCancelConfirmedReservation() {
        ReservationEntity reservation = makeReservation(today(), today().plusDays(1), normalSpace.getId(), employee.getId());
        reservation.setStatus(ReservationEntity.Status.CONFIRMED);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));
        when(userRepository.findById(employee.getId())).thenReturn(Optional.of(employee));

        assertThatThrownBy(() -> reservationService.cancelReservation(1L, employee.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("confirmée");
    }

    // -----------------------------------------------------------------------
    // Règle : expiration scheduler 11h
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("Le scheduler expire les réservations PENDING du jour sans check-in")
    void scheduler_expiresPendingReservations() {
        ReservationEntity r1 = makeReservation(today(), today(), normalSpace.getId(), employee.getId());
        ReservationEntity r2 = makeReservation(today(), today(), electricSpace.getId(), manager.getId());
        r1.setStatus(ReservationEntity.Status.PENDING);
        r2.setStatus(ReservationEntity.Status.PENDING);

        when(reservationRepository.findPendingReservationsForToday(today())).thenReturn(List.of(r1, r2));
        when(reservationRepository.saveAll(any())).thenReturn(List.of(r1, r2));

        int expired = reservationService.expireUnconfirmedReservations();

        assertThat(expired).isEqualTo(2);
        assertThat(r1.getStatus()).isEqualTo(ReservationEntity.Status.EXPIRED);
        assertThat(r2.getStatus()).isEqualTo(ReservationEntity.Status.EXPIRED);
    }

    @Test
    @DisplayName("Le scheduler ne fait rien s'il n'y a pas de réservations PENDING")
    void scheduler_doesNothingWhenNoPendingReservations() {
        when(reservationRepository.findPendingReservationsForToday(today())).thenReturn(List.of());
        when(reservationRepository.saveAll(any())).thenReturn(List.of());

        int expired = reservationService.expireUnconfirmedReservations();

        assertThat(expired).isEqualTo(0);
        verify(reservationRepository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // Helpers
    // -----------------------------------------------------------------------

    private LocalDate today() {
        return LocalDate.now();
    }

    private UserEntity makeUser(Long id, UserEntity.Role role) {
        UserEntity u = new UserEntity("test@test.com", "First", "Last", "pass", role);
        // On force l'id via réflexion car il est géré par la BDD normalement
        try {
            var field = UserEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(u, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return u;
    }

    private ParkingSpaceEntity makeSpace(Long id, String row, String number, boolean electric) {
        ParkingSpaceEntity s = new ParkingSpaceEntity(row, number, electric, false, false);
        try {
            var field = ParkingSpaceEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(s, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return s;
    }

    private ReservationEntity makeReservation(LocalDate start, LocalDate end, Long spaceId, Long userId) {
        return new ReservationEntity(start, end, spaceId, userId);
    }

    private void mockUserAndSpace(UserEntity user, ParkingSpaceEntity space) {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(parkingSpaceRepository.findById(space.getId())).thenReturn(Optional.of(space));
    }

    private void mockNoConflicts() {
        when(reservationRepository.findConflictingReservations(any(), any(), any())).thenReturn(List.of());
        when(reservationRepository.findUserConflictingReservations(any(), any(), any())).thenReturn(List.of());
    }
}