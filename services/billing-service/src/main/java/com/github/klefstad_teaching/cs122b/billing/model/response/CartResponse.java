package com.github.klefstad_teaching.cs122b.billing.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.klefstad_teaching.cs122b.billing.model.data.Item;
import com.github.klefstad_teaching.cs122b.core.result.Result;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class CartResponse {
    private Result result;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal total;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Item> items;

    public Result getResult() {
        return result;
    }

    public CartResponse setResult(Result result) {
        this.result = result;
        return this;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public CartResponse setTotal(BigDecimal total) {
        this.total = total.setScale(2, RoundingMode.DOWN);
        return this;
    }

    public List<Item> getItems() {
        return items;
    }

    public CartResponse setItems(List<Item> items) {
        this.items = items;
        return this;
    }
}
