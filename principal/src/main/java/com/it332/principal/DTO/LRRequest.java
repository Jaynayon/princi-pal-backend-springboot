package com.it332.principal.DTO;

public class LRRequest {
    private String id;
    private String userId;
    private String documentsId;
    private String date;
    private String orsBursNo;
    private String particulars;
    private double amount;
    private String objectCode;
    private String payee;
    private String natureOfPayment;

    // Constructor
    public LRRequest() {
    }

    public LRRequest(String date, String userId, String orsBursNo, String particulars, double amount, String documentId,
            String objectCode, String payee, String natureOfPayment) {
        this.date = date;
        this.userId = userId;
        this.documentsId = documentId;
        this.orsBursNo = orsBursNo;
        this.particulars = particulars;
        this.amount = amount;
        this.objectCode = objectCode;
        this.payee = payee;
        this.natureOfPayment = natureOfPayment;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
