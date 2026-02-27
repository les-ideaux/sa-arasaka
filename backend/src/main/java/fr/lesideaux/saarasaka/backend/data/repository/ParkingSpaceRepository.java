package fr.lesideaux.saarasaka.backend.data.repository;

import fr.lesideaux.saarasaka.backend.data.entity.ParkingSpaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpaceEntity, Long> {
    List<ParkingSpaceEntity> findByIsEquippedWithElectricCharging(boolean equipped);
    Optional<ParkingSpaceEntity> findByRowAndNumber(String row, String number);
}