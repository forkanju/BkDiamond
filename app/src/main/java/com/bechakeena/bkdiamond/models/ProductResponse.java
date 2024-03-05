package com.bechakeena.bkdiamond.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductResponse {

    @SerializedName("data")
    @Expose
    private List<Product> products = null;

    public List<Product> getProducts() {
        return products;
    }
}
