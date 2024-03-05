package com.bechakeena.bkdiamond.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OrderProduct {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("productId")
    @Expose
    private Integer productId;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("unitName")
    @Expose
    private String unitName;
    @SerializedName("unitPrice")
    @Expose
    private Double unitPrice;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("subTotal")
    @Expose
    private Double subTotal;
    @SerializedName("sellingPrice")
    @Expose
    private Double sellingPrice;
    @SerializedName("status")
    @Expose
    private String status;

    public Integer getId() {
        return id;
    }

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getUnitName() {
        return unitName;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public String getStatus() {
        return status;
    }
}
