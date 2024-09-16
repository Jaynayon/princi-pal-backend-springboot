package com.it332.principal.Services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.it332.principal.DTO.DocumentsResponse;
import com.it332.principal.DTO.ExcelRequest;
import com.it332.principal.DTO.JEVResponse;
import com.it332.principal.Models.School;
import com.it332.principal.Models.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.io.ByteArrayOutputStream;

@Service
public class ExportJEVService {
    @Autowired
    UserService userService;

    @Autowired
    JEVService jevService;

    @Autowired
    DocumentsService documentsService;

    @Autowired
    SchoolService schoolService;

    public byte[] generateJEVData(ExcelRequest request) throws IOException {
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
