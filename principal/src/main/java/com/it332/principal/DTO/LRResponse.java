package com.it332.principal.DTO;

import com.it332.principal.Models.LR;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LRResponse {
    private String id;
    private String date;
    private String orsBursNo;
    private String particulars;
    private Double amount;
    private String objectCode;
    private String payee;
    private String natureOfPayment;

    public LRResponse() {
    }

    public LRResponse(LR lr) {
        this.setDate(lr.getDate());
        this.setOrsBursNo(lr.getOrsBursNo());
        this.setParticulars(lr.getParticulars());
        this.setAmount(lr.getAmount());
        this.setObjectCode(lr.getObjectCode());
        this.setPayee(lr.getPayee());
        this.setNatureOfPayment(lr.getNatureOfPayment());
    }
}
