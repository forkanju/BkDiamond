package com.bechakeena.bkdiamond.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Unit {
    @SerializedName("unitList")
    @Expose
    private List<UnitList> unitList = null;

    public List<UnitList> getUnitList() {
        return unitList;
    }

    public void setUnitList(List<UnitList> unitList) {
        this.unitList = unitList;
    }
}
