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

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Set Date
            setCDRDate(sheet, workbook, document.getMonth(), document.getYear(), 4, 0);

            // Set Entity Name
            setCDREntityNameCells(sheet, workbook, "Name of Accountable Officer: ", document.getClaimant(), 4, 9);
            setCDREntityNameCells(sheet, workbook, "Station: ", school.getFullName(), 6, 9);

            // Set Claimant and Cash Adv.
            setCDRHeader(sheet, workbook, document.getClaimant(), 14, 2);
            setCDRHeader(sheet, workbook, document.getCashAdvance(), 14, 3);

            // Set Cert. Cor. and Received by
            setCDRPeopleCells(sheet, workbook, document.getClaimant(), 88, 0);
            setCDRPeopleCells(sheet, workbook, userFullName, 88, 9);

            int rowIndex = 16; // Start from row 17 (zero-based index)

            // Initialize the cell styles array
            CellStyle[] cellStyles = new CellStyle[16]; // For columns A-H (index 0-7)

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
                if (i != 13 || i <= 3) {
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
                for (int i = 0; i < 16; i++) {
                    Cell cell = row.createCell(i);
                    switch (i) {
                        case 0:
                            cell.setCellValue(data.getDate());
                            break;
                        case 1:
                            cell.setCellValue(data.getOrsBursNo());
                            break;
                        case 2:
                            cell.setCellValue(data.getParticulars());
                            break;
                        case 4:
                            cell.setCellValue(data.getAmount());
                            break;
                        case 5:
                            // Set the formula dynamically based on the current rowIndex
                            if (rowIndex == 16) { // First row (for example, row 17 in Excel)
                                cell.setCellFormula("F14-E17"); // Example for the first row
                            } else {
                                // For subsequent rows, adjust the formula according to rowIndex
                                cell.setCellFormula("F" + (rowIndex) + "-E" + (rowIndex + 1));
                            }
                            break;
                        case 7:
                            if ("5020502001".equals(data.getObjectCode())) {
                                cell.setCellValue(data.getAmount());
                            }
                            break;
                        case 8:
                            if ("5020402000".equals(data.getObjectCode())) {
                                cell.setCellValue(data.getAmount());
                            }
                            break;
                        case 9:
                            if ("5020503000".equals(data.getObjectCode())) {
                                cell.setCellValue(data.getAmount());
                            }
                            break;
                        case 10:
                            if ("5029904000".equals(data.getObjectCode())) {
                                cell.setCellValue(data.getAmount());
                            }
                            break;
                        case 11:
                            if ("5020201000".equals(data.getObjectCode())) {
                                cell.setCellValue(data.getAmount());
                            }
                            break;
                        case 12:
                            // R & M school bldg. etc.
                            if ("5021304002".equals(data.getObjectCode())) {
                                cell.setCellValue(data.getAmount());
                            }
                            break;
                        case 15:
                            if ("5020399000".equals(data.getObjectCode())) {
                                cell.setCellValue(data.getAmount());
                            }
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
            workbook.setPrintArea(workbook.getSheetIndex(sheetName), 0, 15, 0, 89);

            // Write workbook to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            // workbook.close();
            return outputStream.toByteArray(); // Return the byte array
        }
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
