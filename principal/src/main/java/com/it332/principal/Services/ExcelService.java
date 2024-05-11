package com.it332.principal.Services;

import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.it332.principal.DTO.DocumentsResponse;
import com.it332.principal.DTO.ExcelRequest;
import com.it332.principal.DTO.LRResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.io.ByteArrayOutputStream;

@Service
public class ExcelService {

    @Autowired
    LRService lrService;

    @Autowired
    DocumentsService documentsService;

    public byte[] generateLRData(ExcelRequest request) throws IOException {
        // Data to write
        List<LRResponse> dataToWrite = lrService.getAllLRsByDocumentsId(request.getDocumentId());
        DocumentsResponse document = documentsService.getDocumentBySchoolYearMonth(
                request.getSchoolId(),
                request.getYear(),
                request.getMonth());

        // Load template workbook from the classpath
        ClassPathResource resource = new ClassPathResource("Templates/LR-2024.xlsx");
        try (InputStream inputStream = resource.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);

            // Rename the sheet
            // Formats sheet name to ex: JAN'24
            workbook.setSheetName(0, formatMonthYear(
                    request.getMonth(),
                    request.getYear()));

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

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

            // Write workbook to ByteArrayOutputStream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            // workbook.close();
            return outputStream.toByteArray(); // Return the byte array

        }
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
