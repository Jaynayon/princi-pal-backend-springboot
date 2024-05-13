package com.it332.principal.Controllers;

import com.it332.principal.DTO.ExcelRequest;
//import com.it332.principal.DTO.ErrorMessage;
import com.it332.principal.Security.NotFoundException;
import com.it332.principal.Services.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @PostMapping("/downloadExcel")
    public ResponseEntity<byte[]> downloadExcel(@RequestBody ExcelRequest request) {
        try {
            // Generate Excel file content as byte array
            byte[] excelBytes = excelService.generateLRData(request);

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
}
