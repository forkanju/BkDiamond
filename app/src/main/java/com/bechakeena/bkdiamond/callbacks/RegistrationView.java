package com.bechakeena.bkdiamond.callbacks;

import com.bechakeena.bkdiamond.models.Registration;
import com.bechakeena.bkdiamond.models.Zone;

import java.util.List;

public interface RegistrationView {

    public void onSuccess(Registration registration);
    public void onSuccess(List<Zone> zoneList);
    public void onError(String error);
}
