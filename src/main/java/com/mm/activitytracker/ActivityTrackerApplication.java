package com.mm.activitytracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EntityScan(basePackages =  {"com.mm.activitytracker", "com.mm.user.core"})
@ComponentScan(basePackages = {"com.mm.activitytracker", "com.mm.user.core"})
@EnableJpaAuditing
public class ActivityTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ActivityTrackerApplication.class, args);
    }
}
