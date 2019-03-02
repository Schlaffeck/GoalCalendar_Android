package com.slamcode.goalcalendar.backup.azure.service;

import android.util.Log;

import com.slamcode.goalcalendar.authentication.AuthenticationProvider;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationResult;
import com.slamcode.goalcalendar.backup.azure.service.retrofit.RetrofitAzureService;

import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AzureWebService implements AzureService {

    private String LOG_TAG = "GC_AzureWeb";

    private final AzureServiceConnection connection;
    private final AuthenticationProvider authenticationProvider;
    private RetrofitAzureService service;

    public AzureWebService(AzureServiceConnection connection, AuthenticationProvider authenticationProvider)
    {
        this.connection = connection;
        this.authenticationProvider = authenticationProvider;
        this.initializeApi();
    }

    @Override
    public void postBackupData(PostBackupDataRequest data) {

        try {
            final CountDownLatch latch = new CountDownLatch(1);
            this.verifyConnection();
            this.verifyData(data);

            Call<Void> call = this.service.postBackupData(
                    data.modelVersion,
                    data.userId,
                    this.connection.getFunctionKey(),
                    this.connection.getAzureAuthToken(),
                    data);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    latch.countDown();
                    Log.d(LOG_TAG, "POST response received");
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    latch.countDown();
                    Log.e(LOG_TAG, "POST response error");
                    t.printStackTrace();
                }
            });
            latch.await();
        }
        catch(InterruptedException iex)
        {
            iex.printStackTrace();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw ex;
        }
    }

    @Override
    public BackupData getBackupData(GetBackupDataRequest request) {
        final BackupData[] result = {null};
        try {
            final CountDownLatch latch = new CountDownLatch(1);
            this.verifyConnection();
            this.verifyData(request);

            Call<BackupData> call = this.service.getBackupData(
                    request.modelVersion,
                    request.userId,
                    this.connection.getFunctionKey(),
                    this.connection.getAzureAuthToken());
            call.enqueue(new Callback<BackupData>() {
                @Override
                public void onResponse(Call<BackupData> call, Response<BackupData> response) {
                    latch.countDown();
                    Log.d(LOG_TAG, "POST response received");
                    if(response.isSuccessful())
                        result[0] = response.body();
                }

                @Override
                public void onFailure(Call<BackupData> call, Throwable t) {
                    latch.countDown();
                    Log.e(LOG_TAG, "POST response error");
                    t.printStackTrace();
                }
            });

            latch.await();
            return result[0];
        }
        catch(InterruptedException iex)
        {
            iex.printStackTrace();
            return result[0];
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            throw ex;
        }
    }

    private void verifyData(PostBackupDataRequest data) {

        if(data.userId == null)
            data.userId = this.authenticationProvider.getCurrentAuthenticationData().getUserId();
    }

    private void verifyData(GetBackupDataRequest data) {

        if(data.userId == null)
            data.userId = this.authenticationProvider.getCurrentAuthenticationData().getUserId();
    }

    private void verifyConnection()
    {
        if(this.connection.getAzureAuthToken() == null)
        {
            AuthenticationResult current = this.authenticationProvider.getCurrentAuthenticationData();
            if(!current.isSignedIn() || !current.isOauthAvailable())
                throw new UnsupportedOperationException("Not authenticated");

            this.connection.setAzureAuthToken(current.getOAuthToken().getOauthToken());

//                Response<String> tokenResp = this.service.postAuthenticate(current.getAuthenticationProviderId(), current.getOAuthToken().getOauthToken()).execute();
//                if(!tokenResp.isSuccessful())
//                    throw new UnsupportedOperationException("Could not authenticate");
//                this.connection.setAzureAuthToken(tokenResp.body());
        }
    }

    private void initializeApi()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(this.connection.getConnectionUrl())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.service = retrofit.create(RetrofitAzureService.class);
    }
}