package com.bechakeena.bkdiamond.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderResponse {

    @SerializedName("data")
    @Expose
    private List<Order> orders = null;

    public List<Order> getOrders() {
        return orders;
    }
}
