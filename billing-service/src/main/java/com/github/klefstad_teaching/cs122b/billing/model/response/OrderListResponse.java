package com.github.klefstad_teaching.cs122b.billing.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.klefstad_teaching.cs122b.billing.model.data.Sale;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderListResponse {
    private Result result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Sale> sales;

    public Result getResult() {
        return result;
    }

    public OrderListResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    public List<Sale> getSales() {
        return sales;
    }

    public OrderListResponse setSales(List<Sale> sales) {
        this.sales = sales;
        return this;
    }
}
