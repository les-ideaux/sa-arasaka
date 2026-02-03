package fr.lesideaux.saarasaka.backend.data.repository;

import fr.lesideaux.saarasaka.backend.data.entity.ParkingSpaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpaceEntity, Long> {
}
