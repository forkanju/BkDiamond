package com.bechakeena.bkdiamond.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.callbacks.OrderView;
import com.bechakeena.bkdiamond.databinding.ActivityPaymentBinding;
import com.bechakeena.bkdiamond.dbhelper.AppDatabase;
import com.bechakeena.bkdiamond.dbhelper.CartItem;
import com.bechakeena.bkdiamond.dialogs.CustomAlertDialog;
import com.bechakeena.bkdiamond.models.Order;
import com.bechakeena.bkdiamond.models.Success;
import com.bechakeena.bkdiamond.presenters.OrderPresenter;
import com.bechakeena.bkdiamond.utils.DebugLog;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;
import com.bechakeena.bkdiamond.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class PaymentActivity extends AppCompatActivity implements OrderView {
    
    private ActivityPaymentBinding binding = null;

    private OrderPresenter mPresenter;
    private Realm realm;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

  

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Payment");

        init();
    }

    private void init(){
        mPresenter = new OrderPresenter(this);

        realm = Realm.getDefaultInstance();
        realm.where(CartItem.class).findAllAsync()
                .addChangeListener(cartItems -> {

                });

        if (checkConnection()){
            prepareOrder();
        }else CustomAlertDialog.showError(this, getString(R.string.err_no_internet_connection));


        binding.btnCheckOrders.setOnClickListener(v -> {
            Intent intent = new Intent(PaymentActivity.this, TransactionActivity.class);
            startActivity(intent);
            finish();
        });

    }

    private void prepareOrder() {

        showAnimation();
        RealmResults<CartItem> cartItems = realm.where(CartItem.class).findAll();
        String token = SharedDataSaveLoad.load(this,getString(R.string.preference_access_token));
        String userId = SharedDataSaveLoad.load(this,getString(R.string.preference_user_id));
        float price = Utils.getCartPrice(cartItems);

        JsonArray productArr = new JsonArray();
        for (CartItem cartItem : cartItems) {
            JsonObject productObj = new JsonObject();
            productObj.addProperty("productId", cartItem.product.getProductId());
            productObj.addProperty("quantity", cartItem.quantity);
            productObj.addProperty("subTotal", cartItem.product.getSellPrice()*cartItem.quantity);
            productObj.addProperty("unitPrice", cartItem.product.getSellPrice());
            productArr.add(productObj);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("customerId", userId);
        jsonObject.addProperty("grandTotal", price);
        jsonObject.addProperty("netTotal", price);
        jsonObject.addProperty("notes", "");
        jsonObject.add("productList", productArr);

        mPresenter.createOrder(token, jsonObject);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(Success success) {
        hideAnimation();
        AppDatabase.clearCart();
    }

    @Override
    public void onError(String error) {
        hideAnimation();
        if (error != null) CustomAlertDialog.showError(this, error+"");
    }


    @Override
    public void onLogout(int code) {

    }


    private boolean checkConnection() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public void showAnimation() {
        binding.mainLayout.setVisibility(View.GONE);
        binding.animationView.setVisibility(View.VISIBLE);
        binding.animationView.setAnimation("animation_loading.json");
        binding.animationView.playAnimation();
        binding.animationView.loop(true);
    }

    public void hideAnimation() {
        binding.mainLayout.setVisibility(View.VISIBLE);
        if (binding.animationView.isAnimating()) binding.animationView.cancelAnimation();
        binding.animationView.setVisibility(View.GONE);
    }


    public String objectToStrings(List<Order> obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (realm != null) {
            realm.removeAllChangeListeners();
            realm.close();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
