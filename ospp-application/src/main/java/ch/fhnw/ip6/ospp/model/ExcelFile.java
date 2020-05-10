package ch.fhnw.ip6.ospp.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExcelFile {
    private final byte[] content;
    private final String name;
}