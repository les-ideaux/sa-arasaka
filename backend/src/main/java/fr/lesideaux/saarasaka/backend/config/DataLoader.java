package fr.lesideaux.saarasaka.backend.config;

import fr.lesideaux.saarasaka.backend.data.TestEntity;
import fr.lesideaux.saarasaka.backend.data.TestRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
