package com.bechakeena.bkdiamond.callbacks;

import com.bechakeena.bkdiamond.models.Profile;
import com.bechakeena.bkdiamond.models.Registration;
import com.bechakeena.bkdiamond.models.Zone;

import java.util.List;

public interface EditProfileView {
    public void onSuccess(Profile profile);
    public void onSuccess(Registration registration);
    public void onSuccess(List<Zone> zoneList);
    public void onLogout(int code);
    public void onError(String error);
}
