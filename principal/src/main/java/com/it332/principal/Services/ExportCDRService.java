package com.it332.principal.Services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.it332.principal.DTO.DocumentsResponse;
import com.it332.principal.DTO.ExcelRequest;
import com.it332.principal.DTO.LRResponse;
import com.it332.principal.Models.School;
import com.it332.principal.Models.Uacs;
import com.it332.principal.Models.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.io.ByteArrayOutputStream;

@Service
public class ExportCDRService {
    @Autowired
    UserService userService;

    @Autowired
    LRService lrService;

    @Autowired
    DocumentsService documentsService;

    @Autowired
    SchoolService schoolService;

    final int DEFAULT_COL_START = 7;
    final int DEFAULT_COL_NUM = 16;
    final int DEFAULT_ROW_SUM = 73;
    final int DEFAULT_ROW_START = 78;
    final int DEFAULT_ROW_LAST = 90;

    public byte[] generateCDRData(ExcelRequest request) throws IOException {
        // Data to write
        School school = schoolService.getSchoolById(request.getSchoolId());
        List<LRResponse> dataToWrite = lrService.getAllApprovedLRsByDocumentsId(request.getDocumentId());
        DocumentsResponse document = documentsService.getDocumentBySchoolYearMonth(
                request.getSchoolId(),
                request.getYear(),
                request.getMonth());
        User user = userService.getUserById(request.getUserId());
        String userFullName = user.getFname() + " " + user.getMname().substring(0, 1) + ". " + user.getLname();
        List<Uacs> addtlUacs = lrService.getAdditionalUacsListByApprovedLr(request.getDocumentId());

        // Load template workbook from the classpath
        ClassPathResource resource = new ClassPathResource("Templates/CDR-2024.xlsx");

        // LR Automation
        try (InputStream inputStream = resource.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {

            // Rename the sheet
            // Formats sheet name to ex: JAN'24
            workbook.setSheetName(0, formatMonthYear(
                    request.getMonth(),
                    request.getYear()) + " (CDR)");

            int addtlCol = addtlUacs.size();
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            if (addtlCol > 0) {
                // Insert additional UACS column at H16 (Column H, index 7)
                insertAdditionalColumns(sheet, DEFAULT_COL_START, addtlUacs);

                // Insert additional UACS rows at 78 (Row 79, zero-based index)
                insertAdditionalRows(sheet, DEFAULT_ROW_START, addtlUacs);

                // Set column widths for the additional columns
                for (int i = 7; i < DEFAULT_COL_NUM + addtlCol; i++) {
                    if (i == DEFAULT_COL_NUM + addtlCol - 1) {
                        sheet.setColumnWidth(i, 18 * 256);
                    } else if (i == DEFAULT_COL_NUM + addtlCol - 2) {
                        sheet.setColumnWidth(i, 13 * 256);
                    } else if (i == DEFAULT_COL_NUM + addtlCol - 3) {
                        sheet.setColumnWidth(i, 29 * 256);
                    } else {
                        sheet.setColumnWidth(i, 11 * 256);
                    }
                }

                // Update the SUM formula that includes the additional columns
                updateSumFormula(sheet, DEFAULT_COL_NUM + addtlUacs.size(), 75, addtlUacs.size());
            }

            // Set Date
            setCDRDate(sheet, workbook, document.getMonth(), document.getYear(), 4, 0);

            // Set Entity Name
            setCDREntityNameCells(sheet, workbook, "Name of Accountable Officer: ",
                    document.getClaimant(), 4, 9 + addtlCol);
            setCDREntityNameCells(sheet, workbook, "Station: ", school.getFullName(), 6, 9 + addtlCol);

            // Set Claimant and Cash Adv.
            setCDRHeader(sheet, workbook, document.getClaimant(), 14, 2);
            setCDRHeader(sheet, workbook, document.getCashAdvance(), 14, 3);

            // Set Cert. Cor. and Received by
            setCDRPeopleCells(sheet, workbook, document.getClaimant(), 88, 0);
            setCDRPeopleCells(sheet, workbook, userFullName, 88, 9 + addtlCol);

            int rowIndex = 16; // Start from row 17 (zero-based index)

            // Initialize the cell styles array
            CellStyle[] cellStyles = new CellStyle[DEFAULT_COL_NUM + addtlCol]; // For columns A-H (index 0-7)

            // Initialize and customize styles for the required columns (skipping B and D)
            for (int i = 0; i < cellStyles.length; i++) {
                // Initialize cell style
                cellStyles[i] = workbook.createCellStyle();

                // Create a new font with Century Gothic and font size 20
                Font font = workbook.createFont();
                font.setFontName("Century Gothic");
                font.setFontHeightInPoints((short) 9); // Font size 20
                // Set font color to black
                font.setColor(IndexedColors.BLACK.getIndex());

                // Apply the new font to the cell style
                cellStyles[i].setFont(font);

                // ORS/BURS, Payee, and Amount custom alignments
                cellStyles[i].setVerticalAlignment(VerticalAlignment.CENTER); // Default vert. alignment

                // Set borders (all borders)
                if (i > 6) {
                    cellStyles[i].setBorderRight(BorderStyle.THIN);
                    cellStyles[i].setBorderLeft(BorderStyle.THIN);
                } else {
                    cellStyles[i].setBorderRight(BorderStyle.MEDIUM);
                    cellStyles[i].setBorderLeft(BorderStyle.MEDIUM);
                }
                cellStyles[i].setBorderTop(BorderStyle.THIN);
                cellStyles[i].setBorderBottom(BorderStyle.THIN);

                // Apply number format with two decimal places to column 4 (Amount)
                if (i != 13 + addtlCol || i <= 3) {
                    DataFormat format = workbook.createDataFormat();
                    cellStyles[i].setDataFormat(format.getFormat("#,##0.00")); // Set decimal format
                    // Set alignment to centered
                    cellStyles[i].setAlignment(HorizontalAlignment.CENTER);
                }
            }

            // Write values to specific cells and apply the correct cell styles
            for (LRResponse data : dataToWrite) {
                Row row = sheet.createRow(rowIndex);

                // Populate cells and apply styles
                for (int i = 0; i < (16 + addtlCol); i++) {
                    Cell cell = row.createCell(i);
                    if (i == 0) {
                        cell.setCellValue(data.getDate());
                    } else if (i == 1) {
                        cell.setCellValue(data.getOrsBursNo());
                    } else if (i == 2) {
                        cell.setCellValue(data.getParticulars());
                    } else if (i == 4) {
                        cell.setCellValue(data.getAmount());
                    } else if (i == 5) {
                        // Set the formula dynamically based on the current rowIndex
                        if (rowIndex == 16) { // First row (for example, row 17 in Excel)
                            cell.setCellFormula("F14-E17"); // Example for the first row
                        } else {
                            // For subsequent rows, adjust the formula according to rowIndex
                            cell.setCellFormula("F" + (rowIndex) + "-E" + (rowIndex + 1));
                        }
                    } else if (i == (7 + addtlCol)) {
                        if ("5020502001".equals(data.getObjectCode())) {
                            cell.setCellValue(data.getAmount());
                        }
                    } else if (i == (8 + addtlCol)) {
                        if ("5020402000".equals(data.getObjectCode())) {
                            cell.setCellValue(data.getAmount());
                        }
                    } else if (i == (9 + addtlCol)) {
                        if ("5020503000".equals(data.getObjectCode())) {
                            cell.setCellValue(data.getAmount());
                        }
                    } else if (i == (10 + addtlCol)) {
                        if ("5029904000".equals(data.getObjectCode())) {
                            cell.setCellValue(data.getAmount());
                        }
                    } else if (i == (11 + addtlCol)) {
                        if ("5020201000".equals(data.getObjectCode())) {
                            cell.setCellValue(data.getAmount());
                        }
                    } else if (i == (12 + addtlCol)) {
                        // R & M school bldg. etc.
                        if ("5021304002".equals(data.getObjectCode())) {
                            cell.setCellValue(data.getAmount());
                        }
                    } else if (i == (15 + addtlCol)) {
                        if ("5020399000".equals(data.getObjectCode())) {
                            cell.setCellValue(data.getAmount());
                        }
                    }

                    if (addtlCol > 0) {
                        for (int j = 0; j < addtlUacs.size(); j++) {
                            if (i == (7 + j)) {
                                if (addtlUacs.get(j).getCode().equals(data.getObjectCode())) {
                                    cell.setCellValue(data.getAmount());
                                }
                            }
                        }
                    }

                    // Apply cell style from the template
                    if (cellStyles[i] != null) {
                        cell.setCellStyle(cellStyles[i]);
                    }
                }

                rowIndex++;
            }

            // Force recalculation of all formulas in the workbook
            workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();

            // Assuming 'workbook' is your XSSFWorkbook object and 'sheet' is your target
            // sheet
            String sheetName = workbook.getSheetName(0); // Get sheet name (assuming first sheet)
            // Set print area (adjust row and column indices)
            workbook.setPrintArea(workbook.getSheetIndex(sheetName), 0, 15 + addtlCol, 0, 89 + addtlCol);

            // Write workbook to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // workbook.close();
            return outputStream.toByteArray(); // Return the byte array
        }
    }

    private void insertAdditionalColumns(Sheet sheet, int colIndex, List<Uacs> addtlUacs) {
        // Shift columns to the right starting from the column index `colIndex`
        sheet.shiftColumns(colIndex, DEFAULT_COL_NUM + addtlUacs.size(), addtlUacs.size());

        for (Row row : sheet) {
            if (row.getRowNum() < 8 || row.getRowNum() > DEFAULT_ROW_SUM) {
                continue; // Skip rows before row 9 and after row 73
            }
            for (int i = colIndex; i < colIndex + addtlUacs.size(); i++) {
                Cell sourceCell;
                Cell newCell = row.createCell(i);

                // Set styling
                if (i == colIndex) {
                    // The first column after the added n columns
                    sourceCell = row.getCell(colIndex + addtlUacs.size());
                    if (row.getRowNum() == 8) { // If it's the header row
                        newCell.setCellStyle(sourceCell.getCellStyle());
                        continue;
                    }
                } else {
                    // The second column after the added n columns
                    sourceCell = row.getCell(colIndex + addtlUacs.size() + 1);
                }
                if (sourceCell != null) {
                    copyColCellStyle(sourceCell, newCell, i); // Copy the style of the source cell
                }

                // Set the UACS Name and Code for the new column
                if (row.getRowNum() == 10) {
                    newCell.setCellValue(addtlUacs.get(i - colIndex).getName());
                    // Merge the cells for the UACS Name
                    sheet.addMergedRegion(new CellRangeAddress(10, 11, i, i));
                } else if (row.getRowNum() == 12) {
                    newCell.setCellValue(addtlUacs.get(i - colIndex).getCode());
                }
            }
        }
    }

    private void insertAdditionalRows(Sheet sheet, int rowIndex, List<Uacs> addtlUacs) {
        // Ensure all rows exist in the range to shift
        for (int i = rowIndex; i <= sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i) == null) {
                sheet.createRow(i);
            }
        }

        // Shift rows down to make room for the additional rows
        sheet.shiftRows(rowIndex, sheet.getLastRowNum(), addtlUacs.size());

        // Reference the source row for default styling (row after the shifted rows)
        int sourceRowIndex = rowIndex + addtlUacs.size(); // This should point to row 81 in this case
        Row sourceRow = sheet.getRow(sourceRowIndex);
        if (sourceRow == null) {
            sourceRow = sheet.createRow(sourceRowIndex); // Create source row if it doesn't exist
        }

        for (int i = 0; i < addtlUacs.size(); i++) {
            int targetRowIndex = rowIndex + i; // Start creating new rows at 78 + i
            Row targetRow = sheet.getRow(targetRowIndex);
            if (targetRow == null) {
                targetRow = sheet.createRow(targetRowIndex); // Create the target row if it doesn't exist
            }

            // 3 columns to occupy: description, object code, and amount
            for (int j = DEFAULT_COL_NUM; j < DEFAULT_COL_NUM + 3; j++) {
                Cell sourceCell = sourceRow.getCell(j - 1); // Get the source cell for styling
                Cell targetCell = targetRow.createCell(j - 1); // Create target cell

                if (sourceCell != null) {
                    copyRowCellStyle(sourceCell, targetCell, DEFAULT_COL_START + i);
                    if (j == DEFAULT_COL_NUM) {
                        targetCell.setCellValue(addtlUacs.get(i).getName());
                    } else if (j == DEFAULT_COL_NUM + 1) {
                        targetCell.setCellValue(addtlUacs.get(i).getCode());
                    } else {
                        targetCell.setCellValue("Amount");
                    }
                }
            }
        }
    }

    // Helper method to copy col cell style
    private void copyColCellStyle(Cell sourceCell, Cell targetCell, int index) {
        if (sourceCell.getCellStyle() != null) {
            targetCell.setCellStyle(sourceCell.getCellStyle());
        }
        switch (sourceCell.getCellType()) {
            case STRING:
                targetCell.setCellValue(sourceCell.getStringCellValue());
                break;
            case NUMERIC:
                targetCell.setCellValue(sourceCell.getNumericCellValue());
                break;
            case BOOLEAN:
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case FORMULA:
                String columnLetter = getCellLetter(targetCell.getColumnIndex());
                // Adjust formula for the range you want
                // Example: If the sourceCell is in column 'H', and we want to sum rows 17 to 72
                targetCell.setCellFormula("SUM(" + columnLetter + "17:" + columnLetter + "72)");
                break;
            default:
                targetCell.setBlank();
                break;
        }
    }

    // Helper method to copy row cell style
    private void copyRowCellStyle(Cell sourceCell, Cell targetCell, int index) {
        if (sourceCell.getCellStyle() != null) {
            targetCell.setCellStyle(sourceCell.getCellStyle());
        }
        switch (sourceCell.getCellType()) {
            case STRING:
                targetCell.setCellValue(sourceCell.getStringCellValue());
                break;
            case NUMERIC:
                targetCell.setCellValue(sourceCell.getNumericCellValue());
                break;
            case BOOLEAN:
                targetCell.setCellValue(sourceCell.getBooleanCellValue());
                break;
            case FORMULA:
                String columnLetter = getCellLetter(index);
                // Adjust formula for the range you want
                // Example: If the sourceCell is in column 'H', and we want to sum rows 17 to 72
                targetCell.setCellFormula(columnLetter + DEFAULT_ROW_SUM);
                break;
            default:
                targetCell.setBlank();
                break;
        }
    }

    private void updateSumFormula(Sheet sheet, int colNo, int rowNo, int newCol) {
        // Find the specific cell to alter
        Row customRow = sheet.getRow(rowNo - 1);
        Cell cell = customRow.getCell(colNo - 1);
        StringBuilder sb = new StringBuilder();

        // Get the column letters for the range
        // 7 is the index for column H
        // e.g. H73+
        for (int i = 0; i < newCol; i++) {
            // colNo-2 because the sum formulas is 2 rows above this cell
            sb.append(getCellLetter(7 + i) + (rowNo - 2) + "+");
        }

        String newFormula = sb.toString() + cell.getCellFormula();
        cell.setCellFormula(newFormula);
    }

    private String getCellLetter(int columnIndex) {
        StringBuilder columnName = new StringBuilder();
        while (columnIndex >= 0) {
            columnName.insert(0, (char) ('A' + (columnIndex % 26)));
            columnIndex = columnIndex / 26 - 1;
        }
        return columnName.toString();
    }

    public void setCDRHeader(Sheet sheet, Workbook workbook, String name, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "JOHN DOE"
        cell.setCellValue(name.toUpperCase());

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();
        Font customFont = workbook.createFont();

        customFont.setFontName("Calibri");
        customFont.setFontHeightInPoints((short) 11);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setBold(true);
        cellStyle.setFont(customFont);

        // Set alignment to centered
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // Set border styles
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);
    }

    public void setCDRHeader(Sheet sheet, Workbook workbook, Double amount, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Create a DataFormat object from the workbook
        DataFormat format = workbook.createDataFormat();

        // Set value to "JOHN DOE"
        cell.setCellValue(amount);

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();
        Font customFont = workbook.createFont();
        cellStyle.setDataFormat(format.getFormat("#,##0.00"));

        customFont.setFontName("Calibri");
        customFont.setFontHeightInPoints((short) 11);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setBold(true);
        cellStyle.setFont(customFont);

        // Set alignment to centered
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // Set border styles
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);
    }

    public void setCDREntityNameCells(Sheet sheet, Workbook workbook, String desc, String name, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "Name of Accountable Officer: FERNANDO R. BONGHANOY JR."
        cell.setCellValue(desc + name.toUpperCase());

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();
        Font customFont = workbook.createFont();

        customFont.setFontName("Calibri");
        customFont.setFontHeightInPoints((short) 11);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setBold(true);
        cellStyle.setFont(customFont);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);
    }

    public void setCDRDate(Sheet sheet, Workbook workbook, String month, String year, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "'NOV 2023"
        cell.setCellValue("'" + month.substring(0, 3).toUpperCase() + " " + year);

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();
        Font customFont = workbook.createFont();

        customFont.setFontName("Calibri");
        customFont.setFontHeightInPoints((short) 11);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setBold(true);
        customFont.setUnderline(Font.U_SINGLE);
        cellStyle.setFont(customFont);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);
    }

    public void setCDRPeopleCells(Sheet sheet, Workbook workbook, String name, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "JOHN DOE"
        cell.setCellValue(name.toUpperCase());

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();
        Font customFont = workbook.createFont();

        customFont.setFontName("Calibri");
        customFont.setFontHeightInPoints((short) 11);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setBold(true);
        customFont.setUnderline(Font.U_SINGLE_ACCOUNTING);
        cellStyle.setFont(customFont);

        // Set alignment to centered
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);
    }

    public String formatMonthYear(String month, String year) {
        // Convert month to uppercase and get the first three letters
        String monthAbbreviation = month.substring(0, 3).toUpperCase();

        // Get the last two characters of the year
        String yearAbbreviation = year.substring(year.length() - 2);

        // Concatenate the formatted month and year
        String formatted = monthAbbreviation + "'" + yearAbbreviation;

        return formatted;
    }
}
