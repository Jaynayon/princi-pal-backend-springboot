package com.it332.principal.Models;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.it332.principal.DTO.LRRequest;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Document(collection = "LR")
public class LR {
    @Id
    private String id;
    private String documentsId;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private Date date;

    private String orsBursNo;
    private String particulars;
    private double amount;
    private String objectCode;
    private String payee;
    private String natureOfPayment;
    private boolean approved;

    // Constructor
    public LR() {
    }

    public LR(Date date, String orsBursNo, String particulars, double amount, String documentsId,
            String objectCode, String payee, String natureOfPayment, boolean approved) {
        this.documentsId = documentsId;
        this.date = date; // Directly assign the date
        this.orsBursNo = orsBursNo;
        this.particulars = particulars;
        this.amount = amount;
        this.objectCode = objectCode;
        this.payee = payee;
        this.natureOfPayment = natureOfPayment;
        this.approved = approved;
    }

    // Overload constructor without ObjectCode
    public LR(Date date, String orsBursNo, String particulars, double amount, String documentsId, String payee,
            String natureOfPayment, boolean approved) {
        this.documentsId = documentsId;
        this.date = date; // Directly assign the date
        this.orsBursNo = orsBursNo;
        this.particulars = particulars;
        this.amount = amount;
        this.payee = payee;
        this.natureOfPayment = natureOfPayment;
        this.approved = approved;
    }

    public LR(LRRequest lr, String objectCode) {
        this.documentsId = lr.getDocumentsId();
        setDate(lr.getDate()); // Convert string date to Date type
        this.orsBursNo = lr.getOrsBursNo();
        this.particulars = lr.getParticulars();
        this.amount = lr.getAmount();
        this.objectCode = objectCode;
        this.payee = lr.getPayee();
        this.natureOfPayment = lr.getNatureOfPayment();
        this.approved = lr.isApproved();
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    // Getters and setters
    public String getDate() {
        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            return dateFormat.format(date); // Format Date object as "MM/dd/yyyy" string
        }
        return null; // Return null if date is null (handle this case as needed)
    }

    public void setDate(String date) { // string args to date
        try {
            // Parse input date string into a Date object
            DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date parsedDate = inputFormat.parse(date);
            this.date = parsedDate; // Set the parsed Date object
        } catch (ParseException e) {
            // Handle date parsing errors appropriately
            e.printStackTrace();
            // Optionally, set a default value or throw an exception
        }
    }

    public void setDate(Date date) {
        this.date = date;
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

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
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
