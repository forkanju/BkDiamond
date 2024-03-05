package com.bechakeena.bkdiamond.activities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.callbacks.PinView;
import com.bechakeena.bkdiamond.databinding.ActivityChangePinBinding;
import com.bechakeena.bkdiamond.dialogs.CustomAlertDialog;
import com.bechakeena.bkdiamond.dialogs.PromptDialog;
import com.bechakeena.bkdiamond.models.Success;
import com.bechakeena.bkdiamond.presenters.PinPresenter;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;

public class ChangePinActivity extends AppCompatActivity implements PinView {

    private ActivityChangePinBinding binding = null;
    private PinPresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.label_change_pin);

        configView();
    }

    private void configView() {

        mPresenter = new PinPresenter(this);
        binding.btnSubmit.setOnClickListener(v -> submitPin());
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
    private void submitPin() {

        if (!validatePhone()) {
            return;
        }

        if (!validatePin()) {
            return;
        }

        if (!validateNewPin()) {
            return;
        }

        hideKeyboard(this);

        String phone = binding.edtPhone.getText().toString().trim();
        String pin = binding.edtPin.getText().toString().trim();
        String newPin = binding.edtNewPin.getText().toString().trim();
        changePin(phone, pin, newPin);


    }

    private void changePin(String phone, String oldPass, String newPass) {
        String token = SharedDataSaveLoad.load(ChangePinActivity.this, getString(R.string.preference_access_token));
        String userId = SharedDataSaveLoad.load(ChangePinActivity.this, getString(R.string.preference_user_id));

        if (checkConnection()) {
            showAnimation();
            mPresenter.changePin(token, userId, oldPass, newPass);
        } else
            CustomAlertDialog.showError(ChangePinActivity.this, getString(R.string.err_no_internet_connection));
    }

    @Override
    public void onSuccess(Success success) {
        hideAnimation();
        showSuccess(ChangePinActivity.this, success.getMessage());
    }

    @Override
    public void onError(String error) {
        hideAnimation();
        if (error != null) CustomAlertDialog.showError(ChangePinActivity.this, error);

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

    private boolean validateNewPin() {
        String pin = binding.edtNewPin.getText().toString().trim();
        if (pin.isEmpty()) {
            binding.inputLayoutNewPin.setError(getString(R.string.err_msg_pin));
            requestFocus(binding.edtNewPin);
            return false;
        } else if (pin.length() < 4) {
            binding.inputLayoutNewPin.setError(getString(R.string.err_msg_pin_length));
            requestFocus(binding.edtNewPin);
            return false;
        } else {
            binding.inputLayoutNewPin.setErrorEnabled(false);
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
}
