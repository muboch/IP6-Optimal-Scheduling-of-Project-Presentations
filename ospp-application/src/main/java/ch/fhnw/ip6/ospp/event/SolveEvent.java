package ch.fhnw.ip6.ospp.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class SolveEvent extends ApplicationEvent {

    private String solverName;
    private boolean testMode;
    private int timeLimit;

    public SolveEvent(Object source, String solverName, boolean testMode, int timeLimit) {
        super(source);
        this.solverName = solverName;
        this.testMode = testMode;
        this.timeLimit = timeLimit;
    }
}
