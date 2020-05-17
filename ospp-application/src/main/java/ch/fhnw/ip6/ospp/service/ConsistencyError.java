package ch.fhnw.ip6.ospp.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonProperty;

@Data
@AllArgsConstructor
public class ConsistencyError {

    @JsonProperty
    private Status status;
    @JsonProperty
    private String message;

    public enum Status {
        WARN,
        ERROR
    }
}
