package com.ecoplay.android.models.network;

import com.ecoplay.android.models.model.JsonResultModel;
import com.ecoplay.android.models.model.UserModel;
import com.ecoplay.android.models.model.VentilationTimeModel;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface RetrofitRestAPI {
    @GET("/air/ventilationtime")
    Observable<JsonResultModel<VentilationTimeModel>> ventilationtime(@QueryMap Map<String, String> queryMap);

    @FormUrlEncoded
    @POST("/logins/login/")
    Observable<JsonResultModel<UserModel>> login(@FieldMap Map<String, String> data);

    // Users
    @FormUrlEncoded
    @POST("/logins/signin/")
    Observable<JsonResultModel<UserModel>> signIn(@FieldMap Map<String, String> data);

    @POST("/logins/googles/")
    Observable<JsonResultModel<UserModel>> googleLogin(@Body UserModel data);

    @POST("/logins/facebooks/")
    Observable<JsonResultModel<UserModel>> facebookLogin(@Body UserModel data);

    @POST("/logins/navers/")
    Observable<JsonResultModel<UserModel>> naverLogin(@Body UserModel data);

    @FormUrlEncoded
    @POST("/users/password/reset/")
    Observable<JsonResultModel<UserModel>> passwordReset(@FieldMap Map<String, String> data);

    @FormUrlEncoded
    @POST("/users/password/change/")
    Observable<JsonResultModel<UserModel>> passwordChange(@Header("Authorization") String authorization, @FieldMap Map<String, String> data);

    // Notifications
    @FormUrlEncoded
    @POST("/notifications/token")
    Observable<JsonResultModel> saveDeviceTokenToServer(
//            @Header("Authorization") String authorization,
            @FieldMap Map<String, String> data
    );

    @DELETE("/notifications/token/{token}")
    Observable<Response> deleteDeviceTokenOfServer(
//            @Header("Authorization") String authorization,
            @Path("token") String token
    );
}
