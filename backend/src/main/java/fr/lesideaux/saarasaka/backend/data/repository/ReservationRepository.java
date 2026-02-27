package fr.lesideaux.saarasaka.backend.data.repository;

import fr.lesideaux.saarasaka.backend.data.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByUserId(Long userId);

    @Query("SELECT r FROM ReservationEntity r WHERE r.parkingSpaceId = :spaceId " +
           "AND r.status NOT IN ('CANCELLED', 'EXPIRED') " +
           "AND r.startDate <= :endDate AND r.endDate >= :startDate")
    List<ReservationEntity> findConflictingReservations(
            @Param("spaceId") Long spaceId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT r FROM ReservationEntity r WHERE r.userId = :userId " +
           "AND r.status NOT IN ('CANCELLED', 'EXPIRED') " +
           "AND r.startDate <= :endDate AND r.endDate >= :startDate")
    List<ReservationEntity> findUserConflictingReservations(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT r FROM ReservationEntity r WHERE r.status = 'PENDING' AND r.startDate = :today")
    List<ReservationEntity> findPendingReservationsForToday(@Param("today") LocalDate today);

    List<ReservationEntity> findByUserIdOrderByStartDateDesc(Long userId);

    @Query("SELECT r FROM ReservationEntity r WHERE r.status NOT IN ('CANCELLED', 'EXPIRED') " +
           "AND r.endDate >= :today")
    List<ReservationEntity> findActiveReservations(@Param("today") LocalDate today);
}