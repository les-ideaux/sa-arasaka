package fr.lesideaux.saarasaka.backend.data.repository;

import fr.lesideaux.saarasaka.backend.data.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {

    List<ReservationEntity> findByParkingSpaceIdIs(Long parkingSpaceId);
}
