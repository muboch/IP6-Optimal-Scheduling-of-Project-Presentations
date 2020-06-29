package ch.fhnw.ip6.ospp;

import ch.fhnw.ip6.ospp.event.SolveEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

@Slf4j
@SpringBootApplication
@ConfigurationProperties(prefix = "ospp")
@ComponentScan(basePackages = "ch.fhnw.ip6")
public class OSPPApplication {

    @Value("${ospp.solver}")
    private static String solver;
    @Value("${ospp.timeLimit}")
    private static int timeLimit;
    @Value("${ospp.testMode}")
    private static SolveEvent.TestMode testMode;

    public static void main(String[] args) {
        SpringApplication.run(OSPPApplication.class, args);

        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();

        log.debug("TestMode: {}, Solver {}, TimeLimit: {}", testMode, solver, timeLimit);
    }
}
