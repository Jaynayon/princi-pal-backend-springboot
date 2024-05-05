package com.it332.principal.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "LR")
public class LR {
    @Id
    private String id;
    private String documentsId;
    private String date;
    private String orsBursNo;
    private String particulars;
    private double amount;

    // Constructor
    public LR(String date, String orsBursNo, String particulars, double amount) {
        this.date = date;
        this.orsBursNo = orsBursNo;
        this.particulars = particulars;
        this.amount = amount;
    }

    // Getters and setters
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrsBursNo() {
        return orsBursNo;
    }

    public void setOrsBursNo(String orsBursNo) {
        this.orsBursNo = orsBursNo;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDocumentsId() {
        return documentsId;
    }

    public void setDocumentsId(String documentsId) {
        this.documentsId = documentsId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
