package org.swp391_group4_backend.ecosolution.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.swp391_group4_backend.ecosolution.scheduler.DailyTaskScheduler;

@Component
@RequiredArgsConstructor
public class StartUpTaskGenerator implements CommandLineRunner {
    private final DailyTaskScheduler dailyTaskScheduler;


    @Override
    public void run(String... args) throws Exception {
        dailyTaskScheduler.generateDailyPickupTasks();
    }
}
