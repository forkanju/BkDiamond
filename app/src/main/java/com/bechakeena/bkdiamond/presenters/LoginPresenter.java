package com.bechakeena.bkdiamond.presenters;


import com.bechakeena.bkdiamond.callbacks.LoginView;
import com.bechakeena.bkdiamond.models.Login;
import com.bechakeena.bkdiamond.services.APIClient;
import com.bechakeena.bkdiamond.utils.DebugLog;
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

public class LoginPresenter {

    private LoginView mViewInterface;
    private APIClient mApiClient;

    public LoginPresenter(LoginView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }

    public void attemptLogin(String phone, String pin,String fcmToken) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", phone);
        jsonObject.addProperty("password", pin);
        jsonObject.addProperty("fcmToken", fcmToken);

        mApiClient.getAPI()
                .getLogin(map, jsonObject)
                .enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {

                        DebugLog.e(response.code()+" CODE");
                        if (response.isSuccessful()){
                            Login login = response.body();
                            if (login != null) {
                                mViewInterface.onSuccess(login);
                            } else {
                                mViewInterface.onError("Data fetching error!");
                            }
                        } else mViewInterface.onError(getErrorMessage(response.errorBody()));
                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable e) {

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
