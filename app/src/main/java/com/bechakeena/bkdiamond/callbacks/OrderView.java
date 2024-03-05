package com.bechakeena.bkdiamond.callbacks;

import com.bechakeena.bkdiamond.models.Success;

public interface OrderView {
    public void onSuccess(Success success);
    public void onLogout(int code);
    public void onError(String error);
}
