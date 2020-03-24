package ch.fhnw.ip6.ospp.model;

import java.util.Arrays;

public enum Type {

    ART("art"),
    MUSIC("music"),
    NORMAL("normal"),
    DANCE("dance");

    String type;

    Type(String type) {
        this.type = type;
    }

    public static Type fromString(String text) {
        return Arrays.stream(Type.values()).filter(b -> b.type.equalsIgnoreCase(text)).findFirst().orElse(null);
    }
}
