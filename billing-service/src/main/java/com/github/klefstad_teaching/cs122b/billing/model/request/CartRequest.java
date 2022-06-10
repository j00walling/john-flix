package com.github.klefstad_teaching.cs122b.billing.model.request;

public class CartRequest {
    private Long movieId;
    private Integer quantity;

    public Long getMovieId() {
        return movieId;
    }

    public CartRequest setMovieId(Long movieId) {
        this.movieId = movieId;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public CartRequest setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}
