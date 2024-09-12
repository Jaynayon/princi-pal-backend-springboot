package com.it332.principal.Controllers;

import com.it332.principal.DTO.ExcelRequest;
//import com.it332.principal.DTO.ErrorMessage;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.ExportDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
public class ExportController {

    @Autowired
    private ExportDocument exportService;

    @PostMapping("/downloadExcel")
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

    @PostMapping("/downloadZip")
    public ResponseEntity<byte[]> downloadZip(@RequestBody ExcelRequest request) {
        try {
            // Create a ByteArrayOutputStream to hold the zip content
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream);

            // List of files to be zipped
            List<String> filenames = Arrays.asList("JEV.xlsx", "LR.xlsx", "CDR.xlsx", "RCD.xlsx");

            // Generate each Excel file
            byte[] jevBytes = exportService.generateData(request);
            byte[] lrBytes = exportService.generateLRData(request);
            byte[] cdrBytes = exportService.generateCDRData(request);
            byte[] rcdBytes = exportService.generateRCDData(request);

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

}
