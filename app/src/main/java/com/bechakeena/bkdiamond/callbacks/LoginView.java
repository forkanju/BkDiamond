package com.bechakeena.bkdiamond.callbacks;

import com.bechakeena.bkdiamond.models.Login;

public interface LoginView {

    public void onSuccess(Login login);
    public void onError(String error);
}
