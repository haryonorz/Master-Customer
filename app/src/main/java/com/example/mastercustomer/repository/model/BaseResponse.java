package com.example.mastercustomer.repository.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BaseResponse {

    @Expose
    @SerializedName("message") private String message;
    @Expose
    @SerializedName("customers") private List<Customers> customersList;
    @Expose
    @SerializedName("customer") private Customers customer;
    @Expose
    @SerializedName("owner") private Customers owner;
    @Expose
    @SerializedName("total_page") private int totalPage;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Customers> getCustomersList() {
        return customersList;
    }

    public void setCustomersList(List<Customers> customersList) {
        this.customersList = customersList;
    }

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

    public Customers getOwner() {
        return owner;
    }

    public void setOwner(Customers owner) {
        this.owner = owner;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }
}
