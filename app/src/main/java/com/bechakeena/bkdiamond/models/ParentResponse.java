package com.bechakeena.bkdiamond.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ParentResponse {

    @SerializedName("parentCategoryList")
    @Expose
    private List<Parent> parents = null;

    public List<Parent> getParents() {
        return parents;
    }
}
