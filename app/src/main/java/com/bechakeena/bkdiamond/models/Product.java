package com.bechakeena.bkdiamond.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Product extends RealmObject {

    @PrimaryKey
    @SerializedName("productId")
    @Expose
    private Integer productId;
    @SerializedName("productName")
    @Expose
    private String productName;
    @SerializedName("productPhoto")
    @Expose
    private String productPhoto;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("quantity")
    @Expose
    private Integer quantity;
    @SerializedName("sellPrice")
    @Expose
    private Double sellPrice;
    @SerializedName("discount")
    @Expose
    private Double discount;
    @SerializedName("alertQuantity")
    @Expose
    private Integer alertQuantity;
    @SerializedName("productStatus")
    @Expose
    private String productStatus;
    @SerializedName("createDate")
    @Expose
    private Long createDate;
    @SerializedName("unitId")
    @Expose
    private Integer unitId;
    @SerializedName("unitName")
    @Expose
    private String unitName;
    @SerializedName("categoryId")
    @Expose
    private Integer categoryId;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;

    public Integer getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductPhoto() {
        return productPhoto;
    }

    public String getDescription() {
        return description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public Double getDiscount() {
        return discount;
    }

    public Integer getAlertQuantity() {
        return alertQuantity;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public Integer getUnitId() {
        return unitId;
    }

    public String getUnitName() {
        return unitName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
