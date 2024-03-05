package com.bechakeena.bkdiamond.callbacks;

import android.view.View;

public interface QuantityChangeListener {
    public void onQuantityChange(String productId,String name,String photo,String unitName, int count, double price, View view);
}
