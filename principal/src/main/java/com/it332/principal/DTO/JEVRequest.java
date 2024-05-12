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
    private String amountType;
    private Float amount;

    public JEVRequest() {
    }

    public JEVRequest(@NotBlank @NotNull String objectCode, @NotBlank @NotNull String documentsId, String amountType,
            Float amount) {
        this.objectCode = objectCode;
        this.documentsId = documentsId;
        setAmountType(amountType);
        setAmount(amount);
    }

    public JEVRequest(@NotBlank @NotNull String objectCode, @NotBlank @NotNull String documentsId) {
        this.objectCode = objectCode;
        this.documentsId = documentsId;
        setAmountType("Credit");
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
        this.amount = amount;
    }

}
