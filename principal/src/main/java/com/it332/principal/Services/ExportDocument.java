package com.it332.principal.Services;

import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.it332.principal.DTO.DocumentsResponse;
import com.it332.principal.DTO.ExcelRequest;
import com.it332.principal.DTO.JEVResponse;
import com.it332.principal.DTO.LRResponse;
import com.it332.principal.Models.School;
import com.it332.principal.Models.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.io.ByteArrayOutputStream;

@Service
public class ExportDocument {
    @Autowired
    UserService userService;

    @Autowired
    LRService lrService;

    @Autowired
    JEVService jevService;

    @Autowired
    DocumentsService documentsService;

    @Autowired
    SchoolService schoolService;

    public byte[] generateData(ExcelRequest request) throws IOException {
        // Data to write
        School school = schoolService.getSchoolById(request.getSchoolId());
        // List<LRResponse> dataToWrite =
        // lrService.getAllLRsByDocumentsId(request.getDocumentId());
        DocumentsResponse document = documentsService.getDocumentBySchoolYearMonth(
                request.getSchoolId(),
                request.getYear(),
                request.getMonth());
        List<JEVResponse> dataToWrite = jevService.getAllJEVsByDocumentsId(document.getId());
        User user = userService.getUserById(request.getUserId());

        // Load template workbook from the classpath
        ClassPathResource resource = new ClassPathResource("Templates/JEV-2024.xlsx");

        // LR Automation
        try (InputStream inputStream = resource.getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)) {

            // Rename the sheet
            // Formats sheet name to ex: JAN'24
            workbook.setSheetName(0, formatMonthYear(
                    request.getMonth(),
                    request.getYear()) + " (JEV)");

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Set Entity/School name
            setJEVEntity(sheet, workbook, school.getFullName(), 6, 2);

            // Set Prepared and Cert. Cor.
            setJEVPeopleCells(sheet, workbook,
                    user.getFname() + " " + user.getMname().substring(0, 1) + ". " + user.getLname(),
                    29, 1);
            setJEVPeopleCells(sheet, workbook, document.getHeadAccounting(), 29, 4);

            // Set User position
            setJEVPeoplePosition(sheet, workbook, user.getPosition(), 30, 1);

            // Set note
            setJEVNote(sheet, workbook, school.getFullName(), document.getClaimant(), document.getMonth(),
                    document.getYear(), 22, 2);

            int rowIndex = 12; // Start from row 13 (zero-based index)

            // Initialize the cell styles array
            CellStyle[] cellStyles = new CellStyle[11]; // For columns A-K (index 0-9)

            // Initialize and customize styles for the required columns (skipping B and D)
            for (int i = 0; i < cellStyles.length; i++) {
                // Initialize cell style
                cellStyles[i] = workbook.createCellStyle();

                // Create a new font with Century Gothic and font size 20
                Font font = workbook.createFont();
                font.setFontName("Century Gothic");
                font.setFontHeightInPoints((short) 10); // Font size 20
                // Set font color to black
                font.setColor(IndexedColors.BLACK.getIndex());

                // Apply the new font to the cell style
                cellStyles[i].setFont(font);

                // ORS/BURS, Payee, and Amount custom alignments
                cellStyles[i].setVerticalAlignment(VerticalAlignment.CENTER); // Default vert. alignment

                if (i == 2) {
                    if (i == dataToWrite.size() - 1) {
                        cellStyles[i].setAlignment(HorizontalAlignment.RIGHT);
                    }
                } else {
                    cellStyles[i].setAlignment(HorizontalAlignment.CENTER);
                }

                // Set borders (all borders)
                cellStyles[i].setBorderTop(BorderStyle.THIN);
                cellStyles[i].setBorderBottom(BorderStyle.THIN);
                cellStyles[i].setBorderLeft(BorderStyle.THIN);
                if (i == cellStyles.length - 1) {
                    cellStyles[i].setBorderRight(BorderStyle.MEDIUM);
                } else {
                    cellStyles[i].setBorderRight(BorderStyle.THIN);
                }

                // Apply number format with two decimal places to column 4 (Amount)
                if (i > 5) {
                    DataFormat format = workbook.createDataFormat();
                    cellStyles[i].setDataFormat(format.getFormat("#,##0.00")); // Set decimal format
                    // Set alignment to centered
                    cellStyles[i].setAlignment(HorizontalAlignment.CENTER);
                }
            }

            // Write values to specific cells and apply the correct cell styles
            for (JEVResponse data : dataToWrite) {
                Row row = sheet.createRow(rowIndex);

                // Populate cells and apply styles
                for (int i = 0; i < 11; i++) {
                    Cell cell = row.createCell(i);
                    switch (i) {
                        case 2:
                            cell.setCellValue(data.getUacsName());
                            break;
                        case 4:
                            cell.setCellValue(data.getUacsCode());
                            break;
                        case 6:
                            if (!data.getUacsCode().equals("1990101000")) {
                                cell.setCellValue(data.getAmount());
                            }
                            break;
                        case 9:
                            if (data.getUacsCode().equals("1990101000")) {
                                cell.setCellValue(data.getAmount());
                            }
                            break;
                        default:
                            break;
                    }

                    // Apply cell style from the template
                    if (cellStyles[i] != null) {
                        // Only apply on Accounts and Explanation column/cell
                        if (i == 2) {
                            if (data.getUacsCode().equals("1990101000")) {
                                // Create a new custom style for right alignment
                                CellStyle customRightAlignedStyle = workbook.createCellStyle();
                                customRightAlignedStyle.cloneStyleFrom(cellStyles[i]); // Copy the base style
                                customRightAlignedStyle.setAlignment(HorizontalAlignment.RIGHT); // Set right alignment
                                cell.setCellStyle(customRightAlignedStyle); // Apply custom style
                            } else {
                                cell.setCellStyle(cellStyles[i]);
                            }
                        } else {
                            cell.setCellStyle(cellStyles[i]);
                        }
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

    public byte[] generateLRData(ExcelRequest request) throws IOException {
        // Data to write
        School school = schoolService.getSchoolById(request.getSchoolId());
        List<LRResponse> dataToWrite = lrService.getAllApprovedLRsByDocumentsId(request.getDocumentId());
        DocumentsResponse document = documentsService.getDocumentBySchoolYearMonth(
                request.getSchoolId(),
                request.getYear(),
                request.getMonth());

        // Load template workbook from the classpath
        ClassPathResource resource = new ClassPathResource("Templates/LR-2024.xlsx");

        // LR Automation
        try (InputStream inputStream = resource.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);

            // Rename the sheet
            // Formats sheet name to ex: JAN'24
            workbook.setSheetName(0, formatMonthYear(
                    request.getMonth(),
                    request.getYear()));

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            // Set Entity Name
            setEntityNameCells(sheet, workbook, school.getFullName(), 7, 1);

            // Set Cash Advance Cell
            setCashAdvCells(sheet, workbook, document.getCashAdvance(), 90, 4);

            // Claimant, SDS, and Head Accounting cells
            setPeopleCells(sheet, workbook, document.getClaimant(), 96, 1);
            setPeopleCells(sheet, workbook, document.getSds(), 96, 3);
            setPeopleCells(sheet, workbook, document.getHeadAccounting(), 96, 4);

            int rowIndex = 12; // Start from row 13 (zero-based index)

            // Get the cell styles from the template
            CellStyle[] cellStyles = new CellStyle[5]; // Assuming you have up to 5 columns

            // Retrieve and store the styles from the header row (assuming row 9 is the
            // header row)
            Row headerRow = sheet.getRow(8);
            for (int i = 1; i <= 4; i++) {
                cellStyles[i] = headerRow.getCell(i).getCellStyle();

                // Create a new font with Century Gothic and font size 20
                Font font = workbook.createFont();
                font.setFontName("Century Gothic");
                font.setFontHeightInPoints((short) 20); // Font size 20
                // Set font color to black
                font.setColor(IndexedColors.BLACK.getIndex());
                // Apply the new font to the cell style
                cellStyles[i].setFont(font);

                // Set borders (all borders)
                cellStyles[i].setBorderTop(BorderStyle.THIN);
                cellStyles[i].setBorderBottom(BorderStyle.THIN);
                cellStyles[i].setBorderRight(BorderStyle.MEDIUM);
                cellStyles[i].setBorderLeft(BorderStyle.MEDIUM);

                // Apply number format with two decimal places to column 4 (Amount)
                if (i == 4) {
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
                for (int i = 1; i <= 4; i++) {
                    Cell cell = row.createCell(i);
                    switch (i) {
                        case 1:
                            cell.setCellValue(data.getDate());
                            break;
                        case 2:
                            cell.setCellValue(data.getOrsBursNo());
                            break;
                        case 3:
                            cell.setCellValue(data.getParticulars());
                            break;
                        case 4:
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
            workbook.setPrintArea(workbook.getSheetIndex(sheetName), 0, 4, 2, 100);

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

    public void setJEVEntity(Sheet sheet, Workbook workbook, String name, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "JOHN DOE"
        cell.setCellValue(name.toUpperCase());

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();

        Font customFont = workbook.createFont();
        customFont.setFontName("Century Gothic");
        customFont.setFontHeightInPoints((short) 13);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setBold(true);
        cellStyle.setFont(customFont);

        // Set alignment to centered
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);
    }

    public void setJEVPeoplePosition(Sheet sheet, Workbook workbook, String name, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "JOHN DOE"
        cell.setCellValue(name.toUpperCase());

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();

        Font customFont = workbook.createFont();
        customFont.setFontName("Century Gothic");
        customFont.setFontHeightInPoints((short) 11);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        cellStyle.setFont(customFont);

        // Set alignment to centered
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);
    }

    public void setJEVPeopleCells(Sheet sheet, Workbook workbook, String name, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "JOHN DOE"
        cell.setCellValue(name.toUpperCase());

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();

        Font customFont = workbook.createFont();
        customFont.setFontName("Century Gothic");
        customFont.setFontHeightInPoints((short) 11);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setUnderline(Font.U_SINGLE);
        customFont.setBold(true);
        cellStyle.setFont(customFont);

        // Set alignment to centered
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);
    }

    public static String toTitleCase(String input) {
        // Split the input string into words
        String[] words = input.toLowerCase().split(" ");

        StringBuilder titleCase = new StringBuilder();

        // Loop through each word
        for (String word : words) {
            if (word.length() > 0) {
                // Capitalize the first letter and keep the rest of the word lowercase
                titleCase.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1))
                        .append(" ");
            }
        }

        // Trim the final string to remove any extra space
        return titleCase.toString().trim();
    }

    public void setJEVNote(Sheet sheet, Workbook workbook, String school, String claimant, String month, String year,
            int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "John Doe"
        cell.setCellValue("MOOE Liquidation of " +
                toTitleCase(school) + " (" + claimant + ") for the month of "
                + month + " " + year + ".");

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();

        Font customFont = workbook.createFont();
        customFont.setFontName("Century Gothic");
        customFont.setFontHeightInPoints((short) 10);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setItalic(true);
        cellStyle.setFont(customFont);

        // Set alignment to centered
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);

        // Enable text wrapping
        cellStyle.setWrapText(true);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);

        // Adjust the row height to fit the wrapped text
        customRow.setHeight((short) -1); // -1 will auto-fit the row height
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

    public void setEntityNameCells(Sheet sheet, Workbook workbook, String name, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "John Doe"
        cell.setCellValue("Entity Name: " + name);

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();
        Font customFont = workbook.createFont();

        customFont.setFontName("Century Gothic");
        customFont.setFontHeightInPoints((short) 24);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setBold(true);
        cellStyle.setFont(customFont);

        // Set alignment to centered
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);
    }

    public void setCashAdvCells(Sheet sheet, Workbook workbook, Double amount, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Create a DataFormat object from the workbook
        DataFormat format = workbook.createDataFormat();

        // Set value to "John Doe"
        cell.setCellValue(amount);

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();
        Font customFont = workbook.createFont();
        cellStyle.setDataFormat(format.getFormat("#,##0.00"));
        customFont.setFontName("Century Gothic");
        customFont.setFontHeightInPoints((short) 16);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setBold(true);
        cellStyle.setFont(customFont);

        // Set alignment to centered
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);
        cellStyle.setBorderTop(BorderStyle.MEDIUM);
        cellStyle.setBorderBottom(BorderStyle.MEDIUM);

        // Apply the new CellStyle to cell B96
        cell.setCellStyle(cellStyle);
    }

    public void setPeopleCells(Sheet sheet, Workbook workbook, String name, int rowNo, int colNo) {
        // Find the specific cell to alter (B96)
        Row customRow = sheet.getRow(rowNo - 1); // Row index 95 (zero-based) is row 96 in Excel
        Cell cell = customRow.getCell(colNo); // Cell B96

        // Set value to "John Doe"
        cell.setCellValue(name);

        // Create a new CellStyle for this specific cell
        CellStyle cellStyle = workbook.createCellStyle();
        Font customFont = workbook.createFont();
        customFont.setFontName("Century Gothic");
        customFont.setFontHeightInPoints((short) 20);
        customFont.setColor(IndexedColors.BLACK.getIndex());
        customFont.setBold(true);
        customFont.setUnderline(Font.U_SINGLE);
        cellStyle.setFont(customFont);

        // Set alignment to centered
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setBorderLeft(BorderStyle.MEDIUM);
        cellStyle.setBorderRight(BorderStyle.MEDIUM);

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
