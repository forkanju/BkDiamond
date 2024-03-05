package com.bechakeena.bkdiamond.presenters;

import com.bechakeena.bkdiamond.callbacks.TransactionView;
import com.bechakeena.bkdiamond.globals.Constants;
import com.bechakeena.bkdiamond.models.OrderResponse;
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

public class TransactionPresenter {

    private TransactionView mViewInterface;
    private APIClient mApiClient;

    public TransactionPresenter(TransactionView view) {
        this.mViewInterface = view;

        if (this.mApiClient == null) {
            this.mApiClient = new APIClient();
        }
    }

    public void getTransaction(String token, String customerId) {
        Map<String, String> map = new HashMap<>();
        map.put("Content-Type", "application/json");
        map.put("Authorization", token);

        mApiClient.getAPI()
                .getOrders(map, customerId, Constants.SORT_ORDER_DESC)
                .enqueue(new Callback<OrderResponse>() {
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {


                        DebugLog.e(response.toString()+" || CODE");

                        if (response.isSuccessful()){
                            OrderResponse orderResponse = response.body();
                            if (orderResponse != null) {
                                mViewInterface.onSuccess(orderResponse.getOrders());
                            } else {
                                mViewInterface.onError("Data fetching error!");
                            }
                        } else mViewInterface.onError(getErrorMessage(response.errorBody()));
                    }

                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable e) {

                        DebugLog.e(e.getLocalizedMessage());

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
