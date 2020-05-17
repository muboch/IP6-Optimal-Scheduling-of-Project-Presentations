package ch.fhnw.ip6.ospp.service.load;

import org.apache.poi.ss.usermodel.Row;

import java.util.Map;

public class AbstractLoadService {

    void createHeaderIndexMap(Row row, Map<String, Integer> headerMap) {
        int col = 0;
        while (col < row.getLastCellNum()) {
            headerMap.put(row.getCell(col).getStringCellValue(), col);
            col++;
        }
    }

}
