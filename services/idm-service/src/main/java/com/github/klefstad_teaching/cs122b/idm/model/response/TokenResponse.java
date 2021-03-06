package com.github.klefstad_teaching.cs122b.idm.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import org.springframework.stereotype.Component;

@Component
public class TokenResponse {
    private Result result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accessToken;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String refreshToken;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
