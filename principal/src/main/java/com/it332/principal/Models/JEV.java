package com.it332.principal.Models;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.it332.principal.DTO.JEVRequest;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
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
    private Boolean credit;
    private Float amount;

    public JEV(JEVRequest jev, Uacs uacs) {
        this.uacs = uacs;
        this.documentsId = jev.getDocumentsId();
        setCredit(jev.getCredit());
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
        if (amount == null) {
            this.amount = Float.parseFloat("0");
        } else
            this.amount = amount;
    }

}
