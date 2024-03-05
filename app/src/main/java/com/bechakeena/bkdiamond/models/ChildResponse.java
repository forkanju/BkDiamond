package com.bechakeena.bkdiamond.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ChildResponse {

    @SerializedName("subCategoryList")
    @Expose
    private List<Child> childes = null;

    public List<Child> getChildes() {
        return childes;
    }
}
