package com.it332.principal.DTO;

public class DocumentsRequest {
    private String schoolId;
    private String month;
    private String year;
    private Double budget;
    private Double budgetLimit;
    private Double cashAdvance;
    private Double annualBudget;
    private String sds;
    private String claimant;
    private String headAccounting;

    public DocumentsRequest() {
    }

    public DocumentsRequest(String schoolId, String month, String year, Double budget, Double budgetLimit,
            Double cashAdvance, Double annualBudget, String sds, String claimant, String headAccounting) {
        this.schoolId = schoolId;
        this.month = month;
        this.year = year;
        this.budget = budget;
        this.budgetLimit = budgetLimit;
        this.cashAdvance = cashAdvance;
        this.annualBudget = annualBudget;
        this.sds = sds;
        this.claimant = claimant;
        this.headAccounting = headAccounting;
    }

    public DocumentsRequest(String schoolId, String month, String year) {
        this.schoolId = schoolId;
        this.month = month;
        this.year = year;
        setBudget(Double.parseDouble("0"));
        setBudgetLimit(Double.parseDouble("0"));
        setCashAdvance(Double.parseDouble("0"));
        setAnnualBudget(Double.parseDouble("0"));
        setSds("");
        setClaimant("");
        setHeadAccounting("");
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Double getBudgetLimit() {
        return budgetLimit;
    }

    public void setBudgetLimit(Double budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public Double getCashAdvance() {
        return cashAdvance;
    }

    public void setCashAdvance(Double cashAdvance) {
        this.cashAdvance = cashAdvance;
    }

    public String getSds() {
        return sds;
    }

    public void setSds(String sds) {
        if (sds == null) {
            this.sds = "";
        } else
            this.sds = sds;
    }

    public String getClaimant() {
        return claimant;
    }

    public void setClaimant(String claimant) {
        if (claimant == null) {
            this.claimant = "";
        } else
            this.claimant = claimant;
    }

    public String getHeadAccounting() {
        return headAccounting;
    }

    public void setHeadAccounting(String headAccounting) {
        if (headAccounting == null) {
            this.headAccounting = "";
        } else
            this.headAccounting = headAccounting;
    }

    public Double getAnnualBudget() {
        return annualBudget;
    }

    public void setAnnualBudget(Double annualBudget) {
        this.annualBudget = annualBudget;
    }

}
