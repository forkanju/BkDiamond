package com.bechakeena.bkdiamond.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.adapters.ZoneListAdapterDialog;
import com.bechakeena.bkdiamond.callbacks.EditProfileView;
import com.bechakeena.bkdiamond.databinding.ActivityEditProfileBinding;
import com.bechakeena.bkdiamond.dialogs.CustomAlertDialog;
import com.bechakeena.bkdiamond.dialogs.PromptDialog;
import com.bechakeena.bkdiamond.globals.Constants;
import com.bechakeena.bkdiamond.models.Profile;
import com.bechakeena.bkdiamond.models.Registration;
import com.bechakeena.bkdiamond.models.UserDetails;
import com.bechakeena.bkdiamond.models.Zone;
import com.bechakeena.bkdiamond.presenters.EditProfilePresenter;
import com.bechakeena.bkdiamond.utils.GlideApp;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class EditProfileActivity extends AppCompatActivity implements EditProfileView {

    private ActivityEditProfileBinding binding = null;
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;

    private EditProfilePresenter mPresenter;
    private List<Zone> zoneList = null;
    private int zoneId = 0;
    private String filePath = null;
    private Uri fileUri = null;
    //Bind component


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Profile");

        configView();
    }


    private void configView() {

        loadProfileDefault();

        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over
        ImagePickerActivity.clearCache(this);

        //init presenter
        mPresenter = new EditProfilePresenter(this);
        getZone();
        //Get User data
        getProfile();


        binding.imgProfile.setOnClickListener(view -> onProfileImageClick());
        binding.imgPlus.setOnClickListener(view -> onProfileImageClick());


        binding.edtZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (zoneList != null) showDialog(zoneList);
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitProfile();
            }
        });
    }

    private void loadProfile(String url) {
        Glide.with(this).load(url).into(binding.imgProfile);
        binding.imgProfile.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }

    private void loadProfileDefault() {
        Glide.with(this).load(R.drawable.baseline_account_circle_black_48)
                .into(binding.imgProfile);
        binding.imgProfile.setColorFilter(ContextCompat.getColor(this, R.color.profile_default_tint));
    }


    private void getProfile() {
        if (checkConnection()) {
            showAnimation();
            String token = SharedDataSaveLoad.load(this, getString(R.string.preference_access_token));
            String userId = SharedDataSaveLoad.load(this, getString(R.string.preference_user_id));
            mPresenter.getProfileById(token, "eq:" + userId);
        } else
            CustomAlertDialog.showError(EditProfileActivity.this, getString(R.string.err_no_internet_connection));
    }


    /**
     * Validating form
     */
    private void submitProfile() {

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


        hideKeyboard(this);
        String shop = binding.edtShop.getText().toString().trim();
        String phone = binding.edtPhone.getText().toString().trim();
        String name = binding.edtName.getText().toString().trim();
        String zip = binding.edtZip.getText().toString().trim();
        String zone = binding.edtZone.getText().toString().trim();
        String address = binding.edtAddress.getText().toString().trim();

        String fcmToken = SharedDataSaveLoad.load(this, getString(R.string.preference_fcm_token));
        updateProfile(shop, phone, name, address);


    }

    private void updateProfile(String shopName, String mobile, String fullName, String address) {
        if (checkConnection()) {
            showAnimation();
            String token = SharedDataSaveLoad.load(this, getString(R.string.preference_access_token));
            String userId = SharedDataSaveLoad.load(this, getString(R.string.preference_user_id));
            String zoneId = SharedDataSaveLoad.load(this, getString(R.string.preference_user_zone_id));
            String accountNo = SharedDataSaveLoad.load(this, getString(R.string.preference_user_account));
            int contactId = SharedDataSaveLoad.loadInt(this, getString(R.string.preference_user_contact_id));
            int addressId = SharedDataSaveLoad.loadInt(this, getString(R.string.preference_user_address_id));

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("userId", userId);
            jsonObject.addProperty("storeName", shopName);
            jsonObject.addProperty("mobileNo", mobile);
            jsonObject.addProperty("fullName", fullName);
            jsonObject.addProperty("zoneId", zoneId);
            jsonObject.addProperty("accountNo", accountNo);
            jsonObject.addProperty("contactId", contactId);
            jsonObject.addProperty("addressId", addressId);
            jsonObject.addProperty("addressLine", address);
            jsonObject.addProperty("status", "A");

            String data = objectToStrings(jsonObject);

            if (fileUri != null) {
                File file = new File(fileUri.getPath());
                RequestBody requestFile = RequestBody.create(okhttp3.MultipartBody.FORM, file);
                // MultipartBody.Part is used to send also the actual file name
                MultipartBody.Part fileRequest = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

                // add another part within the multipart request
                RequestBody dataRequest = RequestBody.create(okhttp3.MultipartBody.FORM, data);

                mPresenter.updateProfile(token, fileRequest, dataRequest);
            } else {
                // add another part within the multipart request
                RequestBody dataRequest = RequestBody.create(okhttp3.MultipartBody.FORM, data);

                mPresenter.updateProfile(token, null, dataRequest);
            }


        } else
            CustomAlertDialog.showError(EditProfileActivity.this, getString(R.string.err_no_internet_connection));
    }

    private void onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }


    private void getZone() {
        if (checkConnection()) {
            String token = SharedDataSaveLoad.load(this, getString(R.string.preference_access_token));
            mPresenter.getZone();
        } else
            CustomAlertDialog.showError(EditProfileActivity.this, getString(R.string.err_no_internet_connection));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

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


    private static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
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

    @Override
    public void onSuccess(Registration registration) {
        hideAnimation();
        showSuccess(EditProfileActivity.this, registration.getMessage());
    }

    @Override
    public void onSuccess(List<Zone> zoneList) {
        this.zoneList = zoneList;
    }

    @Override
    public void onSuccess(Profile profile) {
        hideAnimation();
        if (profile == null) return;
        UserDetails userDetails = profile.getUserDetails().get(0);
        binding.edtShop.setText(userDetails.getStoreName());
        binding.edtName.setText(userDetails.getFullName());
        binding.edtPhone.setText(userDetails.getMobileNo());
        binding.edtZone.setText(userDetails.getZone());
        binding.edtZip.setText(userDetails.getZipCode());
        binding.edtAddress.setText(userDetails.getAddressLine());
        zoneId = userDetails.getZoneId();

        GlideApp.with(this)
                .load(Constants.CUSTOMER_PROFILE_BASE_URL + userDetails.getStorePhoto())
                .placeholder(R.drawable.baseline_account_circle_black_48)
                .error(R.drawable.baseline_account_circle_black_48)
                .into(binding.imgProfile);
    }

    @Override
    public void onLogout(int code) {
        SharedDataSaveLoad.remove(EditProfileActivity.this, getString(R.string.preference_access_token));
        Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onError(String error) {
        hideAnimation();
        if (error != null) CustomAlertDialog.showError(EditProfileActivity.this, error);

    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(EditProfileActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(EditProfileActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    // loading profile image from local cache
                    fileUri = uri;
                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
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
        ZoneListAdapterDialog adapterDialog = new ZoneListAdapterDialog(EditProfileActivity.this, zoneList);

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
