package fr.lesideaux.saarasaka.backend.controller;

import fr.lesideaux.saarasaka.backend.data.entity.ParkingSpaceEntity;
import fr.lesideaux.saarasaka.backend.data.repository.ParkingSpaceRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/parking")
public class ParkingSpaceController {

    private final ParkingSpaceRepository parkingSpaceRepository;

    public ParkingSpaceController(ParkingSpaceRepository parkingSpaceRepository) {
        this.parkingSpaceRepository = parkingSpaceRepository;
    }

    @GetMapping
    public List<ParkingSpaceEntity> getAllParkingSpaces() {
        return parkingSpaceRepository.findAll();
    }
}
