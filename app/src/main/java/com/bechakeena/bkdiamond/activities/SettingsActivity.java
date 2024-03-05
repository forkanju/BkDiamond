package com.bechakeena.bkdiamond.activities;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.callbacks.LanguageSelectListener;
import com.bechakeena.bkdiamond.databinding.ActivitySettingsBinding;
import com.bechakeena.bkdiamond.dialogs.ChooseAlertDialog;
import com.bechakeena.bkdiamond.dialogs.PromptDialog;
import com.bechakeena.bkdiamond.fragments.LanguageCustomDialog;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity implements LanguageSelectListener {

    private ActivitySettingsBinding binding = null;
    private LanguageCustomDialog mLanguageDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.title_settings);

        initConfig();

    }

    private void initConfig(){
        mLanguageDialog = new LanguageCustomDialog();
        closeOptionsMenu();

        binding.layoutProfile.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        binding.layoutOrder.setOnClickListener(v -> {
            //link to youtube video
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=8InfEdhCb7s"));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.google.android.youtube");
            startActivity(intent);
        });

        binding.layoutTransaction.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, TransactionActivity.class);
            startActivity(intent);
        });

        binding.layoutPassword.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ChangePinActivity.class);
            startActivity(intent);
        });

        binding.layoutLanguage.setOnClickListener(v -> mLanguageDialog.showDialog(SettingsActivity.this, SettingsActivity.this));

        binding.layoutSignout.setOnClickListener(v -> showLogoutDialog());

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onLanguageSelect(String lang) {
        setLanguage(lang);
    }

    private void setLanguage(String lang) {
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang.toLowerCase())); // API 17+ only.
        res.updateConfiguration(conf, dm);
        SharedDataSaveLoad.save(this, getString(R.string.preference_language_key), lang);
        restartActivity();
    }

    private void restartActivity() {
        Intent setting = getIntent();
        Intent home = MainActivity.mainActivity.getIntent();

        MainActivity.mainActivity.finish();
        SettingsActivity.this.finish();

        startActivity(home);
        startActivity(setting);
    }

    public void showLogoutDialog() {
        new ChooseAlertDialog(this)
                .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                .setAnimationEnable(true)
                .setTitleText(getString(R.string.sign_out))
                .setContentText(getString(R.string.alert_confirm_logout))
                .setNegativeListener(getString(R.string.yes), new ChooseAlertDialog.OnNegativeListener() {
                    @Override
                    public void onClick(ChooseAlertDialog dialog) {
                        dialog.dismiss();
                        logoutActivity();
                    }
                })
                .setPositiveListener(getString(R.string.no), new ChooseAlertDialog.OnPositiveListener() {
                    @Override
                    public void onClick(ChooseAlertDialog dialog) {
                        dialog.dismiss();

                    }
                }).show();
    }

    private void logoutActivity() {
        SharedDataSaveLoad.remove(SettingsActivity.this, getString(R.string.preference_access_token));
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}
