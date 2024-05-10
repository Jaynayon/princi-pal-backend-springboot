package com.it332.principal.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.it332.principal.DTO.LRRequest;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Document(collection = "LR")
public class LR {
    @Id
    private String id;
    private String documentsId;
    private String date;
    private String orsBursNo;
    private String particulars;
    private double amount;
    private String objectCode;
    private String payee;
    private String natureOfPayment;

    // Constructor
    public LR() {
    }

    public LR(String date, String orsBursNo, String particulars, double amount, String documentsId,
            String objectCode, String payee, String natureOfPayment) {
        this.documentsId = documentsId;
        this.date = date;
        this.orsBursNo = orsBursNo;
        this.particulars = particulars;
        this.amount = amount;
        this.objectCode = objectCode;
        this.payee = payee;
        this.natureOfPayment = natureOfPayment;
    }

    // Overload constructor without ObjectCode
    public LR(String date, String orsBursNo, String particulars, double amount, String documentsId, String payee,
            String natureOfPayment) {
        this.documentsId = documentsId;
        this.date = date;
        this.orsBursNo = orsBursNo;
        this.particulars = particulars;
        this.amount = amount;
        this.payee = payee;
        this.natureOfPayment = natureOfPayment;
    }

    public LR(LRRequest lr, String objectCode) {
        this.documentsId = lr.getDocumentsId();
        this.date = lr.getDate();
        this.orsBursNo = lr.getOrsBursNo();
        this.particulars = lr.getParticulars();
        this.amount = lr.getAmount();
        this.objectCode = objectCode;
        this.payee = lr.getPayee();
        this.natureOfPayment = lr.getNatureOfPayment();
    }

    // Getters and setters
    public String getPayee() {
        return payee;
    }

    public void setPayee(String payee) {
        this.payee = payee;
    }

    public String getNatureOfPayment() {
        return natureOfPayment;
    }

    public void setNatureOfPayment(String natureOfPayment) {
        this.natureOfPayment = natureOfPayment;
    }

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

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
