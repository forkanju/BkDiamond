package com.bechakeena.bkdiamond.presenters;

import com.bechakeena.bkdiamond.callbacks.ProfileView;
import com.bechakeena.bkdiamond.models.Profile;
import com.bechakeena.bkdiamond.services.APIClient;
import com.bechakeena.bkdiamond.utils.DebugLog;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class ProfilePresenter {

    private ProfileView mViewInterface;
    private APIClient mApiClient;

    public ProfilePresenter(ProfileView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }

    public void getProfileById(String token, String userId) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", token);

        mApiClient.getAPI()
                .getProfileById(map, userId)
                .enqueue(new Callback<Profile>() {
                    @Override
                    public void onResponse(Call<Profile> call, Response<Profile> response) {
                        if (response.code() == 401){
                            mViewInterface.onLogout(response.code());
                            return;
                        }

                        if (response.isSuccessful()){
                            mViewInterface.onSuccess(response.body());
                        } else mViewInterface.onError(getErrorMessage(response.errorBody()));
                    }

                    @Override
                    public void onFailure(Call<Profile> call, Throwable e) {


                        if (e instanceof HttpException) {

                            int code = ((HttpException) e).response().code();
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            mViewInterface.onError(getErrorMessage(responseBody));

                        } else if (e instanceof SocketTimeoutException) {

                            mViewInterface.onError("Server connection error");

                        } else if (e instanceof IOException) {

                            mViewInterface.onError("IOException");

                        } else {
                            mViewInterface.onError("Unknown error");
                        }
                    }
                });
    }


    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}