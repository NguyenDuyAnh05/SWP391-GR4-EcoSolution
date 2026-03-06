package org.swp391_group4_backend.ecosolution.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.User;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserAuth;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserRole;
import org.swp391_group4_backend.ecosolution.auth.domain.entity.UserStatus;
import org.swp391_group4_backend.ecosolution.auth.repository.UserAuthRepository;
import org.swp391_group4_backend.ecosolution.auth.repository.UserRepository;
import org.swp391_group4_backend.ecosolution.collectors.domain.entity.CollectorScore;
import org.swp391_group4_backend.ecosolution.collectors.domain.entity.CollectorStatusHistory;
import org.swp391_group4_backend.ecosolution.collectors.repository.CollectorScoreRepository;
import org.swp391_group4_backend.ecosolution.collectors.repository.CollectorStatusHistoryRepository;
import org.swp391_group4_backend.ecosolution.tasks.domain.entity.TaskStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
@Slf4j
public class DataInitializer {

  @Bean
  @Transactional
  public CommandLineRunner initializeData(
      UserRepository userRepository,
      UserAuthRepository userAuthRepository,
      CollectorScoreRepository collectorScoreRepository,
      CollectorStatusHistoryRepository collectorStatusHistoryRepository,
      PasswordEncoder passwordEncoder) {

    return args -> {
      // Check if data already exists
      if (userRepository.count() > 0) {
        log.info("Database already initialized. Skipping data initialization.");
        return;
      }

      log.info("Initializing database with sample data...");

      // Create Admin User
      User admin = createAndSaveUser(
          userRepository,
          userAuthRepository,
          passwordEncoder,
          "System Administrator",
          "admin@ecosolution.com",
          "Admin@123456",
          UserRole.SYSTEM_ADMIN
      );
      log.info("✓ Created SYSTEM_ADMIN: {} ({})", admin.getName(), admin.getEmail());

      // Create Enterprise Admin Users
      User enterpriseAdmin1 = createAndSaveUser(
          userRepository,
          userAuthRepository,
          passwordEncoder,
          "Enterprise Admin 1",
          "enterprise_admin1@ecosolution.com",
          "EnterpriseAdmin@123456",
          UserRole.ENTERPRISE_ADMIN
      );
      log.info("✓ Created ENTERPRISE_ADMIN: {} ({})", enterpriseAdmin1.getName(), enterpriseAdmin1.getEmail());

      User enterpriseAdmin2 = createAndSaveUser(
          userRepository,
          userAuthRepository,
          passwordEncoder,
          "Enterprise Admin 2",
          "enterprise_admin2@ecosolution.com",
          "EnterpriseAdmin@123456",
          UserRole.ENTERPRISE_ADMIN
      );
      log.info("✓ Created ENTERPRISE_ADMIN: {} ({})", enterpriseAdmin2.getName(), enterpriseAdmin2.getEmail());

      // Create Assignor Users
      User assignor1 = createAndSaveUser(
          userRepository,
          userAuthRepository,
          passwordEncoder,
          "Task Assignor 1",
          "assignor1@ecosolution.com",
          "Assignor@123456",
          UserRole.ASSIGNOR
      );
      log.info("✓ Created ASSIGNOR: {} ({})", assignor1.getName(), assignor1.getEmail());

      User assignor2 = createAndSaveUser(
          userRepository,
          userAuthRepository,
          passwordEncoder,
          "Task Assignor 2",
          "assignor2@ecosolution.com",
          "Assignor@123456",
          UserRole.ASSIGNOR
      );
      log.info("✓ Created ASSIGNOR: {} ({})", assignor2.getName(), assignor2.getEmail());

      // Create Collector Users
      List<User> collectors = new ArrayList<>();
      String[] collectorNames = {
          "John Collector",
          "Alice Walker",
          "Bob Smith",
          "Carol Johnson",
          "David Lee",
          "Emma Wilson",
          "Frank Brown",
          "Grace Davis"
      };

      for (int i = 0; i < collectorNames.length; i++) {
        User collector = createAndSaveUser(
            userRepository,
            userAuthRepository,
            passwordEncoder,
            collectorNames[i],
            "collector" + (i + 1) + "@ecosolution.com",
            "Collector@123456",
            UserRole.COLLECTOR
        );
        collectors.add(collector);
        log.info("✓ Created COLLECTOR: {} ({})", collector.getName(), collector.getEmail());
      }

      // Create Collector Scores
      log.info("\nCreating collector scores...");
      BigDecimal[][] scoreData = {
          {BigDecimal.valueOf(95.5), BigDecimal.valueOf(92.3), BigDecimal.valueOf(2.1), BigDecimal.valueOf(93.27)},
          {BigDecimal.valueOf(88.0), BigDecimal.valueOf(85.5), BigDecimal.valueOf(5.2), BigDecimal.valueOf(86.10)},
          {BigDecimal.valueOf(92.7), BigDecimal.valueOf(90.1), BigDecimal.valueOf(3.5), BigDecimal.valueOf(90.90)},
          {BigDecimal.valueOf(85.3), BigDecimal.valueOf(82.0), BigDecimal.valueOf(8.0), BigDecimal.valueOf(83.05)},
          {BigDecimal.valueOf(97.2), BigDecimal.valueOf(95.8), BigDecimal.valueOf(1.2), BigDecimal.valueOf(96.25)},
          {BigDecimal.valueOf(91.0), BigDecimal.valueOf(88.5), BigDecimal.valueOf(4.0), BigDecimal.valueOf(89.25)},
          {BigDecimal.valueOf(86.5), BigDecimal.valueOf(83.2), BigDecimal.valueOf(6.8), BigDecimal.valueOf(84.65)},
          {BigDecimal.valueOf(93.8), BigDecimal.valueOf(91.5), BigDecimal.valueOf(3.0), BigDecimal.valueOf(92.05)}
      };

      for (int i = 0; i < collectors.size(); i++) {
        CollectorScore score = CollectorScore.builder()
            .collector(collectors.get(i))
            .collectorId(collectors.get(i).getId())
            .responseRate(scoreData[i][0])
            .completionRate(scoreData[i][1])
            .complaintRate(scoreData[i][2])
            .reliabilityScore(scoreData[i][3])
            .updatedAt(LocalDateTime.now())
            .build();

        collectorScoreRepository.save(score);
        log.info("  ✓ Score created for {} - Reliability: {}", collectors.get(i).getName(), scoreData[i][3]);
      }

      // Create Collector Status Histories
      log.info("\nCreating collector status histories...");
      TaskStatus[][] statusTransitions = {
          {TaskStatus.ASSIGNED, TaskStatus.ACCEPTED},
          {TaskStatus.ACCEPTED, TaskStatus.IN_PROGRESS},
          {TaskStatus.IN_PROGRESS, TaskStatus.COMPLETED},
          {TaskStatus.ASSIGNED, TaskStatus.CANCELLED},
          {TaskStatus.ASSIGNED, TaskStatus.ACCEPTED},
          {TaskStatus.ACCEPTED, TaskStatus.IN_PROGRESS}
      };

      String[] statusReasons = {
          "Initial account activation",
          "Started collection task",
          "Collection task completed successfully",
          "Task cancelled due to unavailability",
          "Reassigned to new collection area",
          "Resumed collection operations"
      };

      for (int i = 0; i < collectors.size(); i++) {
        TaskStatus[] transitions = statusTransitions[i % statusTransitions.length];
        String reason = statusReasons[i % statusReasons.length];

        CollectorStatusHistory history = CollectorStatusHistory.builder()
            .collector(collectors.get(i))
            .statusFrom(transitions[0])
            .statusTo(transitions[1])
            .reason(reason)
            .changedAt(LocalDateTime.now().minusHours(24 - i))
            .build();

        collectorStatusHistoryRepository.save(history);
        log.info("  ✓ Status history created for {}: {} → {}",
            collectors.get(i).getName(), transitions[0], transitions[1]);
      }

      // Create Citizen Users
      log.info("\nCreating citizen users...");
      String[] citizenNames = {
          "Maria García",
          "Chen Wei",
          "Priya Patel",
          "Johann Mueller",
          "Sofia Rossi"
      };

      for (int i = 0; i < citizenNames.length; i++) {
        User citizen = createAndSaveUser(
            userRepository,
            userAuthRepository,
            passwordEncoder,
            citizenNames[i],
            "citizen" + (i + 1) + "@ecosolution.com",
            "Citizen@123456",
            UserRole.CITIZEN
        );
        log.info("✓ Created CITIZEN: {} ({})", citizen.getName(), citizen.getEmail());
      }

      log.info("\n========================================");
      log.info("✓ Database initialization completed successfully!");
      log.info("========================================");
      log.info("\nTest Credentials:");
      log.info("  Admin: admin@ecosolution.com / Admin@123456");
      log.info("  Enterprise Admin: enterprise_admin1@ecosolution.com / EnterpriseAdmin@123456");
      log.info("  Assignor: assignor1@ecosolution.com / Assignor@123456");
      log.info("  Collector: collector1@ecosolution.com / Collector@123456");
      log.info("  Citizen: citizen1@ecosolution.com / Citizen@123456");
      log.info("========================================\n");
    };
  }

  private User createAndSaveUser(
      UserRepository userRepository,
      UserAuthRepository userAuthRepository,
      PasswordEncoder passwordEncoder,
      String name,
      String email,
      String password,
      UserRole role) {

    // Create User entity
    User user = User.builder()
        .id(UUID.randomUUID())
        .name(name)
        .email(email)
        .role(role)
        .status(UserStatus.ACTIVE)
        .createdAt(LocalDateTime.now())
        .build();

    User savedUser = userRepository.save(user);

    // Create UserAuth entity
    UserAuth userAuth = UserAuth.builder()
        .userId(savedUser.getId())
        .username(email)
        .passwordHash(passwordEncoder.encode(password))
        .user(savedUser)
        .createdAt(LocalDateTime.now())
        .build();

    userAuthRepository.save(userAuth);

    return savedUser;
  }
}


