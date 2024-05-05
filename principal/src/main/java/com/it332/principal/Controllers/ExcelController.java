package com.it332.principal.Controllers;

import com.it332.principal.Models.LR;
import com.it332.principal.Services.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @GetMapping("/downloadExcel")
    public ResponseEntity<byte[]> downloadExcel() {
        try {
            // Mock LR data for testing
            List<LR> dataToWrite = new ArrayList<>();
            dataToWrite.add(new LR("11/11/2023", "SI# 2056",
                    "TINONGS FOOD INTRNL- Purchased torta bread (small) for District Meet", 3124));
            dataToWrite.add(new LR("12/12/2023", "SI# 2057", "Example Particulars", 2500));
            dataToWrite.add(new LR("12/12/2023", "SI# 2057", "Example Particulars", 5000));

            // Generate Excel file content as byte array
            byte[] excelBytes = excelService.generateLRData(dataToWrite);

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
        }
    }
}
