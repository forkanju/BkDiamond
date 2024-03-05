package com.bechakeena.bkdiamond.utils;

import com.bechakeena.bkdiamond.dbhelper.CartItem;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmResults;

public class Utils {
    public static float getCartPrice(RealmResults<CartItem> cartItems) {
        float price = 0f;
        for (CartItem item : cartItems) {
            price += item.product.getSellPrice() * item.quantity;
        }
        return price;
    }

    public static String getOrderTimestamp(Long timestamp) {

        String finalTime = "";
        try {
            SimpleDateFormat inputFormatter = new SimpleDateFormat("h:mma, MM dd, yyyy");
            SimpleDateFormat outputFormatter = new SimpleDateFormat("MMM d, YYYY");
            Date date = new Date(timestamp);
            finalTime = outputFormatter.format(date);
        }catch (Exception e){
            finalTime = "N/A";
        }
        return finalTime;

    }

    public static String getDoubleFormat(Double number) {

        if (number == null || number == 0) return "";
       DecimalFormat df = new DecimalFormat("##.##");
       return df.format(number);

    }


}
