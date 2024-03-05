package com.bechakeena.bkdiamond.services;

import com.bechakeena.bkdiamond.models.ChildResponse;
import com.bechakeena.bkdiamond.models.Common;
import com.bechakeena.bkdiamond.models.Login;
import com.bechakeena.bkdiamond.models.OrderResponse;
import com.bechakeena.bkdiamond.models.ParentResponse;
import com.bechakeena.bkdiamond.models.ProductResponse;
import com.bechakeena.bkdiamond.models.Profile;
import com.bechakeena.bkdiamond.models.Registration;
import com.bechakeena.bkdiamond.models.Success;
import com.bechakeena.bkdiamond.models.Unit;
import com.bechakeena.bkdiamond.models.ZoneList;
import com.google.gson.JsonObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIServices {

    @GET("/auth/v1/zones/")
    Call<ZoneList> getZone(@HeaderMap Map<String, String> headers);

    @POST("/auth/v1/create/customer/")
    Call<Registration> getRegistration(@HeaderMap Map<String, String> headers,
                                       @Body JsonObject jsonObject,
                                       @Query("data") String data);

    @Multipart
    @POST("/api/customer/v1/createOrUpdate/")
    Call<Registration> updateProfile(@HeaderMap Map<String, String> headers,
                                     @Part MultipartBody.Part file,
                                     @Part("data") RequestBody data);

    @POST("/auth/login/")
    Call<Login> getLogin(@HeaderMap Map<String, String> headers,
                         @Body JsonObject jsonObject);

    @POST("/api/user/v1/changePassword/")
    Call<Success> changePin(@HeaderMap Map<String, String> headers,
                                 @Body JsonObject jsonObject);

    @GET("/api/user/v1/search/")
    Call<Profile> getProfileById(@HeaderMap Map<String, String> headers,
                                 @Query("userId") String userId);


    @GET("/api/general/v1/getParentCategories/")
    Call<ParentResponse> getParentCategory(@HeaderMap Map<String, String> headers);

    @GET("/api/general/v1/getChildCategoriesByParentId/")
    Call<ChildResponse> getChildByParentIdCategory(@HeaderMap Map<String, String> headers,
                                    @Query("parentId") String parentId);


    @POST("/api/supplier/v1/supplierProduct/")
    Call<Common> createProduct(@HeaderMap Map<String, String> headers,
                               @Body JsonObject locationPost);

    @POST("/api/supplier/v1/supplierProduct/")
    Call<Common> updateProduct(@HeaderMap Map<String, String> headers,
                               @Body JsonObject locationPost);

    @GET("/api/general/v1/getUnits/")
    Call<Unit> getUnits(@HeaderMap Map<String, String> headers);


    @GET("/api/customer/v1/products/search/")
    Call<ProductResponse> getProducts(@HeaderMap Map<String, String> headers,
                                      @Query("zoneId") String zoneId,
                                      @Query("sort") String sort);
    @GET("/api/customer/v1/products/search/")
    Call<ProductResponse> getProductSearch(@HeaderMap Map<String, String> headers,
                                      @Query("zoneId") String zoneId,
                                      @Query("productName") String productName,
                                      @Query("sort") String sort);

    @GET("/api/customer/v1/products/search/")
    Call<ProductResponse> getProductsByCategoryId(@HeaderMap Map<String, String> headers,
                                      @Query("zoneId") String zoneId,
                                      @Query("categoryId") String catId,
                                      @Query("sort") String sort);

    @GET("/api/products/v1/product/search/")
    Call<ProductResponse> getProductsByProductId(@HeaderMap Map<String, String> headers,
                                                  @Query("productId") String zoneId);

    @POST("/api/customer/v1/orders")
    Call<Success> createOrder(@HeaderMap Map<String, String> headers, @Body JsonObject jsonObject);

    @GET("/api/customer/v1/getCustomerOrderList/")
    Call<OrderResponse> getOrders(@HeaderMap Map<String, String> headers,
                                  @Query("customerId") String customerId,
                                  @Query("sort") String sort);

}
