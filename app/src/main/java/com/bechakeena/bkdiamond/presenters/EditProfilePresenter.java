package com.bechakeena.bkdiamond.presenters;

import com.bechakeena.bkdiamond.callbacks.EditProfileView;
import com.bechakeena.bkdiamond.models.Profile;
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

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;

public class EditProfilePresenter {

    private EditProfileView mViewInterface;
    private APIClient mApiClient;

    public EditProfilePresenter(EditProfileView view) {
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

                        DebugLog.e("ERROR "+e.getMessage());
                        e.getStackTrace();

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

    public void updateProfile(String token, MultipartBody.Part fileRequest, RequestBody dataRequest) {
        Map<String, String> map = new HashMap<>();
        //map.put("Content-Type", "application/json");
        map.put("Authorization", token);

//        JsonObject fileObj = new JsonObject();
//        fileObj.addProperty("file", filePath);


        mApiClient.getAPI()
                .updateProfile(map, fileRequest, dataRequest)
                .enqueue(new Callback<Registration>() {
                    @Override
                    public void onResponse(Call<Registration> call, Response<Registration> response) {

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
