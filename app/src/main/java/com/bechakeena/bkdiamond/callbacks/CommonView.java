package com.bechakeena.bkdiamond.callbacks;

import com.bechakeena.bkdiamond.models.Unit;

public interface CommonView {
    public void onSuccess(String msg);
    public void onUnit(Unit unit);
    public void onLogout(int code);
    public void onError(String error);
}
