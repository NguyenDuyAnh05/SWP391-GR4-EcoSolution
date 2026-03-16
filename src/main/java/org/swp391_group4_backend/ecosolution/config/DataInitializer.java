package org.swp391_group4_backend.ecosolution.config;

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
        if(userRepository.count() == 0){

            List<User> users = List.of(
                    new User(null, "Citizen","1" ,"citizen1", "1", UserRole.CITIZEN),
                    new User(null, "Citizen","2" ,"citizen2", "1", UserRole.CITIZEN),
                    new User(null, "Citizen","3" ,"citizen3", "1", UserRole.CITIZEN),
                    new User(null, "Citizen","4" ,"citizen4", "1", UserRole.CITIZEN),
                    new User(null, "Citizen","5" ,"citizen5", "1", UserRole.CITIZEN),
                    new User(null, "Citizen","6" ,"citizen6", "1", UserRole.CITIZEN),

                    new User(null, "Manager","1","manager1", "1", UserRole.MANAGER),
                    new User(null, "Manager","2","manager2", "1", UserRole.MANAGER),

                    new User(null, "collector","1","collector1", "1", UserRole.COLLECTOR),
                    new User(null, "collector","2","collector2", "1", UserRole.COLLECTOR),
                    new User(null, "collector","3","collector3", "1", UserRole.COLLECTOR),
                    new User(null, "collector","4","collector4", "1", UserRole.COLLECTOR),
                    new User(null, "collector","5","collector5", "1", UserRole.COLLECTOR),
                    new User(null, "collector","6","collector6", "1", UserRole.COLLECTOR),
                    new User(null, "collector","7","collector7", "1", UserRole.COLLECTOR)
            );
            userRepository.saveAll(users);
            System.out.println("Data initialized");
        }

    }
}
