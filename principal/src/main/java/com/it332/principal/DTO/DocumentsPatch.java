package com.it332.principal.DTO;

import com.it332.principal.Models.Documents;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentsPatch {
    private Double budget;

    private Double budgetLimit;

    private Double cashAdvance;

    private boolean budgetExceeded;

    private String sds;

    private String claimant;

    private String headAccounting;

    public DocumentsPatch() {
    }

    /*
     * public DocumentsPatch(Documents document) {
     * setBudget(document.getBudget());
     * setBudgetLimit(document.getBudgetLimit());
     * setCashAdvance(document.getCashAdvance());
     * setBudgetExceeded(document.isBudgetExceeded());
     * setSds(document.getSds());
     * setClaimant(document.getClaimant());
     * setHeadAccounting(document.getHeadAccounting());
     * }
     */

}
