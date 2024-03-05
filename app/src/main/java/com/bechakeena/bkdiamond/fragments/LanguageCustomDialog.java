package com.bechakeena.bkdiamond.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bechakeena.bkdiamond.R;
import com.bechakeena.bkdiamond.callbacks.LanguageSelectListener;
import com.bechakeena.bkdiamond.utils.SharedDataSaveLoad;


public class LanguageCustomDialog {

    private LanguageSelectListener listener;
    public void showDialog(Activity activity, final LanguageSelectListener listener){
        this.listener = listener;
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_language_choose);
        //component initialization
        ImageView img_close = (ImageView) dialog.findViewById(R.id.img_close);
        ImageView img_english = (ImageView) dialog.findViewById(R.id.img_english);
        ImageView img_bangla = (ImageView) dialog.findViewById(R.id.img_bangla);

        RelativeLayout layout_english = (RelativeLayout) dialog.findViewById(R.id.layout_english);
        RelativeLayout layout_bangla = (RelativeLayout) dialog.findViewById(R.id.layout_bangla);

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        layout_english.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLanguageSelect("en");
                dialog.dismiss();
            }
        });

        layout_bangla.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLanguageSelect("bn");
                dialog.dismiss();
            }
        });


        String lang = SharedDataSaveLoad.load(activity,activity.getString(R.string.preference_language_key));
        switch (lang){
            case "en":
                img_english.setVisibility(View.VISIBLE);
                img_bangla.setVisibility(View.GONE);
                break;
            case "bn":
                img_english.setVisibility(View.GONE);
                img_bangla.setVisibility(View.VISIBLE);
                break;
            default:
                img_english.setVisibility(View.VISIBLE);
                img_bangla.setVisibility(View.GONE);
                break;
        }

        dialog.show();

    }


}
