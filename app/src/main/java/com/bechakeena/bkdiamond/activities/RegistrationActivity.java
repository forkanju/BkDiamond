package com.bechakeena.bkdiamond.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.adapters.ZoneListAdapterDialog;
import com.bechakeena.bkdiamond.callbacks.RegistrationView;
import com.bechakeena.bkdiamond.databinding.ActivityRegistrationBinding;
import com.bechakeena.bkdiamond.dialogs.CustomAlertDialog;
import com.bechakeena.bkdiamond.dialogs.PromptDialog;
import com.bechakeena.bkdiamond.models.Registration;
import com.bechakeena.bkdiamond.models.Zone;
import com.bechakeena.bkdiamond.presenters.RegistrationPresenter;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationActivity extends AppCompatActivity implements RegistrationView {

    private ActivityRegistrationBinding binding = null;
    private RegistrationPresenter mPresenter;
    private List<Zone> zoneList = null;
    private int zoneId = 0;
    //Bind component


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.label_registration);

        configView();

    }

    private void configView() {

//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//                            DebugLog.e("getInstanceId failed" + task.getException());
//                            return;
//                        }
//
//                        DebugLog.e(task.getResult().getToken());
//                        // Get new Instance ID token
//                        deviceToken = task.getResult().getToken();
//                    }
//
//                });


        mPresenter = new RegistrationPresenter(this);
        mPresenter.getZone();

        binding.edtZone.setOnClickListener(v -> {
            if (zoneList != null) showDialog(zoneList);
        });
        binding.btnSubmit.setOnClickListener(v -> submitRegistration());


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

    /**
     * Validating form
     */
    private void submitRegistration() {

        if (!validateShop()) {
            return;
        }

        if (!validatePhone()) {
            return;
        }

        if (!validateName()) {
            return;
        }

        if (!validateZone()) {
            return;
        }

        if (!validatePin()) {
            return;
        }

        hideKeyboard(this);
        String shop = binding.edtShop.getText().toString().trim();
        String phone = binding.edtPhone.getText().toString().trim();
        String name = binding.edtName.getText().toString().trim();
        String zip = binding.edtZip.getText().toString().trim();
        String zone = binding.edtZone.getText().toString().trim();
        String address = binding.edtAddress.getText().toString().trim();
        String pin = binding.edtPin.getText().toString().trim();

        String fcmToken = SharedDataSaveLoad.load(this, getString(R.string.preference_fcm_token));
        attemptRegistration(shop, phone, name, address, pin);


    }

    private void attemptRegistration(String shopName, String mobileNo, String fullName, String address, String pin) {
        if (checkConnection()) {
            showAnimation();
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("storeName", shopName);
            jsonObject.addProperty("mobileNo", mobileNo);
            jsonObject.addProperty("fullName", fullName);
            jsonObject.addProperty("zoneId", zoneId);
            jsonObject.addProperty("addressLine", address);
            jsonObject.addProperty("credential", pin);
            jsonObject.addProperty("status", "I");
            String data = objectToStrings(jsonObject);

            mPresenter.attemptRegistration(data);
        } else
            CustomAlertDialog.showError(RegistrationActivity.this, getString(R.string.err_no_internet_connection));
    }

    @Override
    public void onSuccess(Registration registration) {
        hideAnimation();
        showSuccess(RegistrationActivity.this, registration.getMessage());

    }

    @Override
    public void onSuccess(List<Zone> zoneList) {
        this.zoneList = zoneList;
    }


    @Override
    public void onError(String error) {
        hideAnimation();
        if (error != null) CustomAlertDialog.showError(RegistrationActivity.this, error);

    }

    private boolean validateShop() {
        String shop = binding.edtShop.getText().toString().trim();

        if (shop.isEmpty()) {
            binding.inputLayoutShop.setError(getString(R.string.err_msg_shop));
            requestFocus(binding.edtShop);
            return false;
        } else {
            binding.inputLayoutShop.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePhone() {
        String phone = binding.edtPhone.getText().toString().trim();

        if (phone.isEmpty() || !isValidPhone(phone)) {
            binding.inputLayoutPhone.setError(getString(R.string.err_msg_phone));
            requestFocus(binding.edtPhone);
            return false;
        } else {
            binding.inputLayoutPhone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateName() {
        String name = binding.edtName.getText().toString().trim();

        if (name.isEmpty()) {
            binding.inputLayoutName.setError(getString(R.string.err_msg_name));
            requestFocus(binding.inputLayoutName);
            return false;
        } else {
            binding.inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateZone() {
        String zone = binding.edtZone.getText().toString().trim();

        if (zone.isEmpty() || zone.equalsIgnoreCase("Select Zone")) {
            binding.inputLayoutZone.setError(getString(R.string.err_msg_zone));
            requestFocus(binding.inputLayoutZone);
            return false;
        } else {
            binding.inputLayoutZone.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateAddress() {
        String address = binding.edtAddress.getText().toString().trim();

        if (address.isEmpty()) {
            binding.inputLayoutAddress.setError(getString(R.string.enter_your_address));
            requestFocus(binding.edtAddress);
            return false;
        } else {
            binding.inputLayoutAddress.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePin() {
        String pin = binding.edtPin.getText().toString().trim();
        if (pin.isEmpty()) {
            binding.inputLayoutPin.setError(getString(R.string.err_msg_pin));
            requestFocus(binding.edtPin);
            return false;
        } else if (pin.length() < 4) {
            binding.inputLayoutPin.setError(getString(R.string.err_msg_pin_length));
            requestFocus(binding.edtPin);
            return false;
        } else {
            binding.inputLayoutPin.setErrorEnabled(false);
        }

        return true;
    }


    private static boolean isValidPhone(String phone) {
        Pattern mPattern = Pattern.compile("^(?:\\+?88)?01[13-9]\\d{8}$");
        Matcher matcher = mPattern.matcher(phone);
        return !TextUtils.isEmpty(phone) && matcher.find();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
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

    public void showSuccess(Context context, String msg) {
        new PromptDialog(context)
                .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                .setAnimationEnable(true)
                .setTitleText(context.getString(R.string.success))
                .setContentText(msg)
                .setPositiveListener(context.getString(R.string.ok), new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();
    }

    private void showDialog(List<Zone> zoneList) {

        final Dialog dialog = new Dialog(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_main, null);

        ListView lv = (ListView) view.findViewById(R.id.custom_list);

        // Change MyActivity.this and myListOfItems to your own values
        ZoneListAdapterDialog adapterDialog = new ZoneListAdapterDialog(RegistrationActivity.this, zoneList);

        lv.setAdapter(adapterDialog);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                zoneId = zoneList.get(position).getZoneId();
                binding.edtZone.setText(zoneList.get(position).getName());
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);

        dialog.show();

    }

    public String objectToStrings(JsonObject obj) {
        Gson gson = new Gson();
        return gson.toJson(obj);
    }

}
