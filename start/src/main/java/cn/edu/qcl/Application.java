package cn.edu.qcl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot Starter
 *
 */
@SpringBootApplication(scanBasePackages = {"cn.edu.qcl"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
