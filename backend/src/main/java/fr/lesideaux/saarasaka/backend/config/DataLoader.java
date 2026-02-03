package fr.lesideaux.saarasaka.backend.config;

import fr.lesideaux.saarasaka.backend.data.entity.ParkingSpaceEntity;
import fr.lesideaux.saarasaka.backend.data.entity.TestEntity;
import fr.lesideaux.saarasaka.backend.data.repository.ParkingSpaceRepository;
import fr.lesideaux.saarasaka.backend.data.repository.TestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(TestRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new TestEntity(1L, "Hello for Saburo Arasaka"));
            }
        };
    }

    @Bean
    CommandLineRunner loadParkingSpaces(ParkingSpaceRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<String> rows = List.of("A", "B", "C", "D", "E", "F");
                List<String> numbers = List.of("01", "02", "03", "04", "05", "06", "07", "08", "09", "10");

                for (String row : rows) {
                    for (String number : numbers) {
                        if (row.equals("A") || row.equals("F")) {
                            repository.save(new ParkingSpaceEntity(row, number, true, false, false));
                        } else {
                            repository.save(new ParkingSpaceEntity(row, number, false, false, false));
                        }
                    }
                }
            }
        };
    }
}
