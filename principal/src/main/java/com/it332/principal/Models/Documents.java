package com.it332.principal.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.it332.principal.DTO.DocumentsRequest;

import javax.validation.constraints.*;

//@Data and @AllArgsConstructor to i think create getters and setters
@Document(collection = "Documents")
public class Documents {

    @Id
    private String id;

    @NotBlank
    private String schoolId;

    @NotBlank
    private String month;

    @NotBlank
    private String year;

    @Min(value = 0, message = "Budget must be a non-negative number")
    private Double budget = 0.0;

    @Min(value = 0, message = "Budget limit must be a non-negative number")
    private Double budgetLimit = 0.0;

    @Min(value = 0, message = "Cash advance must be a non-negative number")
    private Double cashAdvance = 0.0;

    @Min(value = 0, message = "Annual budget must be a non-negative number")
    private Double annualBudget = 0.0;

    @Min(value = 0, message = "Annual expense must be a non-negative number")
    private Double annualExpense = 0.0;

    private boolean budgetExceeded;

    private boolean budgetLimitExceeded;

    private String sds;

    private String claimant;

    private String headAccounting;

    public Documents() {
    }

    public Documents(DocumentsRequest doc) {
        setSchoolId(doc.getSchoolId());
        setMonth(doc.getMonth());
        setYear(doc.getYear());
        setAnnualBudget(doc.getAnnualBudget());
        setBudgetLimit(doc.getBudgetLimit());
        setBudgetExceeded(false);
        setBudgetLimitExceeded(false);
        setSds(doc.getSds());
        setClaimant(doc.getClaimant());
        setHeadAccounting(doc.getHeadAccounting());
    }

    public Documents(String schoolId, @NotBlank String month, @NotBlank String year) {
        this.schoolId = schoolId;
        this.month = month;
        this.year = year;
        this.budget = 0.0;
        this.budgetLimit = 0.0;
        this.cashAdvance = 0.0;
        this.annualBudget = 0.0;
        this.budgetExceeded = false;
        this.budgetLimitExceeded = false;
        setSds("");
        setClaimant("");
        setHeadAccounting("");
    }

    public Documents(String schoolId, @NotBlank String month, @NotBlank String year,
            @Min(value = 0, message = "Budget limit must be a non-negative number") Double budgetLimit,
            @Min(value = 0, message = "Cash advance must be a non-negative number") Double cashAdvance,
            @Min(value = 0, message = "Annual budget must be a non-negative number") Double annualBudget,
            boolean budgetExceeded, boolean budgetLimitExceeded, String sds, String claimant, String headAccounting) {
        this.schoolId = schoolId;
        this.month = month;
        this.year = year;
        this.budgetLimit = budgetLimit;
        this.cashAdvance = cashAdvance;
        this.annualBudget = annualBudget;
        this.budgetExceeded = budgetExceeded;
        this.budgetLimitExceeded = budgetLimitExceeded;
        setSds(sds);
        setClaimant(claimant);
        setHeadAccounting(headAccounting);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getAnnualBudget() {
        return annualBudget;
    }

    public void setAnnualBudget(Double annualBudget) {
        this.annualBudget = annualBudget;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String school) {
        this.schoolId = school;
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

    public boolean isBudgetLimitExceeded() {
        return budgetLimitExceeded;
    }

    public void setBudgetLimitExceeded(boolean budgetLimitExceeded) {
        this.budgetLimitExceeded = budgetLimitExceeded;
    }

    public Double getCashAdvance() {
        return cashAdvance;
    }

    public void setCashAdvance(Double cashAdvance) {
        this.cashAdvance = cashAdvance;
    }

    public boolean isBudgetExceeded() {
        return budgetExceeded;
    }

    public void setBudgetExceeded(boolean budgetExceeded) {
        this.budgetExceeded = budgetExceeded;
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

    public Double getAnnualExpense() {
        return annualExpense;
    }

    public void setAnnualExpense(Double annualExpense) {
        this.annualExpense = annualExpense;
    }

}
