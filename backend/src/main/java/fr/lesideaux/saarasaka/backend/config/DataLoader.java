package fr.lesideaux.saarasaka.backend.config;

import fr.lesideaux.saarasaka.backend.data.entity.ParkingSpaceEntity;
import fr.lesideaux.saarasaka.backend.data.entity.UserEntity;
import fr.lesideaux.saarasaka.backend.data.repository.ParkingSpaceRepository;
import fr.lesideaux.saarasaka.backend.data.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadParkingSpaces(ParkingSpaceRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<String> rows = List.of("A", "B", "C", "D", "E", "F");
                List<String> numbers = List.of("01", "02", "03", "04", "05", "06", "07", "08", "09", "10");

                for (String row : rows) {
                    for (String number : numbers) {
                        boolean hasCharger = row.equals("A") || row.equals("F");
                        repository.save(new ParkingSpaceEntity(row, number, hasCharger, false, false));
                    }
                }
            }
        };
    }

    @Bean
    CommandLineRunner loadUsers(UserRepository repository, PasswordEncoder encoder) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new UserEntity(
                        "employee@company.com", "Alice", "Dupont",
                        encoder.encode("password123"),
                        UserEntity.Role.EMPLOYEE
                ));
                repository.save(new UserEntity(
                        "manager@company.com", "Bob", "Martin",
                        encoder.encode("password123"),
                        UserEntity.Role.MANAGER
                ));
                repository.save(new UserEntity(
                        "secretary@company.com", "Claire", "Bernard",
                        encoder.encode("password123"),
                        UserEntity.Role.SECRETARY
                ));
            }
        };
    }
}
