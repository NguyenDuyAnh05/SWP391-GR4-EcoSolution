package org.swp391_group4_backend.ecosolution.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.swp391_group4_backend.ecosolution.constant.UserRole;
import org.swp391_group4_backend.ecosolution.entity.User;
import org.swp391_group4_backend.ecosolution.repository.UserRepository;

import java.util.List;
@Component
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;

    public DataInitializer(UserRepository repository) {
        this.userRepository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(userRepository.count() == 0);{
            List<User> users = List.of(
                    new User(null, "citizen1", "1", UserRole.CITIZEN),
                    new User(null, "citizen2", "1", UserRole.CITIZEN),
                    new User(null, "citizen3", "1", UserRole.CITIZEN),
                    new User(null, "citizen4", "1", UserRole.CITIZEN),
                    new User(null, "citizen5", "1", UserRole.CITIZEN),
                    new User(null, "citizen6", "1", UserRole.CITIZEN),

                    new User(null, "manager1", "1", UserRole.MANAGER),
                    new User(null, "manager2", "1", UserRole.MANAGER),

                    new User(null, "collector1", "1", UserRole.COLLECTOR),
                    new User(null, "collector2", "1", UserRole.COLLECTOR),
                    new User(null, "collector3", "1", UserRole.COLLECTOR),
                    new User(null, "collector4", "1", UserRole.COLLECTOR),
                    new User(null, "collector5", "1", UserRole.COLLECTOR),
                    new User(null, "collector6", "1", UserRole.COLLECTOR),
                    new User(null, "collector7", "1", UserRole.COLLECTOR)
            );
            userRepository.saveAll(users);
            System.out.println("Data initialized");
        }

    }
}
