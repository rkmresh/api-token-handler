package com.api.token.handler;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse {
    private String token;

    @JsonProperty("access_token")
    private String accessToken;

    private String apiToken;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }
}