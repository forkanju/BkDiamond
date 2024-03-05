package com.bechakeena.bkdiamond.presenters;

import android.util.Log;

import com.bechakeena.bkdiamond.callbacks.ProductView;
import com.bechakeena.bkdiamond.globals.Constants;
import com.bechakeena.bkdiamond.models.ChildResponse;
import com.bechakeena.bkdiamond.models.Parent;
import com.bechakeena.bkdiamond.models.ParentResponse;
import com.bechakeena.bkdiamond.models.ProductResponse;
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

public class ProductPresenter {

    private ProductView mViewInterface;
    private APIClient mApiClient;

    public ProductPresenter(ProductView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }

    public void getParentCategory(String token) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", token);


        mApiClient.getAPI()
                .getParentCategory(map)
                .enqueue(new Callback<ParentResponse>() {
                    @Override
                    public void onResponse(Call<ParentResponse> call, Response<ParentResponse> response) {

                        DebugLog.e(response.toString());
                        if (response.code() == 401){
                            mViewInterface.onLogout(response.code());
                            return;
                        }

                        if (response.isSuccessful()){
                            ParentResponse parentResponse = response.body();
                            if (parentResponse != null) {
                                mViewInterface.onParent(parentResponse.getParents());
                            } else {
                                mViewInterface.onError("Data fetching error!");
                            }
                        } else {
                            mViewInterface.onError(getErrorMessage(response.errorBody()));
                            Log.d("ProductPresenter", "onResponse: else1: "+response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ParentResponse> call, Throwable e) {

                        e.printStackTrace();

                        if (e instanceof HttpException) {

                            int code = ((HttpException) e).response().code();
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            mViewInterface.onError(getErrorMessage(responseBody));
                            Log.d("ProductPresenter", "onFailure:  "+responseBody);

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

    public void getChildCategory(String token, String parentId) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", token);


        mApiClient.getAPI()
                .getChildByParentIdCategory(map, parentId)
                .enqueue(new Callback<ChildResponse>() {
                    @Override
                    public void onResponse(Call<ChildResponse> call, Response<ChildResponse> response) {

                        DebugLog.e(response.toString());
                        if (response.code() == 401){
                            mViewInterface.onLogout(response.code());
                            return;
                        }

                        if (response.isSuccessful()){
                            ChildResponse childResponse = response.body();
                            if (childResponse != null) {
                                mViewInterface.onChild(childResponse.getChildes());
                            } else {
                                mViewInterface.onError("Data fetching error!");
                            }
                        } else {
                            mViewInterface.onError(getErrorMessage(response.errorBody()));
                            Log.d("ProductPresenter", "onResponse: else2: "+response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<ChildResponse> call, Throwable e) {

                        e.printStackTrace();

                        if (e instanceof HttpException) {

                            int code = ((HttpException) e).response().code();
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            mViewInterface.onError(getErrorMessage(responseBody));
                            Log.d("ProductPresenter", "onFailure:  "+responseBody);

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

    public void getProductByCategoryId(String token, String zoneId, String catId) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", token);

        mApiClient.getAPI()
                .getProductsByCategoryId(map, zoneId, catId, Constants.SORT_PRODUCT_DESC)
                .enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {


                        if (response.code() == 401){
                            mViewInterface.onLogout(response.code());
                            return;
                        }

                        if (response.isSuccessful()){
                            ProductResponse proResponse = response.body();
                            if (proResponse != null){
                                mViewInterface.onProduct(proResponse.getProducts());
                            }else {
                                mViewInterface.onError("No Data Found!");
                            }

                        } else {
                            mViewInterface.onError(getErrorMessage(response.errorBody()));
                            Log.d("ProductPresenter", "onResponse: else3: "+getErrorMessage(response.errorBody()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable e) {

                        if (e instanceof HttpException) {

                            int code = ((HttpException) e).response().code();
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            mViewInterface.onError(getErrorMessage(responseBody));
                            Log.d("ProductPresenter", "onFailure:  "+responseBody);

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


    public void getProducts(String token, String zoneId) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", token);

        Log.d("ProductPresenter", "getProducts: zoneId: "+zoneId);

        mApiClient.getAPI()
                .getProducts(map, zoneId, Constants.SORT_PRODUCT_DESC)
                .enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {

                        if (response.code() == 401){
                            mViewInterface.onLogout(response.code());
                            return;
                        }

                        if (response.isSuccessful()){
                            ProductResponse productResponse = response.body();
                            if (productResponse != null){

                                mViewInterface.onProduct(productResponse.getProducts());
                            }else {
                                mViewInterface.onError("No Data Found!");
                            }

                        } else {
                            mViewInterface.onError(getErrorMessage(response.errorBody()));
                            Log.d("ProductPresenter", "onResponse: else4: "+getErrorMessage(response.errorBody()));
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable e) {

                        DebugLog.e("ERROR "+e.getMessage());
                        e.getStackTrace();


                        if (e instanceof HttpException) {

                            int code = ((HttpException) e).response().code();
                            ResponseBody responseBody = ((HttpException) e).response().errorBody();
                            mViewInterface.onError(getErrorMessage(responseBody));
                            Log.d("ProductPresenter", "onFailure:  "+responseBody);

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
