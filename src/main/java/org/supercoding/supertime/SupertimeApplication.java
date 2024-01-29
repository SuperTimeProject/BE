package org.supercoding.supertime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SupertimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupertimeApplication.class, args);
    }

}
