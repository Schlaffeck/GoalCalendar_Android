package com.slamcode.goalcalendar.backup.azure.service.retrofit;

import com.slamcode.goalcalendar.backup.azure.service.AzureService;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitAzureService {

    @POST("api/authenticate/{providerId}/{token}")
    Call<String> postAuthenticate(@Path("providerId") String providerId, @Path("token") String providerToken);

    @Headers({
            "Accept: application/json"
    })
    @POST("api/backups/{version}/{id}")
    Call<Void> postBackupData(
            @Path("version") int version,
            @Path("id") String id,
            @Query("code") String code,
            @Header("X-ZUMO-AUTH") String token,
            @Body AzureService.BackupData data);


    @Headers({
            "Accept: application/json"
    })
    @GET("api/backups/{version}/{id}")
    Call<AzureService.BackupData> getBackupData(
            @Path("version") int version,
            @Path("id") String id,
            @Query("code") String code,
            @Query("token") String token);
}
