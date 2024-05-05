package com.it332.principal.DTO;

import com.it332.principal.Models.LR;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LRResponse {
    private String date;
    private String orsBursNo;
    private String particulars;
    private Double amount;

    public LRResponse() {
    }

    public LRResponse(LR lr) {
        this.setDate(lr.getDate());
        this.setOrsBursNo(lr.getOrsBursNo());
        this.setParticulars(lr.getParticulars());
        this.setAmount(lr.getAmount());
    }
}
