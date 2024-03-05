package com.bechakeena.bkdiamond.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.adapters.ProductsAdapter;
import com.bechakeena.bkdiamond.callbacks.ProductView;
import com.bechakeena.bkdiamond.databinding.ActivityProductBinding;
import com.bechakeena.bkdiamond.dbhelper.AppDatabase;
import com.bechakeena.bkdiamond.dbhelper.CartItem;
import com.bechakeena.bkdiamond.dialogs.CustomAlertDialog;
import com.bechakeena.bkdiamond.fragments.CartBottomSheetFragment;
import com.bechakeena.bkdiamond.models.Child;
import com.bechakeena.bkdiamond.models.Parent;
import com.bechakeena.bkdiamond.models.Product;
import com.bechakeena.bkdiamond.presenters.ProductPresenter;
import com.bechakeena.bkdiamond.utils.CartInfoBar;
import com.bechakeena.bkdiamond.utils.GridSpacingItemDecoration;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;
import com.bechakeena.bkdiamond.utils.Utils;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ProductActivity extends AppCompatActivity implements ProductView,
        ProductsAdapter.ProductsAdapterListener, SwipeRefreshLayout.OnRefreshListener{


    private ActivityProductBinding binding = null;

    CartInfoBar cartInfoBar;
    private int childId = 0;

    private ProductPresenter mPresenter;
    private ProductsAdapter mAdapter;
    private Realm realm;
    private RealmResults<CartItem> cartItems;
    private RealmChangeListener<RealmResults<CartItem>> cartRealmChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Product");

        init();
        getProductById(String.valueOf(childId));
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
        cartInfoBar.setData(itemCount, String.valueOf(Utils.getCartPrice(cartItems)));
    }

    private void init() {

       binding.swipeRefresh.setOnRefreshListener(this);
       binding.swipeRefresh.setRefreshing(false);

        childId = getIntent().getIntExtra("childId", 0);
        mPresenter = new ProductPresenter(this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        binding.recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        cartInfoBar = new CartInfoBar(getApplicationContext());
        cartInfoBar.setListener(this::showCart);
    }

    private void getProductById(String catId){
        if (checkConnection()){
            String token = SharedDataSaveLoad.load(this, getString(R.string.preference_access_token));
            String zoneId = SharedDataSaveLoad.load(this, getString(R.string.preference_user_zone_id));
            binding.swipeRefresh.setRefreshing(true);
            mPresenter.getProductByCategoryId(token, "eq:"+zoneId, "eq:"+catId);
        }else CustomAlertDialog.showError(this, getString(R.string.err_no_internet_connection));
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
            cartInfoBar.setVisibility(View.VISIBLE);
        else
            cartInfoBar.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (cartItems != null) {
            cartItems.removeChangeListener(cartRealmChangeListener);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cartItems != null) {
            cartItems.addChangeListener(cartRealmChangeListener);
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
        binding.recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onParent(List<Parent> parents) {

    }

    @Override
    public void onChild(List<Child> childes) {

    }


    @Override
    public void onLogout(int code) {
        binding.swipeRefresh.setRefreshing(false);

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

    @Override
    public void onRefresh() {
        getProductById(String.valueOf(childId));
    }
}
