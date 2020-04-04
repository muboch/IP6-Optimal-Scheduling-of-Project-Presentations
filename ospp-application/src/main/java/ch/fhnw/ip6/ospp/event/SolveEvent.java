package ch.fhnw.ip6.ospp.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class SolveEvent extends ApplicationEvent {

    public SolveEvent(Object source) {
        super(source);
    }



}
