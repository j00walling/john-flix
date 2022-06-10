package com.github.klefstad_teaching.cs122b.billing.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import org.springframework.stereotype.Component;

@Component
public class PaymentResponse {
    private Result result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String paymentIntentId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String clientSecret;

    public Result getResult() {
        return result;
    }

    public PaymentResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public PaymentResponse setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
        return this;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public PaymentResponse setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }
}
