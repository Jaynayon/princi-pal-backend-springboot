package com.it332.principal.Services;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.it332.principal.Models.LR;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import java.nio.file.Paths;
import java.io.File;
import java.io.FileNotFoundException;

@Service
public class ExcelService {

    public void insertLRData() throws IOException {
        // Mock LR data for testing
        List<LR> dataToWrite = new ArrayList<>();
        dataToWrite.add(new LR("11/11/2023", "SI# 2056",
                "TINONGS FOOD INTRNL- Purchased torta bread (small) for District Meet", 3124));
        dataToWrite.add(new LR("12/12/2023", "SI# 2057", "Example Particulars", 2500));
        dataToWrite.add(new LR("12/12/2023", "SI# 2057", "Example Particulars", 4000));

        // School name for output file naming
        String schoolName = "Jaclupan";

        // Load template workbook from the classpath
        ClassPathResource resource = new ClassPathResource("Templates/LR-2024.xlsx");
        try (InputStream inputStream = resource.getInputStream()) {
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

            int rowIndex = 12; // Start from row 13 (zero-based index)

            // Populate or change the value of a certain cell (e.g., B9)
            Row headerRow = sheet.getRow(8);
            Cell cellB9 = headerRow.getCell(1); // Cell B9
            cellB9.setCellValue(2);

            // Write values to specific cells and retain cell styling
            for (LR data : dataToWrite) {
                Row row = sheet.createRow(rowIndex);

                row.createCell(1).setCellValue(data.getDate());
                row.createCell(2).setCellValue(data.getOrsBursNo());
                row.createCell(3).setCellValue(data.getParticulars());
                row.createCell(4).setCellValue(data.getAmount());

                rowIndex++;
            }
            // Get the absolute path to the project's root directory
            String projectRootPath = System.getProperty("user.dir");

            // Define the output directory path within the project's resources
            String outputDirectoryPath = projectRootPath + "/src/main/resources/Output/";

            // Create the output directory if it doesn't exist
            File outputDirectory = new File(outputDirectoryPath);
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            // Define the absolute output file path
            String outputFilePath = outputDirectoryPath + schoolName + "_LR-2024.xlsx";

            // Save the workbook to the output file
            try (FileOutputStream fileOut = new FileOutputStream(outputFilePath)) {
                workbook.write(fileOut);
            }

            System.out.println("Values written to Excel file: " + outputFilePath);
            workbook.close();

        }
    }
}
