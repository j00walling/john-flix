package com.github.klefstad_teaching.cs122b.billing.model.data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

public class Sale {
    private Long saleId;
    private BigDecimal total;
    private Instant orderDate;

    public Long getSaleId() {
        return saleId;
    }

    public Sale setSaleId(Long saleId) {
        this.saleId = saleId;
        return this;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public Sale setTotal(BigDecimal total) {
        this.total = total.setScale(2, RoundingMode.DOWN);
        return this;
    }

    public Instant getOrderDate() {
        return orderDate;
    }

    public Sale setOrderDate(Instant orderDate) {
        this.orderDate = orderDate;
        return this;
    }
}
