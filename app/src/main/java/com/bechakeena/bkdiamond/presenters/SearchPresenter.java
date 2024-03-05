package com.bechakeena.bkdiamond.presenters;

import com.bechakeena.bkdiamond.callbacks.ProductSearchView;
import com.bechakeena.bkdiamond.callbacks.ProductView;
import com.bechakeena.bkdiamond.globals.Constants;
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

public class SearchPresenter {

    private ProductSearchView mViewInterface;
    private APIClient mApiClient;

    public SearchPresenter(ProductSearchView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }


    public void getProductSearch(String token, String zoneId, String productName) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", token);

        mApiClient.getAPI()
                .getProductSearch(map, zoneId, productName, Constants.SORT_PRODUCT_DESC)
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

                        } else mViewInterface.onError(getErrorMessage(response.errorBody()));
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable e) {

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


    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            return jsonObject.getString("message");
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
