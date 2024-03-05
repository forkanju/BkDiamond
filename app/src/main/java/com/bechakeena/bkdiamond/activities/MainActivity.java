package com.bechakeena.bkdiamond.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.adapters.ParentAdapter;
import com.bechakeena.bkdiamond.adapters.ProductsAdapter;
import com.bechakeena.bkdiamond.callbacks.ItemClickListener;
import com.bechakeena.bkdiamond.callbacks.ProductView;
import com.bechakeena.bkdiamond.databinding.ActivityMainBinding;
import com.bechakeena.bkdiamond.dbhelper.AppDatabase;
import com.bechakeena.bkdiamond.dbhelper.CartItem;
import com.bechakeena.bkdiamond.dialogs.CustomAlertDialog;
import com.bechakeena.bkdiamond.fragments.CartBottomSheetFragment;
import com.bechakeena.bkdiamond.models.Child;
import com.bechakeena.bkdiamond.models.Notification;
import com.bechakeena.bkdiamond.models.Parent;
import com.bechakeena.bkdiamond.models.Product;
import com.bechakeena.bkdiamond.presenters.ProductPresenter;
import com.bechakeena.bkdiamond.utils.GridSpacingItemDecoration;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;
import com.bechakeena.bkdiamond.utils.Utils;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements ProductsAdapter.ProductsAdapterListener, ProductView,
        SwipeRefreshLayout.OnRefreshListener, ItemClickListener {

    private ActivityMainBinding binding = null;
    private static final int REQ_CODE_VERSION_UPDATE = 17300;
    private AppUpdateManager mAppUpdateManager;
    public static MainActivity mainActivity = null;


    private ProductPresenter mPresenter;
    private ProductsAdapter mAdapter;
    private ParentAdapter mParentAdapter;
    private Realm realm;
    private RealmResults<CartItem> cartItems;
    private RealmChangeListener<RealmResults<CartItem>> cartRealmChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_notification);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        init();
        getAllCategory();
        getRecentProduct();

        realm = Realm.getDefaultInstance();
        cartItems = realm.where(CartItem.class).findAllAsync();

        RealmResults<Notification> notifications = AppDatabase.getNotifications();

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent inSearch = new Intent(this, ProductSearchActivity.class);
                startActivity(inSearch);
                return true;
            case R.id.action_notification:
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                Intent intSettings = new Intent(this, SettingsActivity.class);
                startActivity(intSettings);
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

        mainActivity = this;
        mPresenter = new ProductPresenter(this);

        binding.swipeRefresh.setOnRefreshListener(this);
        binding.swipeRefresh.setRefreshing(false);

        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                        appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    //  check for the type of update flow you want
                    requestUpdate(appUpdateInfo);
                }
            }
        });

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
            mPresenter.getParentCategory(token);
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
        mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                MainActivity.this,
                                REQ_CODE_VERSION_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_VERSION_UPDATE) {
            switch (requestCode) {
                case Activity.RESULT_OK:
                    break;
                case Activity.RESULT_CANCELED:
                    logout();
                    break;
                case ActivityResult.RESULT_IN_APP_UPDATE_FAILED:
                    logout();
                    break;
                default:
                    break;
            }
        }
    }

    private void requestUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            mAppUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE, //  HERE specify the type of update flow you want
                    this,   //  the instance of an activity
                    REQ_CODE_VERSION_UPDATE
            );
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
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
        if (parents != null && parents.size() > 0) {
            mParentAdapter = new ParentAdapter(this, parents, this);
            binding.recyclerCategory.setAdapter(mParentAdapter);
        }
    }

    @Override
    public void onChild(List<Child> childes) {

    }


    @Override
    public void onLogout(int code) {
        binding.swipeRefresh.setRefreshing(false);
        SharedDataSaveLoad.remove(MainActivity.this, getString(R.string.preference_access_token));
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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

    @Override
    public void onClick(int id) {
        Intent intent = new Intent(MainActivity.this, ChildActivity.class);
        intent.putExtra("parentId", id);
        startActivity(intent);
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
}