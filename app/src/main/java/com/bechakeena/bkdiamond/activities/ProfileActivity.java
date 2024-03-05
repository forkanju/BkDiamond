package com.bechakeena.bkdiamond.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.callbacks.ProfileView;
import com.bechakeena.bkdiamond.databinding.ActivityProfileBinding;
import com.bechakeena.bkdiamond.dialogs.CustomAlertDialog;
import com.bechakeena.bkdiamond.dialogs.PromptDialog;
import com.bechakeena.bkdiamond.globals.Constants;
import com.bechakeena.bkdiamond.models.Profile;
import com.bechakeena.bkdiamond.models.UserDetails;
import com.bechakeena.bkdiamond.presenters.ProfilePresenter;
import com.bechakeena.bkdiamond.utils.GlideApp;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;
import com.bumptech.glide.request.RequestOptions;


public class ProfileActivity extends AppCompatActivity implements ProfileView {

    private ActivityProfileBinding binding = null;

    private ProfilePresenter mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        viewConfig();


    }

    private void viewConfig() {

        mPresenter = new ProfilePresenter(this);

        getProfile();
        binding.imgBack.setOnClickListener(v -> finish());

        binding.imgEdit.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });
    }


    private void getProfile() {
        if (checkConnection()) {
            binding.progressBar.setVisibility(View.VISIBLE);
            String token = SharedDataSaveLoad.load(this, getString(R.string.preference_access_token));
            String userId = SharedDataSaveLoad.load(this, getString(R.string.preference_user_id));
            mPresenter.getProfileById(token, "eq:" + userId);
        } else
            CustomAlertDialog.showError(ProfileActivity.this, getString(R.string.err_no_internet_connection));
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

    @Override
    public void onSuccess(Profile profile) {

        binding.progressBar.setVisibility(View.GONE);
        if (profile != null) {
            UserDetails userDetails = profile.getUserDetails().get(0);
            //Save Data Shared Preference
            save(userDetails);
            binding.txtName.setText(userDetails.getFullName() != null ? userDetails.getFullName() : "N/A");
            binding.txtAddress.setText(userDetails.getAddressLine() != null ? userDetails.getAddressLine() : "N/A");
            binding.txtShopname.setText(userDetails.getStoreName() != null ? userDetails.getStoreName() : "N/A");
            binding.txtPhone.setText(userDetails.getMobileNo() != null ? userDetails.getMobileNo() : "N/A");
            binding.txtZone.setText(userDetails.getZone() != null ? userDetails.getZone() : "N/A");
            GlideApp.with(this)
                    .load(Constants.CUSTOMER_PROFILE_BASE_URL + userDetails.getStorePhoto())
                    .placeholder(R.drawable.ic_user)
                    .error(R.drawable.ic_user)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.imgProfile);

        }


    }

    @Override
    public void onLogout(int code) {
        binding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onError(String error) {
        binding.progressBar.setVisibility(View.GONE);
    }

    private boolean checkConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void save(UserDetails userDetails) {
        SharedDataSaveLoad.save(this, getString(R.string.preference_user_type_id), userDetails.getUserTypeId());
        SharedDataSaveLoad.save(this, getString(R.string.preference_user_account), userDetails.getAccountNo());
        SharedDataSaveLoad.save(this, getString(R.string.preference_user_account), userDetails.getAccountNo());
        SharedDataSaveLoad.save(this, getString(R.string.preference_user_contact_id), userDetails.getContactId());
        SharedDataSaveLoad.save(this, getString(R.string.preference_user_address_id), userDetails.getAddressId());

    }

}
