package com.bechakeena.bkdiamond.presenters;

import com.bechakeena.bkdiamond.callbacks.CommonView;
import com.bechakeena.bkdiamond.models.Common;
import com.bechakeena.bkdiamond.models.Product;
import com.bechakeena.bkdiamond.models.Unit;
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

public class UpdateProductPresenter {

    private CommonView mViewInterface;
    private APIClient mApiClient;

    public UpdateProductPresenter(CommonView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }

    public void getUnit(String token) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", token);

        mApiClient.getAPI()
                .getUnits(map)
                .enqueue(new Callback<Unit>() {
                    @Override
                    public void onResponse(Call<Unit> call, Response<Unit> response) {

                        if (response.code() == 401){
                            mViewInterface.onLogout(response.code());
                            return;
                        }
                        if (response.isSuccessful()){
                            Unit unit = response.body();
                            mViewInterface.onUnit(unit);

                        } else mViewInterface.onError(getErrorMessage(response.errorBody()));
                    }

                    @Override
                    public void onFailure(Call<Unit> call, Throwable e) {

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

    public void updateProduct(String token,String supplierId, Product product,String name, String description, String quantity, String price,int unitId) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", token);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", product.getProductId());
        jsonObject.addProperty("productName", name);
        jsonObject.addProperty("productPhoto", "");
        jsonObject.addProperty("description", description);
        jsonObject.addProperty("quantity", quantity);
        jsonObject.addProperty("sellPrice", price);
        jsonObject.addProperty("productStatus", "A");
        jsonObject.addProperty("categoryId", product.getCategoryId());
        jsonObject.addProperty("unitId", unitId);
        jsonObject.addProperty("supplierId", supplierId);


        mApiClient.getAPI()
                .updateProduct(map, jsonObject)
                .enqueue(new Callback<Common>() {
                    @Override
                    public void onResponse(Call<Common> call, Response<Common> response) {

                        DebugLog.e(response.code()+"HELLO");

                        if (response.code() == 401){
                            mViewInterface.onLogout(response.code());
                            return;
                        }
                        if (response.isSuccessful()){
                            Common common = response.body();
                            if (common != null) {
                                mViewInterface.onSuccess(common.getMessage());
                            } else {
                                mViewInterface.onError("Data fetching error!");
                            }
                        } else mViewInterface.onError(getErrorMessage(response.errorBody()));
                    }

                    @Override
                    public void onFailure(Call<Common> call, Throwable e) {

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
