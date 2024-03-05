package com.bechakeena.bkdiamond.presenters;

import com.bechakeena.bkdiamond.callbacks.PinView;
import com.bechakeena.bkdiamond.models.Success;
import com.bechakeena.bkdiamond.services.APIClient;
import com.google.gson.JsonObject;

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

public class PinPresenter {

    private PinView mViewInterface;
    private APIClient mApiClient;

    public PinPresenter(PinView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }

    public void changePin(String token, String userId,String oldPassword,String newPassword) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", token);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("oldPassword", oldPassword);
        jsonObject.addProperty("newPassword", newPassword);

        mApiClient.getAPI()
                .changePin(map, jsonObject)
                .enqueue(new Callback<Success>() {
                    @Override
                    public void onResponse(Call<Success> call, Response<Success> response) {

                        if (response.isSuccessful()){
                            Success success = response.body();
                            if (success != null) {
                                mViewInterface.onSuccess(success);
                            } else {
                                mViewInterface.onError("Data fetching error!");
                            }
                        } else mViewInterface.onError(getErrorMessage(response.errorBody()));
                    }

                    @Override
                    public void onFailure(Call<Success> call, Throwable e) {

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