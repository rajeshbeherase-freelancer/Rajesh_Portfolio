package Utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtils {

    private static final String TEST_DATA_FILE = "testdata/EmpMonitor_TestData.xlsx";

    /**
     * Reads the requested sheet and returns an Object[][] where each row is a Map of column header -> cell value.
     * Empty rows and rows marked for skipping (first cell = "N" or "No") are ignored.
     */
    public static Object[][] getTestData(String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();

        try (InputStream fis = ExcelUtils.class.getClassLoader().getResourceAsStream(TEST_DATA_FILE);
             Workbook workbook = new XSSFWorkbook(fis)) {

            if (fis == null) {
                throw new RuntimeException("Test data file not found: " + TEST_DATA_FILE);
            }

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found in test data file: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Header row is missing in sheet: " + sheetName);
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(getCellValue(cell).trim());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) {
                    continue;
                }

                String runFlag = getCellValue(row.getCell(0)).trim();
                if (runFlag.equalsIgnoreCase("N") || runFlag.equalsIgnoreCase("No")) {
                    continue;
                }

                Map<String, String> rowData = new LinkedHashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    rowData.put(headers.get(j), getCellValue(row.getCell(j)));
                }
                data.add(rowData);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to read test data file: " + TEST_DATA_FILE, e);
        }

        Object[][] result = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            result[i][0] = data.get(i);
        }
        return result;
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private static boolean isEmptyRow(Row row) {
        for (Cell cell : row) {
            if (!getCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
