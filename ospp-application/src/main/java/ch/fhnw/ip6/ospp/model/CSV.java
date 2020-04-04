package ch.fhnw.ip6.ospp.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CSV {
    private byte[] content;
    private String name;
}