package com.it332.principal.Services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.it332.principal.DTO.DocumentsResponse;
import com.it332.principal.DTO.ExcelRequest;
import com.it332.principal.DTO.LRResponse;
import com.it332.principal.Models.School;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.io.ByteArrayOutputStream;

@Service
public class ExportRCDService {

    @Autowired
    LRService lrService;

    @Autowired
    DocumentsService documentsService;

    @Autowired
    SchoolService schoolService;

    public byte[] generateRCDData(ExcelRequest request) throws IOException {
        // Data to write
        School school = schoolService.getSchoolById(request.getSchoolId());
        List<LRResponse> dataToWrite = lrService.getAllApprovedLRsByDocumentsId(request.getDocumentId());
        DocumentsResponse document = documentsService.getDocumentBySchoolYearMonth(
                request.getSchoolId(),
                request.getYear(),
                request.getMonth());

        // Load template workbook from the classpath
        ClassPathResource resource = new ClassPathResource("Templates/RCD-2024.xlsx");

        // LR Automation
        try (InputStream inputStream = resource.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {

            // Rename the sheet
            // Formats sheet name to ex: JAN'24
            workbook.setSheetName(0, formatMonthYear(
                    request.getMonth(),
                    request.getYear()) + " (RCD)");

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Set Period Covered
            setRCDPeriodCovered(sheet, workbook, document.getMonth(), document.getYear(), 3, 0);

            // Set Entity Name
            setRCDEntityNameCells(sheet, workbook, school.getFullName(), 6, 1);

            // Set Disbursing Officer
            setRCDPeopleCells(sheet, workbook, document.getClaimant(), 69, 4);

            int rowIndex = 11; // Start from row 12 (zero-based index)

            // Initialize the cell styles array
            CellStyle[] cellStyles = new CellStyle[8]; // For columns A-H (index 0-7)

            // Initialize and customize styles for the required columns (skipping B and D)
            for (int i = 0; i < cellStyles.length; i++) {
                // Initialize cell style
                cellStyles[i] = workbook.createCellStyle();

                // Create a new font with Century Gothic and font size 20
                Font font = workbook.createFont();
                font.setFontName("Century Gothic");
                font.setFontHeightInPoints((short) 16); // Font size 20
                // Set font color to black
                font.setColor(IndexedColors.BLACK.getIndex());

                // Apply the new font to the cell style
                cellStyles[i].setFont(font);

                // ORS/BURS, Payee, and Amount custom alignments
                cellStyles[i].setVerticalAlignment(VerticalAlignment.CENTER); // Default vert. alignment

                if (i == 2 || i == 4) {
                    cellStyles[i].setAlignment(HorizontalAlignment.LEFT);
                } else if (i == 7) {
                    cellStyles[i].setAlignment(HorizontalAlignment.RIGHT);
                } else {
                    cellStyles[i].setAlignment(HorizontalAlignment.CENTER); // Center align others
                }

                // Set borders (all borders)
                cellStyles[i].setBorderTop(BorderStyle.THIN);
                cellStyles[i].setBorderBottom(BorderStyle.THIN);
                cellStyles[i].setBorderRight(BorderStyle.MEDIUM);
                cellStyles[i].setBorderLeft(BorderStyle.MEDIUM);

                // Apply number format with two decimal places to column 4 (Amount)
                if (i == 7) {
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
                for (int i = 0; i < 8; i++) {
                    Cell cell = row.createCell(i);
                    switch (i) {
                        case 0:
                            cell.setCellValue(data.getDate());
                            break;
                        case 2:
                            cell.setCellValue(data.getOrsBursNo());
                            break;
                        case 4:
                            cell.setCellValue(data.getPayee());
                            break;
                        case 5:
                            cell.setCellValue(data.getObjectCode());
                            break;
                        case 6:
                            cell.setCellValue(data.getNatureOfPayment());
                            break;
                        case 7:
                            cell.setCellValue(data.getAmount());
                            break;
                        default:
                            break;
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
            workbook.setPrintArea(workbook.getSheetIndex(sheetName), 0, 7, 2, 100);

            // Write workbook to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // workbook.close();
            return outputStream.toByteArray(); // Return the byte array
        }
    }

    public void setRCDPeriodCovered(Sheet sheet, Workbook workbook, String month, String year, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "John Doe"
        cell.setCellValue("Period Covered " + month.substring(0, 3).toUpperCase() + " " + year);

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();
        Font customFont = workbook.createFont();

        customFont.setFontName("Century Gothic");
        customFont.setFontHeightInPoints((short) 12);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setBold(true);
        cellStyle.setFont(customFont);

        // Set alignment to centered
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);
    }

    public void setRCDPeopleCells(Sheet sheet, Workbook workbook, String name, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "John Doe"
        cell.setCellValue(name);

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();
        Font customFont = workbook.createFont();

        customFont.setFontName("Century Gothic");
        customFont.setFontHeightInPoints((short) 18);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setBold(true);
        cellStyle.setFont(customFont);

        // Set alignment to centered
        cellStyle.setVerticalAlignment(VerticalAlignment.BOTTOM);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderBottom(BorderStyle.THIN);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);
    }

    public void setRCDEntityNameCells(Sheet sheet, Workbook workbook, String name, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "John Doe"
        cell.setCellValue(name);

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();
        Font customFont = workbook.createFont();

        customFont.setFontName("Century Gothic");
        customFont.setFontHeightInPoints((short) 11);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setBold(true);
        cellStyle.setFont(customFont);

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
