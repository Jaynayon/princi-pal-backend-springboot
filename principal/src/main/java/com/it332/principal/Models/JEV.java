package com.it332.principal.Models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.it332.principal.DTO.JEVRequest;

@Document(collection = "JEV")
public class JEV {
    @Id
    private String id;
    @NotBlank
    @NotNull
    private Uacs uacs;
    @NotBlank
    @NotNull
    private String documentsId;
    private String amountType;
    private Float amount;
    private Float budget;

    public JEV(JEVRequest jev, Uacs uacs) {
        this.uacs = uacs;
        this.documentsId = jev.getDocumentsId();
        setBudget(jev.getBudget());
        setAmountType(jev.getAmountType());
        setAmount(jev.getAmount());
    }

    public JEV() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uacs getUacs() {
        return uacs;
    }

    public void setUacs(Uacs uacs) {
        this.uacs = uacs;
    }

    public String getDocumentsId() {
        return documentsId;
    }

    public void setDocumentsId(String documentsId) {
        this.documentsId = documentsId;
    }

    public String getAmountType() {
        return amountType;
    }

    public void setAmountType(String amountType) {
        if (amountType == null) {
            this.amountType = "Credit";
        } else
            this.amountType = amountType;

    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        if (amount == null) {
            this.amount = Float.parseFloat("0");
        } else
            this.amount = amount;
    }

    public Float getBudget() {
        return budget;
    }

    public void setBudget(Float budget) {
        if (budget == null) {
            this.budget = Float.parseFloat("0");
        } else
            this.budget = budget;
    }

}
