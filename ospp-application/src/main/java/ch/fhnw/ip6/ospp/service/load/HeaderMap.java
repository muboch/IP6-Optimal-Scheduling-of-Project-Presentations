package ch.fhnw.ip6.ospp.service.load;

import ch.fhnw.ip6.ospp.service.FachlicheException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HeaderMap extends HashMap<String, Integer> {

    private final String[] headerCols;

    protected HeaderMap(String... headerCols) {
        this.headerCols = headerCols;
    }

    @Override
    public Integer get(Object key) {
        Integer value = super.get(key);
        if (value == null) {
            throw new FachlicheException("Die Spaltenbezeichnungen sind nicht konform. Folgende Bezeichnungen sind erlaubt: " + String.join(", ", headerCols));
        }
        return value;
    }

    public List<String> getHeaderCols() {
        return Arrays.asList(headerCols);
    }
}
