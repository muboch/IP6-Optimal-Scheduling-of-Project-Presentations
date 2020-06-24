package ch.fhnw.ip6.ospp.event;

import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class SolveEvent extends ApplicationEvent {

    private String solverName;
    private TestMode testMode;
    private int timeLimit;

    public enum TestMode {
        NONE,
        NORMAL,
        LARGE
    }

    public SolveEvent(Object source, String solverName, TestMode testMode, int timeLimit) {
        super(source);
        this.solverName = solverName;
        this.testMode = testMode;
        this.timeLimit = timeLimit;
    }
}
