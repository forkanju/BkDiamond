package com.bechakeena.bkdiamond.callbacks;

import com.bechakeena.bkdiamond.models.Order;

import java.util.List;

public interface TransactionView {
    public void onSuccess(List<Order> orders);
    public void onLogout(int code);
    public void onError(String error);
}
