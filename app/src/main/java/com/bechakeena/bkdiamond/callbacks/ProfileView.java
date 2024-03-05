package com.bechakeena.bkdiamond.callbacks;

import com.bechakeena.bkdiamond.models.Profile;

public interface ProfileView {
    public void onSuccess(Profile profile);
    public void onLogout(int code);
    public void onError(String error);
}
