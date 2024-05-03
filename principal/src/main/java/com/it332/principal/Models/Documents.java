package com.it332.principal.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.*;

@Document(collection = "Documents")
public class Documents {

    @Id
    private String id;

    private String school; // Store ObjectId as String for referencing

    @NotBlank
    private String month;

    @NotBlank
    private String year;

    @Min(value = 0, message = "Budget must be a non-negative number")
    @Max(value = 999999, message = "Budget must not exceed 999999")
    private Double budget;

    @Min(value = 0, message = "Budget limit must be a non-negative number")
    @Max(value = 999999, message = "Budget limit must not exceed 999999")
    private Double budgetLimit;

    @Min(value = 0, message = "Cash advance must be a non-negative number")
    @Max(value = 999999, message = "Cash advance must not exceed 999999")
    private Double cashAdvance;

    private boolean budgetExceeded;

    private String sds;

    private String claimant;

    private String headAccounting;

    public Documents() {
    }

    public Documents(String school, @NotBlank String month, @NotBlank String year,
            @Min(value = 0, message = "Budget must be a non-negative number") @Max(value = 999999, message = "Budget must not exceed 999999") Double budget,
            @Min(value = 0, message = "Budget limit must be a non-negative number") @Max(value = 999999, message = "Budget limit must not exceed 999999") Double budgetLimit,
            @Min(value = 0, message = "Cash advance must be a non-negative number") @Max(value = 999999, message = "Cash advance must not exceed 999999") Double cashAdvance,
            boolean budgetExceeded, String sds, String claimant, String headAccounting) {
        this.school = school;
        this.month = month;
        this.year = year;
        this.budget = budget;
        this.budgetLimit = budgetLimit;
        this.cashAdvance = cashAdvance;
        this.budgetExceeded = budgetExceeded;
        this.sds = sds;
        this.claimant = claimant;
        this.headAccounting = headAccounting;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
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
        this.sds = sds;
    }

    public String getClaimant() {
        return claimant;
    }

    public void setClaimant(String claimant) {
        this.claimant = claimant;
    }

    public String getHeadAccounting() {
        return headAccounting;
    }

    public void setHeadAccounting(String headAccounting) {
        this.headAccounting = headAccounting;
    }

}
