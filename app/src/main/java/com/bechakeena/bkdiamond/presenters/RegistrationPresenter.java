package com.bechakeena.bkdiamond.presenters;

import com.bechakeena.bkdiamond.callbacks.RegistrationView;
import com.bechakeena.bkdiamond.models.Registration;
import com.bechakeena.bkdiamond.models.ZoneList;
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

public class RegistrationPresenter {

    private RegistrationView mViewInterface;
    private APIClient mApiClient;

    public RegistrationPresenter(RegistrationView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }

    public void getZone() {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");

        mApiClient.getAPI()
                .getZone(map)
                .enqueue(new Callback<ZoneList>() {
                    @Override
                    public void onResponse(Call<ZoneList> call, Response<ZoneList> response) {

                        if (response.isSuccessful()){
                            ZoneList zoneList = response.body();
                            if (zoneList != null) {
                                mViewInterface.onSuccess(zoneList.getZoneList());
                            } else {
                                mViewInterface.onError("Data fetching error!");
                            }
                        } else mViewInterface.onError(getErrorMessage(response.errorBody()));
                    }

                    @Override
                    public void onFailure(Call<ZoneList> call, Throwable e) {

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

    public void attemptRegistration(String data) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("file", "");

        mApiClient.getAPI()
                .getRegistration(map, jsonObject, data)
                .enqueue(new Callback<Registration>() {
                    @Override
                    public void onResponse(Call<Registration> call, Response<Registration> response) {

                        DebugLog.e(response.code()+" CODE ");
                        DebugLog.e(response.toString());

                        if (response.isSuccessful()){
                            Registration registration = response.body();
                            if (registration != null) {
                                mViewInterface.onSuccess(registration);
                            } else {
                                mViewInterface.onError("Data fetching error!");
                            }
                        } else mViewInterface.onError(getErrorMessage(response.errorBody()));
                    }

                    @Override
                    public void onFailure(Call<Registration> call, Throwable e) {

                        e.printStackTrace();

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
