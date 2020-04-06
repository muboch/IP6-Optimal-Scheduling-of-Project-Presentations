package ch.fhnw.ip6.ospp.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExcelFile {
    private byte[] content;
    private String name;
}