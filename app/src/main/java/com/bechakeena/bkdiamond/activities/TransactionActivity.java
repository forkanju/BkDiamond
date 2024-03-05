package com.bechakeena.bkdiamond.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.adapters.TransactionAdapter;
import com.bechakeena.bkdiamond.callbacks.TransactionView;
import com.bechakeena.bkdiamond.databinding.ActivityTransactionBinding;
import com.bechakeena.bkdiamond.dialogs.CustomAlertDialog;
import com.bechakeena.bkdiamond.models.Order;
import com.bechakeena.bkdiamond.presenters.TransactionPresenter;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;

import java.util.List;

public class TransactionActivity extends AppCompatActivity implements TransactionView, SwipeRefreshLayout.OnRefreshListener {

    private ActivityTransactionBinding binding = null;
    private TransactionPresenter mPresenter;

    private TransactionAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Transaction");

        init();
    }

    private void init(){
        mPresenter = new TransactionPresenter(this);
        binding.swipeRefresh.setOnRefreshListener(this);
        binding.swipeRefresh.setRefreshing(false);
        mPresenter = new TransactionPresenter(this);
        binding.recyclerList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        binding.recyclerList.setLayoutManager(mLayoutManager);

        getTransactionByUser();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onError(String error) {
        hideAnimation();
        showEmptyAnimation();
    }

    @Override
    public void onSuccess(List<Order> orders) {
        hideAnimation();
        if (orders != null && orders.size() > 0){
            mAdapter = new TransactionAdapter(this, orders);
            binding.recyclerList.setAdapter(mAdapter);
        }else showEmptyAnimation();
    }

    @Override
    public void onLogout(int code) {
        SharedDataSaveLoad.remove(this, getString(R.string.preference_access_token));
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRefresh() {
        getTransactionByUser();
    }

    private void getTransactionByUser(){

        if (checkConnection()){
            binding.swipeRefresh.setRefreshing(true);
            String token = SharedDataSaveLoad.load(this,getString(R.string.preference_access_token));
            String userId = SharedDataSaveLoad.load(this,getString(R.string.preference_user_id));
            mPresenter.getTransaction(token, "eq:"+userId);
        }else CustomAlertDialog.showError(this,getString(R.string.err_no_internet_connection));
    }

    private boolean checkConnection() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


    public void showAnimation() {
        binding.recyclerList.setVisibility(View.GONE);
        binding.animationView.setVisibility(View.VISIBLE);
        binding.animationView.setAnimation("animation_loading.json");
        binding.animationView.playAnimation();
        binding.animationView.loop(true);
    }

    public void showEmptyAnimation() {
        binding.recyclerList.setVisibility(View.GONE);
        binding.animationView.setVisibility(View.VISIBLE);
        binding.animationView.setAnimation("empty_box.json");
        binding.animationView.playAnimation();
        binding.animationView.loop(false);
    }

    public void hideAnimation() {
        binding.swipeRefresh.setRefreshing(false);
        binding.recyclerList.setVisibility(View.VISIBLE);
        if (binding.animationView.isAnimating()) binding.animationView.cancelAnimation();
        binding.animationView.setVisibility(View.GONE);
    }
}
