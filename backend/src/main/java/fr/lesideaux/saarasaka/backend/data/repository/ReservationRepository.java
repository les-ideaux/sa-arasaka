package fr.lesideaux.saarasaka.backend.data.repository;

import fr.lesideaux.saarasaka.backend.data.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByParkingSpaceIdIsAndStartDateBetween(Long parkingSpaceId, Date startDateAfter, Date startDateBefore);

    List<ReservationEntity> findByParkingSpaceIdIsAndEndDateBetween(Long parkingSpaceId, Date startDateAfter, Date startDateBefore);

    List<ReservationEntity> findByParkingSpaceIdIsAndStartDateBetweenOrEndDateBetween(Long parkingSpaceId, Date startDateAfter, Date startDateBefore, Date endDateAfter, Date endDateBefore);

    List<ReservationEntity> findByParkingSpaceIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long parkingSpaceId,  Date startDate, Date endDate);
}
