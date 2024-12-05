package com.it332.principal.Controllers;

import com.it332.principal.DTO.ExcelRequest;
//import com.it332.principal.DTO.ErrorMessage;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.ExportCDRService;
import com.it332.principal.Services.ExportDocument;
import com.it332.principal.Services.ExportJEVService;
import com.it332.principal.Services.ExportLRService;
import com.it332.principal.Services.ExportRCDService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@RestController
public class ExportController {

    @Autowired
    private ExportDocument exportService;

    @Autowired
    private ExportLRService exportLRService;

    @Autowired
    private ExportJEVService exportJEVService;

    @Autowired
    private ExportCDRService exportCDRService;

    @Autowired
    private ExportRCDService exportRCDService;

    @PostMapping("/api/downloadExcel")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody ExcelRequest request) {
        try {
            // Generate Excel file content as byte array
            byte[] excelBytes = exportService.generateData(request);

            // Set filename for download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "LR_Data.xlsx");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (IOException e) {
            e.printStackTrace(); // Handle exception appropriately
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        } catch (IllegalArgumentException e) {
            // This exception is thrown when a duplicate file name is detected
            // err.setMessage("Failed to get Document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(null);
        } catch (NotFoundException e) {
            // This exception is thrown when a no file is detected
            // err.setMessage("Failed to get Document: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        } catch (Exception e) {
            // Catching any other unexpected exceptions
            e.printStackTrace();
            // err.setMessage("Internal server error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/api/downloadZip")
    public ResponseEntity<byte[]> downloadZip(@RequestBody ExcelRequest request) {
        try {
            // Create a ByteArrayOutputStream to hold the zip content
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream);

            // List of files to be zipped
            // List<String> filenames = Arrays.asList("JEV.xlsx", "LR.xlsx", "CDR.xlsx",
            // "RCD.xlsx");

            // Generate and process each Excel file
            byte[] jevBytes = removeCalcChain(exportJEVService.generateJEVData(request));
            byte[] lrBytes = removeCalcChain(exportLRService.generateLRData(request));
            byte[] cdrBytes = removeCalcChain(exportCDRService.generateCDRData(request));
            byte[] rcdBytes = removeCalcChain(exportRCDService.generateRCDData(request));

            // Zip each file
            addToZip("JEV.xlsx", jevBytes, zipOut);
            addToZip("LR.xlsx", lrBytes, zipOut);
            addToZip("CDR.xlsx", cdrBytes, zipOut);
            addToZip("RCD.xlsx", rcdBytes, zipOut);

            // Close the zip stream
            zipOut.close();

            // Set headers for zip file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "Documents.zip");
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            // Return the zipped byte array as a response
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(byteArrayOutputStream.toByteArray());

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Utility method to add file to zip
    private void addToZip(String filename, byte[] fileContent, ZipOutputStream zipOut) throws IOException {
        ZipEntry zipEntry = new ZipEntry(filename);
        zipOut.putNextEntry(zipEntry);
        zipOut.write(fileContent);
        zipOut.closeEntry();
    }

    // Utility method to remove calcChain.xml from an Excel file
    private byte[] removeCalcChain(byte[] excelBytes) throws IOException {
        // Input and output streams for processing the Excel file
        ByteArrayInputStream inputStream = new ByteArrayInputStream(excelBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Process the file as a ZIP
        try (ZipInputStream zipIn = new ZipInputStream(inputStream);
                ZipOutputStream zipOut = new ZipOutputStream(outputStream)) {
            ZipEntry entry;

            while ((entry = zipIn.getNextEntry()) != null) {
                // Skip calcChain.xml
                if ("xl/calcChain.xml".equals(entry.getName())) {
                    continue;
                }
                // Copy other files to the output ZIP
                zipOut.putNextEntry(new ZipEntry(entry.getName()));
                byte[] buffer = new byte[1024];
                int len;
                while ((len = zipIn.read(buffer)) > 0) {
                    zipOut.write(buffer, 0, len);
                }
            }
        }

        // Return the modified Excel file as a byte array
        return outputStream.toByteArray();
    }

}
