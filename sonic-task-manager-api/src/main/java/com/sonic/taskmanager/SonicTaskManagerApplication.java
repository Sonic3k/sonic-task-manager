package com.sonic.taskmanager;

import com.sonic.taskmanager.service.PreferencesService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SonicTaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SonicTaskManagerApplication.class, args);
    }

    /**
     * Initialize default preferences on application startup
     */
    @Bean
    CommandLineRunner initPreferences(PreferencesService preferencesService) {
        return args -> {
            preferencesService.initializeDefaultPreferences();
            System.out.println("Sonic Task Manager started successfully!");
            System.out.println("Access the application at: http://localhost:8080");
        };
    }
}