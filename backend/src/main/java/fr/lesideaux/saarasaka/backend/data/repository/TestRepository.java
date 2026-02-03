package fr.lesideaux.saarasaka.backend.data.repository;

import fr.lesideaux.saarasaka.backend.data.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity, Long> {
}
