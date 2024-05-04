package com.it332.principal.DTO;

import com.it332.principal.Models.Documents;
import com.it332.principal.Models.School;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DocumentsResponse {
    private String id;

    private String school;

    private String month;

    private String year;

    private Double budget;

    private Double budgetLimit;

    private Double cashAdvance;

    private boolean budgetExceeded;

    private String sds;

    private String claimant;

    private String headAccounting;

    public DocumentsResponse() {
    }

    public DocumentsResponse(School school, Documents document) {
        setId(document.getId());
        setSchool(school.getName());
        setMonth(document.getMonth());
        setYear(document.getYear());
        setBudget(document.getBudget());
        setBudgetLimit(document.getBudgetLimit());
        setCashAdvance(document.getCashAdvance());
        setBudgetExceeded(document.isBudgetExceeded());
        setSds(document.getSds());
        setClaimant(document.getClaimant());
        setHeadAccounting(document.getHeadAccounting());
    }

}
