package com.slamcode.goalcalendar.authentication.dagger2;

import android.content.Context;

import com.slamcode.goalcalendar.ApplicationContext;
import com.slamcode.goalcalendar.authentication.AuthenticationProvider;
import com.slamcode.goalcalendar.authentication.clients.AuthenticationClient;
import com.slamcode.goalcalendar.authentication.clients.google.GoogleAuthenticationClient;
import com.slamcode.goalcalendar.authentication.impl.DefaultAuthenticationProvider;

import java.util.Map;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.StringKey;

@Module
public class AuthenticationDagger2Module {

    private final Context context;

    public AuthenticationDagger2Module(Context context)
    {
        this.context = context;
    }

    @Provides(type = Provides.Type.MAP)
    @StringKey(GoogleAuthenticationClient.PROVIDER_ID)
    @Singleton
    public AuthenticationClient provideGoogleAuthenticationClient(ApplicationContext applicationContext)
    {
        return new GoogleAuthenticationClient(applicationContext);
    }

    @Provides
    @Singleton
    public AuthenticationProvider provideAuthenticationProvider(Map<String, AuthenticationClient> clientMap)
    {
        return new DefaultAuthenticationProvider(clientMap);
    }
}
