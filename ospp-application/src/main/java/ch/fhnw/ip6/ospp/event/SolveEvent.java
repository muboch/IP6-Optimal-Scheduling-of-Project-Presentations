package ch.fhnw.ip6.ospp.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Setter
@Getter
public class SolveEvent extends ApplicationEvent {

    private String solverName;
    private TestMode testMode;

    public enum TestMode {
        NORMAL("2019"),
        LARGE("300");

        String indicator;

        public String getIndicator() {
            return indicator;
        }

        TestMode(String indicator) {
            this.indicator = indicator;
        }
    }

    public SolveEvent(Object source, String solverName, TestMode testMode) {
        super(source);
        this.solverName = solverName;
        this.testMode = testMode;
    }
}
