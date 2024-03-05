package com.bechakeena.bkdiamond.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.adapters.ChildAdapter;
import com.bechakeena.bkdiamond.adapters.ProductsAdapter;
import com.bechakeena.bkdiamond.callbacks.ProductView;
import com.bechakeena.bkdiamond.databinding.ActivityChildBinding;
import com.bechakeena.bkdiamond.dbhelper.AppDatabase;
import com.bechakeena.bkdiamond.dbhelper.CartItem;
import com.bechakeena.bkdiamond.dialogs.CustomAlertDialog;
import com.bechakeena.bkdiamond.fragments.CartBottomSheetFragment;
import com.bechakeena.bkdiamond.models.Child;
import com.bechakeena.bkdiamond.models.Parent;
import com.bechakeena.bkdiamond.models.Product;
import com.bechakeena.bkdiamond.presenters.ProductPresenter;
import com.bechakeena.bkdiamond.utils.GridSpacingItemDecoration;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;
import com.bechakeena.bkdiamond.utils.Utils;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ChildActivity extends AppCompatActivity implements ProductsAdapter.ProductsAdapterListener, ProductView,
        SwipeRefreshLayout.OnRefreshListener, ChildAdapter.CategoryItemClickListener {
    private ActivityChildBinding binding = null;
    private int parentId = 0;
    private FirebaseDatabase mDatabase;


    private ProductPresenter mPresenter;
    private ProductsAdapter mAdapter;
    private ChildAdapter mChildAdapter;
    private Realm realm;
    private RealmResults<CartItem> cartItems;
    private RealmChangeListener<RealmResults<CartItem>> cartRealmChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChildBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        init();
        getAllCategory();
        getRecentProduct();

        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItem.class).findAllAsync();

        cartRealmChangeListener = cartItems -> {

            if (cartItems != null && cartItems.size() > 0) {
                setCartInfoBar(cartItems);
                toggleCartBar(true);
            } else {
                toggleCartBar(false);
            }

            //mAdapter.setCartItems(cartItems);
        };

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


    private void setCartInfoBar(RealmResults<CartItem> cartItems) {
        int itemCount = 0;
        for (CartItem cartItem : cartItems) {
            itemCount += cartItem.quantity;
        }
        binding.cartInfoBar.setData(itemCount, String.valueOf(Utils.getCartPrice(cartItems)));
    }

    private void init() {


        mDatabase = FirebaseDatabase.getInstance();
        mPresenter = new ProductPresenter(this);

        parentId = getIntent().getIntExtra("parentId", 0);

        binding.swipeRefresh.setOnRefreshListener(this);
        binding.swipeRefresh.setRefreshing(false);


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        binding.recyclerProduct.setLayoutManager(mLayoutManager);
        binding.recyclerProduct.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        binding.recyclerProduct.setItemAnimator(new DefaultItemAnimator());
        //category init
        LinearLayoutManager mCatLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerCategory.setLayoutManager(mCatLayoutManager);
        binding.recyclerCategory.setItemAnimator(new DefaultItemAnimator());

        binding.cartInfoBar.setListener(() -> showCart());
    }


    /**
     * Rendering the products from local db
     */
    private void getRecentProduct() {
        if (checkConnection()) {
            String token = SharedDataSaveLoad.load(this, getString(R.string.preference_access_token));
            String zoneId = SharedDataSaveLoad.load(this, getString(R.string.preference_user_zone_id));

            binding.swipeRefresh.setRefreshing(true);
            mPresenter.getProducts(token, "eq:" + zoneId);
        } else CustomAlertDialog.showError(this, getString(R.string.err_no_internet_connection));
    }

    private void getAllCategory() {
        if (checkConnection()) {
            String token = SharedDataSaveLoad.load(this, getString(R.string.preference_access_token));
            mPresenter.getChildCategory(token, String.valueOf(parentId));
        } else CustomAlertDialog.showError(this, getString(R.string.err_no_internet_connection));
    }


    void showCart() {
        CartBottomSheetFragment fragment = new CartBottomSheetFragment();
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }
    }

    @Override
    public void onProductAddedCart(int index, Product product) {
        AppDatabase.addItemToCart(product);
        if (cartItems != null) {
            mAdapter.updateItem(index, cartItems);
        }
    }

    @Override
    public void onProductRemovedFromCart(int index, Product product) {
        AppDatabase.removeCartItem(product);
        if (cartItems != null) {
            mAdapter.updateItem(index, cartItems);
        }
    }

    private void toggleCartBar(boolean show) {
        if (show)
            binding.cartInfoBar.setVisibility(View.VISIBLE);
        else
            binding.cartInfoBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cartItems != null) {
            cartItems.removeChangeListener(cartRealmChangeListener);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
        }
        if (realm != null) {
            realm.close();
        }
    }


    @Override
    public void onProduct(List<Product> products) {
        binding.swipeRefresh.setRefreshing(false);
        mAdapter = new ProductsAdapter(this, products, this);
        binding.recyclerProduct.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onParent(List<Parent> parents) {

    }

    @Override
    public void onChild(List<Child> childes) {
        if (childes != null && childes.size() > 0) {
            mChildAdapter = new ChildAdapter(this, childes, this);
            binding.recyclerCategory.setAdapter(mChildAdapter);
        }
    }


    @Override
    public void onLogout(int code) {
        binding.swipeRefresh.setRefreshing(false);
        SharedDataSaveLoad.remove(ChildActivity.this, getString(R.string.preference_access_token));
        Intent intent = new Intent(ChildActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError(String error) {
        binding.swipeRefresh.setRefreshing(false);

        if (error != null) CustomAlertDialog.showError(this, error);
    }

    private boolean checkConnection() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void logout() {
        SharedDataSaveLoad.remove(this, getString(R.string.preference_access_token));
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRefresh() {
        getRecentProduct();
    }

    @Override
    public void onItemClick(int id, String name) {
        String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        DatabaseReference myRef = mDatabase.getReference("Version-1-0-10-" + timeStamp);
        Map<String, String> data = new HashMap<>();
        data.put("Category Name", name);
        myRef.setValue(data);
        Intent intent = new Intent(ChildActivity.this, ProductActivity.class);
        intent.putExtra("childId", id);
        startActivity(intent);
    }
}
