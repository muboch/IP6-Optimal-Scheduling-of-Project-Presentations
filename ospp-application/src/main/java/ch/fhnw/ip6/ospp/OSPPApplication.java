package ch.fhnw.ip6.ospp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ch.fhnw.ip6")
public class OSPPApplication {

    public static void main(String[] args) {
        SpringApplication.run(OSPPApplication.class, args);
    }
}
