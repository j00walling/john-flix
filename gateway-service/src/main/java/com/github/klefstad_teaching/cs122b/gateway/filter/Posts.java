package com.github.klefstad_teaching.cs122b.gateway.filter;

public class Posts {
    private String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public Posts setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }
}
