package com.slamcode.goalcalendar.authentication.clients;

public class AuthenticationToken {

    private String token;

    public AuthenticationToken(String token)
    {
        this.token = token;
    }

    public String getOauthToken() {
        return this.token;
    }
}
