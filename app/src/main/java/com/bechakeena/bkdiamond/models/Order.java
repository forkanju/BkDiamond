package com.bechakeena.bkdiamond.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Order {

    @SerializedName("orderNo")
    @Expose
    private String orderNo;
    @SerializedName("tax")
    @Expose
    private Integer tax;
    @SerializedName("vat")
    @Expose
    private Integer vat;
    @SerializedName("discount")
    @Expose
    private Double discount;
    @SerializedName("discountType")
    @Expose
    private String discountType;
    @SerializedName("subTotal")
    @Expose
    private Double subTotal;
    @SerializedName("total")
    @Expose
    private Double total;
    @SerializedName("amountPaid")
    @Expose
    private Double amountPaid;
    @SerializedName("amountDue")
    @Expose
    private Double amountDue;
    @SerializedName("orderStatusId")
    @Expose
    private Integer orderStatusId;
    @SerializedName("orderStatusName")
    @Expose
    private String orderStatusName;
    @SerializedName("orderType")
    @Expose
    private String orderType;
    @SerializedName("notes")
    @Expose
    private String notes;
    @SerializedName("orderDate")
    @Expose
    private Long orderDate;
    @SerializedName("dueDate")
    @Expose
    private Long dueDate;
    @SerializedName("paymentStatus")
    @Expose
    private String paymentStatus;
    @SerializedName("orderedUserId")
    @Expose
    private Integer orderedUserId;
    @SerializedName("orderedStoreName")
    @Expose
    private String orderedStoreName;
    @SerializedName("orderedUserName")
    @Expose
    private String orderedUserName;
    @SerializedName("orderedUserMobileNo")
    @Expose
    private String orderedUserMobileNo;
    @SerializedName("orderLineList")
    @Expose
    private List<OrderProduct> orderProducts = null;


    public String getOrderNo() {
        return orderNo;
    }

    public Integer getTax() {
        return tax;
    }

    public Integer getVat() {
        return vat;
    }

    public Double getDiscount() {
        return discount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public Double getSubTotal() {
        return subTotal;
    }

    public Double getTotal() {
        return total;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public Double getAmountDue() {
        return amountDue;
    }

    public Integer getOrderStatusId() {
        return orderStatusId;
    }

    public String getOrderStatusName() {
        return orderStatusName;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getNotes() {
        return notes;
    }

    public Long getOrderDate() {
        return orderDate;
    }

    public Long getDueDate() {
        return dueDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public Integer getOrderedUserId() {
        return orderedUserId;
    }

    public String getOrderedStoreName() {
        return orderedStoreName;
    }

    public String getOrderedUserName() {
        return orderedUserName;
    }

    public String getOrderedUserMobileNo() {
        return orderedUserMobileNo;
    }

    public List<OrderProduct> getOrderProducts() {
        return orderProducts;
    }
}
