package com.bechakeena.bkdiamond.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bechakeena.bkdiamond.R;



public class CartInfoBar extends RelativeLayout {
    private CartInfoBarListener listener;

    private TextView cartInfo;

    public CartInfoBar(Context context) {
        super(context);
        init(context, null);
    }

    public CartInfoBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init2(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_cart_info_bar, null);
        addView(view);
    }

    public void init(Context context, @Nullable AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_cart_info_bar, this, true);
        cartInfo = view.findViewById(R.id.cart_price);
        view.findViewById(R.id.container).setOnClickListener(v -> {
            if (listener != null)
                listener.onClick();
        });
    }


    public void setListener(CartInfoBarListener listener) {
        this.listener = listener;
    }

    public void setData(int itemCount, String price) {
        if (cartInfo != null)
            cartInfo.setText(getContext().getString(R.string.cart_info_bar_data, itemCount, price));
    }

    public interface CartInfoBarListener {
        void onClick();
    }
}
