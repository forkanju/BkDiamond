package com.bechakeena.bkdiamond.callbacks;

import com.bechakeena.bkdiamond.models.Child;
import com.bechakeena.bkdiamond.models.Parent;
import com.bechakeena.bkdiamond.models.Product;

import java.util.List;

public interface ProductSearchView {
    public void onProduct(List<Product> products);
    public void onLogout(int code);
    public void onError(String error);
}
