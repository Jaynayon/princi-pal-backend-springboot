package com.it332.principal.DTO;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.it332.principal.Models.LR;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LRResponse {
    private String id;

    // @JsonFormat(pattern = "MM/dd/yyyy")
    private String date;

    private String orsBursNo;
    private String particulars;
    private Double amount;
    private String objectCode;
    private String payee;
    private String natureOfPayment;

    public LRResponse(LR lr) {
        this.id = lr.getId();
        this.date = lr.getDate();
        this.orsBursNo = lr.getOrsBursNo();
        this.particulars = lr.getParticulars();
        this.amount = lr.getAmount();
        this.objectCode = lr.getObjectCode();
        this.payee = lr.getPayee();
        this.natureOfPayment = lr.getNatureOfPayment();
    }
}
