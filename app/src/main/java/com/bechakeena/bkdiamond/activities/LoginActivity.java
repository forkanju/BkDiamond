package com.bechakeena.bkdiamond.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AppCompatActivity;
import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.callbacks.LoginView;
import com.bechakeena.bkdiamond.databinding.ActivityLoginBinding;
import com.bechakeena.bkdiamond.dialogs.CustomAlertDialog;
import com.bechakeena.bkdiamond.models.Login;
import com.bechakeena.bkdiamond.presenters.LoginPresenter;
import com.bechakeena.bkdiamond.utils.DebugLog;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;


public class LoginActivity extends AppCompatActivity implements LoginView {


    private ActivityLoginBinding binding;
    private String deviceToken = "";
    private LoginPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        getSupportActionBar().setTitle(R.string.title_login);

        viewConfig();

    }

    private void viewConfig() {

//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (!task.isSuccessful()) {
//
//                            return;
//                        }
//                        // Get new Instance ID token
//                        deviceToken = task.getResult().getToken();
//                        DebugLog.e(deviceToken);
//                        SharedDataSaveLoad.save(LoginActivity.this, getString(R.string.preference_fcm_token), deviceToken);
//                    }
//
//                });

        binding.txtVersion.setText("Version : " + getVersion());
        mPresenter = new LoginPresenter(this);
        binding.btnLogin.setOnClickListener(v -> submitLogin());
        String phone = SharedDataSaveLoad.load(this, getString(R.string.preference_user_phone));
        if (!TextUtils.isEmpty(phone)) binding.edtPhone.setText(phone);

        binding.btnRegistration.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
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

    /**
     * Validating form
     */
    private void submitLogin() {

        if (!validatePhone()) {
            return;
        }

        if (!validatePin()) {
            return;
        }

        hideKeyboard(this);

        String phone = binding.edtPhone.getText().toString().trim();
        String pin = binding.edtPin.getText().toString().trim();
        SharedDataSaveLoad.save(this, getString(R.string.preference_user_phone), phone);
        attemptLogin(phone, pin);


    }

    private void attemptLogin(String phone, String pin) {
        String fcmToken = SharedDataSaveLoad.load(this, getString(R.string.preference_fcm_token));
        if (checkConnection()) {
            showAnimation();
            mPresenter.attemptLogin(phone, pin, fcmToken);
        } else
            CustomAlertDialog.showError(LoginActivity.this, getString(R.string.err_no_internet_connection));
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
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
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

    private String getVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "0.0.0";
        }
    }

    @Override
    public void onSuccess(Login login) {
        hideAnimation();
        JWT jwt = new JWT(login.getToken());
        Claim claimUser = jwt.getClaim("userId");
        Claim claimZone = jwt.getClaim("zoneId");

        String userId = claimUser.asString();
        String zoneId = claimZone.asString();
        SharedDataSaveLoad.save(this, getString(R.string.preference_access_token), "Bearer " + login.getToken());
        SharedDataSaveLoad.save(this, getString(R.string.preference_user_id), userId);
        SharedDataSaveLoad.save(this, getString(R.string.preference_user_zone_id), zoneId);
        goDashboard();

    }

    @Override
    public void onError(String error) {
        hideAnimation();
        if (error != null) CustomAlertDialog.showError(LoginActivity.this, error);

    }

    private void goDashboard() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
