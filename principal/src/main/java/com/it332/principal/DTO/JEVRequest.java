package com.it332.principal.DTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class JEVRequest {
    @NotBlank
    @NotNull
    private String objectCode;
    @NotBlank
    @NotNull
    private String documentsId;
    private Boolean credit;
    private Float amount;

    public JEVRequest() {
    }

    public JEVRequest(@NotBlank @NotNull String objectCode, @NotBlank @NotNull String documentsId, Boolean credit,
            Float amount) {
        this.objectCode = objectCode;
        this.documentsId = documentsId;
        setCredit(credit);
        setAmount(amount);
    }

    public JEVRequest(@NotBlank @NotNull String objectCode, @NotBlank @NotNull String documentsId) {
        this.objectCode = objectCode;
        this.documentsId = documentsId;
        setCredit(true);
        setAmount(Float.parseFloat("0"));
    }

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    public String getDocumentsId() {
        return documentsId;
    }

    public void setDocumentsId(String documentsId) {
        this.documentsId = documentsId;
    }

    public Boolean getCredit() {
        return credit;
    }

    public void setCredit(Boolean credit) {
        if (credit == null) {
            this.credit = true;
        } else
            this.credit = credit;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

}
