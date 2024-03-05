package com.bechakeena.bkdiamond.callbacks;

import com.bechakeena.bkdiamond.models.Success;

public interface PinView {

    public void onSuccess(Success success);
    public void onError(String error);
}
