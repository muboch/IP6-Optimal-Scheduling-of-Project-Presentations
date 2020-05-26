package ch.fhnw.ip6.ospp.service.load;

import ch.fhnw.ip6.ospp.service.FachlicheException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class AbstractLoadService {

    void createHeaderIndexMap(Row row, HeaderMap headerMap) {
        int col = 0;
        while (col < row.getLastCellNum()) {
            String cell = row.getCell(col).getStringCellValue();
            if (headerMap.getHeaderCols().contains(cell)) {
                headerMap.put(cell, col);
                col++;
            } else {
                throw new FachlicheException("Die Spaltenbezeichnungen sind nicht konform. Folgende Bezeichnungen sind erlaubt: " + String.join(", ", headerMap.getHeaderCols()));
            }

        }
    }
}
