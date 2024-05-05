package com.it332.principal.DTO;

import javax.validation.constraints.Min;

import com.it332.principal.Models.Documents;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentsPatch {
    @Min(value = 0, message = "Budget must be a non-negative number")
    private Double budget;

    @Min(value = 0, message = "Budget must be a non-negative number")
    private Double budgetLimit;

    @Min(value = 0, message = "Budget must be a non-negative number")
    private Double cashAdvance;

    private boolean budgetExceeded;

    private String sds;

    private String claimant;

    private String headAccounting;

    public DocumentsPatch() {
    }

    public DocumentsPatch(Documents document) {
        this.setBudget(document.getBudget());
        this.setBudgetLimit(document.getBudgetLimit());
        this.setCashAdvance(document.getCashAdvance());
        this.setBudgetExceeded(document.isBudgetExceeded());
        this.setSds(document.getSds());
        this.setClaimant(document.getClaimant());
        this.setHeadAccounting(document.getHeadAccounting());
    }

}
