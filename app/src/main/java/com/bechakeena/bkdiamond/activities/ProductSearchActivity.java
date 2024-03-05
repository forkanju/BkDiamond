package com.bechakeena.bkdiamond.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.adapters.SearchAdapter;
import com.bechakeena.bkdiamond.callbacks.ProductSearchView;
import com.bechakeena.bkdiamond.databinding.ActivityMainBinding;
import com.bechakeena.bkdiamond.databinding.ActivitySearchBinding;
import com.bechakeena.bkdiamond.dbhelper.AppDatabase;
import com.bechakeena.bkdiamond.dbhelper.CartItem;
import com.bechakeena.bkdiamond.dialogs.CustomAlertDialog;
import com.bechakeena.bkdiamond.fragments.CartBottomSheetFragment;
import com.bechakeena.bkdiamond.models.Product;
import com.bechakeena.bkdiamond.presenters.SearchPresenter;
import com.bechakeena.bkdiamond.utils.CartInfoBar;
import com.bechakeena.bkdiamond.utils.DebugLog;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;
import com.bechakeena.bkdiamond.utils.Utils;

import java.util.List;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ProductSearchActivity extends AppCompatActivity implements ProductSearchView,
        SearchAdapter.ProductsAdapterListener{
    private ActivitySearchBinding binding = null;
    private SearchView searchView;
    private ProgressDialog mProgressdialog;


    CartInfoBar cartInfoBar;

    private SearchPresenter mPresenter;
    private SearchAdapter mAdapter;
    private Realm realm;
    private RealmResults<CartItem> cartItems;
    private RealmChangeListener<RealmResults<CartItem>> cartRealmChangeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        // toolbar fancy stuff
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search products");

        init();

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

    private void getProductSearch(String query) {
        if (!query.isEmpty()) query = "like:"+query;
        if (checkConnection()){
            String token = SharedDataSaveLoad.load(this, getString(R.string.preference_access_token));
            String zoneId = SharedDataSaveLoad.load(this, getString(R.string.preference_user_zone_id));
            mProgressdialog.show();
            mPresenter.getProductSearch(token, "eq:"+zoneId,query);
        }else CustomAlertDialog.showError(this, getString(R.string.err_no_internet_connection));
    }

    private void setCartInfoBar(RealmResults<CartItem> cartItems) {
        int itemCount = 0;
        for (CartItem cartItem : cartItems) {
            itemCount += cartItem.quantity;
        }
        cartInfoBar.setData(itemCount, String.valueOf(Utils.getCartPrice(cartItems)));
    }

    private void init() {

        mProgressdialog = new ProgressDialog(this);
        mProgressdialog.setCancelable(false);
        mPresenter = new SearchPresenter(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(binding.recyclerProduct.getContext(), mLayoutManager.getOrientation());
        binding.recyclerProduct.addItemDecoration(dividerItemDecoration);
        binding.recyclerProduct.setLayoutManager(mLayoutManager);
        binding.recyclerProduct.setItemAnimator(new DefaultItemAnimator());
        cartInfoBar = new CartInfoBar(getApplicationContext());
        cartInfoBar.setListener(() -> showCart());
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
        if (mProgressdialog != null && mProgressdialog.isShowing()) mProgressdialog.dismiss();
        mAdapter = new SearchAdapter(this, products,ProductSearchActivity.this);
        binding.recyclerProduct.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLogout(int code) {
        if (mProgressdialog != null && mProgressdialog.isShowing()) mProgressdialog.dismiss();
        SharedDataSaveLoad.remove(ProductSearchActivity.this, getString(R.string.preference_access_token));
        Intent intent = new Intent(ProductSearchActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError(String error) {
        if (mProgressdialog != null && mProgressdialog.isShowing()) mProgressdialog.dismiss();
        if (error != null) CustomAlertDialog.showError(this, error);
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

    private boolean checkConnection() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                DebugLog.e("onQueryTextSubmit : "+query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                DebugLog.e("onQueryTextChange : "+query);
                getProductSearch(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
            case R.id.action_search:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    void showCart() {
        CartBottomSheetFragment fragment = new CartBottomSheetFragment();
        fragment.show(getSupportFragmentManager(), fragment.getTag());
    }

}