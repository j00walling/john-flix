package com.github.klefstad_teaching.cs122b.billing.util;

import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import org.springframework.stereotype.Component;

@Component
public final class Validate {

    public static void checkQuantity(Integer quantity) {
        if (quantity <= 0) {
            throw new ResultError(BillingResults.INVALID_QUANTITY);
        }
        if (quantity > 10) {
            throw new ResultError(BillingResults.MAX_QUANTITY);
        }
    }
}
