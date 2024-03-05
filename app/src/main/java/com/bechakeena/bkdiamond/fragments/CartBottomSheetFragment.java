package com.bechakeena.bkdiamond.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.activities.PaymentActivity;
import com.bechakeena.bkdiamond.adapters.CartProductsAdapter;
import com.bechakeena.bkdiamond.adapters.ProductsAdapter;
import com.bechakeena.bkdiamond.databinding.ActivitySplashBinding;
import com.bechakeena.bkdiamond.databinding.FragmentCartBottomSheetBinding;
import com.bechakeena.bkdiamond.dbhelper.AppDatabase;
import com.bechakeena.bkdiamond.dbhelper.CartItem;
import com.bechakeena.bkdiamond.utils.Utils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;


import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CartBottomSheetFragment extends BottomSheetDialogFragment implements CartProductsAdapter.CartProductsAdapterListener {


    private FragmentCartBottomSheetBinding binding = null;

    private Realm realm;
    private CartProductsAdapter mAdapter;
    private RealmResults<CartItem> cartItems;
    private RealmChangeListener<RealmResults<CartItem>> cartItemRealmChangeListener;

    public CartBottomSheetFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Making bottom sheet expanding to full height by default
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_cart_bottom_sheet, container, false);
        binding = FragmentCartBottomSheetBinding.inflate(getLayoutInflater());


        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItem.class).findAllAsync();

        cartItemRealmChangeListener = cartItems -> {
            mAdapter.setData(cartItems);
            setTotalPrice();
        };

        cartItems.addChangeListener(cartItemRealmChangeListener);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mAdapter = new CartProductsAdapter(getActivity(), this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        binding.btnCheckout.setOnClickListener(view -> onCheckoutClick());
        binding.icClose.setOnClickListener(view -> onCloseClick());

        setTotalPrice();
    }

    private void setTotalPrice() {
        if (cartItems != null) {
            float price = Utils.getCartPrice(cartItems);
            if (price > 0) {
                binding.btnCheckout.setText(getString(R.string.btn_checkout, getString(R.string.price_with_currency, price)));
            } else {
                // if the price is zero, dismiss the dialog
                dismiss();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cartItems != null) {
            cartItems.removeChangeListener(cartItemRealmChangeListener);
        }

        if (realm != null) {
            realm.close();
        }
    }


    void onCloseClick() {
        dismiss();
    }


    void onCheckoutClick() {
        startActivity(new Intent(getActivity(), PaymentActivity.class));
        dismiss();
    }

    @Override
    public void onCartItemRemoved(int index, CartItem cartItem) {
        AppDatabase.removeCartItem(cartItem);
    }
}
