package com.it332.principal.Models;

import com.it332.principal.DTO.JEVRequest;

public class LRJEV {
    private String id; // We'll be using the id of the uacs code
    private String uacsCode;
    private String uacsName;
    private String amountType = "Credit";
    private Double amount;

    public LRJEV(JEVRequest jev, Uacs uacs) {
        this.id = uacs.getId();
        this.uacsCode = uacs.getCode();
        this.uacsName = uacs.getName();
        setAmountType(jev.getAmountType());
    }

    public LRJEV(Uacs uacs) {
        this.id = uacs.getId();
        this.uacsCode = uacs.getCode();
        this.uacsName = uacs.getName();
        setAmount(0.0);
    }

    public LRJEV() {
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

    public String getAmountType() {
        return amountType;
    }

    public void setAmountType(String amountType) {
        if (amountType == null) {
            this.amountType = "Credit";
        } else
            this.amountType = amountType;

    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        if (amount == null) {
            this.amount = Double.parseDouble("0");
        } else
            this.amount = amount;
    }

}
