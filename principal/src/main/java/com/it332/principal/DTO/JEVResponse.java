package com.it332.principal.DTO;

import com.it332.principal.Models.JEV;

public class JEVResponse {
    private String id;
    private String uacsCode;
    private String uacsName;
    private String documentsId;
    private String amountType;
    private Float amount;

    public JEVResponse() {
    }

    public JEVResponse(String id, String uacsCode, String uacsName, String documentsId, String amountType,
            Float amount) {
        this.id = id;
        this.uacsCode = uacsCode;
        this.uacsName = uacsName;
        this.documentsId = documentsId;
        this.amountType = amountType;
        this.amount = amount;
    }

    public JEVResponse(JEV jev) {
        setId(jev.getId());
        setUacsCode(jev.getUacs().getCode());
        setUacsName(jev.getUacs().getName());
        setDocumentsId(jev.getDocumentsId());
        setAmountType(jev.getAmountType());
        setAmount(jev.getAmount());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUacsCode() {
        return uacsCode;
    }

    public void setUacsCode(String uacsCode) {
        this.uacsCode = uacsCode;
    }

    public String getUacsName() {
        return uacsName;
    }

    public void setUacsName(String uacsName) {
        this.uacsName = uacsName;
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
        this.amountType = amountType;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

}
