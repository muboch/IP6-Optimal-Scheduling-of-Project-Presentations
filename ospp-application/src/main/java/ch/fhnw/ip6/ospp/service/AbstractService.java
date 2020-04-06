package ch.fhnw.ip6.ospp.service;

import org.apache.poi.ss.usermodel.Row;

import java.util.Map;

public class AbstractService {

    void createHeaderIndexMap(Row row, Map<String, Integer> headerMap) {
        int col = 0;
        while (col < row.getLastCellNum()) {
            headerMap.put(row.getCell(col).getStringCellValue(), col);
            col++;
        }
    }

}
