package com.github.klefstad_teaching.cs122b.billing.repo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.klefstad_teaching.cs122b.billing.model.data.Item;
import com.github.klefstad_teaching.cs122b.billing.model.data.Sale;
import com.github.klefstad_teaching.cs122b.billing.model.response.CartResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.OrderListResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.PaymentResponse;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BillingRepo
{
    private final NamedParameterJdbcTemplate template;
    private final ObjectMapper objectMapper;

    @Autowired
    public BillingRepo(NamedParameterJdbcTemplate template, ObjectMapper objectMapper) {
        this.template = template;
        this.objectMapper = objectMapper;
    }

    public boolean itemInCart(Long userId, Long movieId) {
        final String sql =
                "SELECT c.movie_id " +
                "FROM billing.cart c " +
                "WHERE c.movie_id = :movieId AND c.user_id = :userId;";

        List<Integer> movies = this.template.query(
                sql,
                new MapSqlParameterSource()
                        .addValue("movieId", movieId.intValue(), Types.INTEGER)
                        .addValue("userId", userId.intValue(), Types.INTEGER),
                (rs, rowNum) -> rs.getInt("movie_id")
        );

        return !movies.isEmpty();
    }

    public ResponseEntity<CartResponse> insertItem(Long userId, Long movieId, Integer quantity) {
        final String sql =
                "INSERT INTO billing.cart (user_id, movie_id, quantity) " +
                "VALUES (:userId, :movieId, :quantity);";

        this.template.update(
                sql,
                new MapSqlParameterSource()
                        .addValue("userId", userId.intValue(), Types.INTEGER)
                        .addValue("movieId", movieId.intValue(), Types.INTEGER)
                        .addValue("quantity", quantity, Types.INTEGER)
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CartResponse().setResult(BillingResults.CART_ITEM_INSERTED));
    }

    public ResponseEntity<CartResponse> updateCart(Long userId, Long movieId, Integer quantity) {
        final String sql =
                "UPDATE billing.cart " +
                "SET quantity = :quantity " +
                "WHERE user_id = :userId AND movie_id = :movieId;";

        this.template.update(
                sql,
                new MapSqlParameterSource()
                        .addValue("quantity", quantity, Types.INTEGER)
                        .addValue("userId", userId.intValue(), Types.INTEGER)
                        .addValue("movieId", movieId.intValue(), Types.INTEGER)
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CartResponse().setResult(BillingResults.CART_ITEM_UPDATED));
    }

    public ResponseEntity<CartResponse> deleteItem(Long userId, Long movieId) {
        final String sql =
                "DELETE FROM billing.cart " +
                "WHERE user_id = :userId AND movie_id = :movieId";

        this.template.update(
                sql,
                new MapSqlParameterSource()
                        .addValue("userId", userId.intValue(), Types.INTEGER)
                        .addValue("movieId", movieId.intValue(), Types.INTEGER)
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CartResponse().setResult(BillingResults.CART_ITEM_DELETED));
    }

    public ResponseEntity<CartResponse> retrieveCart(Long userId, int premium) {
        final String sql =
               "SELECT " +
                   "JSON_ARRAYAGG(JSON_OBJECT( 'unitPrice', i.unit_price, 'quantity', i.quantity, 'movieId', i.movie_id, 'movieTitle', i.title, 'backdropPath', i.backdrop_path, 'posterPath', i.poster_path )) AS items " +
                   "FROM " +
                   "  ( " +
                   "    SELECT " +
                   "(IF(:premium = 0, bmp.unit_price,  " +
                   "      ( " +
                   "        bmp.unit_price * (1 - (bmp.premium_discount / 100.0)) " +
                   "      ) " +
                   ")) AS unit_price, " +
                   "      bc.quantity, " +
                   "      bc.movie_id, " +
                   "      mm.title, " +
                   "      mm.backdrop_path, " +
                   "      mm.poster_path  " +
                   "    FROM " +
                   "      billing.cart bc  " +
                   "      JOIN " +
                   "        billing.movie_price bmp  " +
                   "        ON bmp.movie_id = bc.movie_id  " +
                   "      JOIN " +
                   "        movies.movie mm  " +
                   "        ON mm.id = bmp.movie_id  " +
                   "    WHERE " +
                   "      bc.user_id = :userId  " +
                   "  ) " +
                   "  AS i;";


        return ResponseEntity
                .status(HttpStatus.OK)
                .body(this.template.queryForObject(
                        sql,
                        new MapSqlParameterSource()
                                .addValue("userId", userId.intValue(), Types.INTEGER)
                                .addValue("premium", premium, Types.INTEGER),
                        this::cartMapping
                ));
    }

    private CartResponse cartMapping(ResultSet rs, int rowNumber) throws SQLException {
        List<Item> items;
        BigDecimal total = BigDecimal.ZERO;

        try {
            String jsonArrayString = rs.getString("items");

            if (jsonArrayString == null) {
                throw new ResultError(BillingResults.CART_EMPTY);
            }

            Item[] itemArray = objectMapper.readValue(jsonArrayString, Item[].class);
            items = Arrays.stream(itemArray).collect(Collectors.toList());

            for (Item item : items) {
                total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to map 'items' to Item[]");
        }

        return new CartResponse()
                .setResult(BillingResults.CART_RETRIEVED)
                .setItems(items)
                .setTotal(total);
    }

    public ResponseEntity<CartResponse> clearCart(Long userId) {
        final String sql =
                "DELETE FROM billing.cart " +
                "WHERE user_id = :userId";

        this.template.update(
                sql,
                new MapSqlParameterSource()
                        .addValue("userId", userId.intValue(), Types.INTEGER)
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new CartResponse().setResult(BillingResults.CART_CLEARED));
    }

    public void cartIsEmpty(Long userId) {
        final String sql =
                "SELECT movie_id " +
                "FROM billing.cart " +
                "WHERE user_id = :userId;";

        List<Integer> movies = this.template.query(
                sql,
                new MapSqlParameterSource()
                        .addValue("userId", userId.intValue(), Types.INTEGER),
                (rs, rowNum) -> rs.getInt("movie_id")
        );

        if (movies.isEmpty()) {
            throw new ResultError(BillingResults.CART_EMPTY);
        }
    }

    public String getCartTitles(Long userId) {
        final String sql =
                "SELECT mm.title " +
                "FROM movies.movie mm " +
                "JOIN billing.cart bc ON bc.movie_id = mm.id " +
                "WHERE bc.user_id = :userId;";

        List<String> titleList = this.template.query(
                sql,
                new MapSqlParameterSource()
                        .addValue("userId", userId.intValue(), Types.INTEGER),
                (rs, rowNum) -> rs.getString("title")
        );

        StringBuilder titles = new StringBuilder();

        for (String title : titleList) {
            titles.append(title).append(", ");
        }

        return titles.substring(0, titles.length()-2).toString();
    }

    public ResponseEntity<PaymentResponse> createSale(Long userId, BigDecimal total) {
        final String sql =
                "INSERT INTO billing.sale (user_id, total, order_date) " +
                "VALUES (:userId, :total, :orderDate);";

        Instant now = Instant.now();

        this.template.update(
                sql,
                new MapSqlParameterSource()
                        .addValue("userId", userId.intValue(), Types.INTEGER)
                        .addValue("total", total, Types.DECIMAL)
                        .addValue("orderDate", Timestamp.from(now), Types.TIMESTAMP)
        );

        final String sql2 =
                "SELECT id " +
                "FROM billing.sale " +
                "ORDER BY order_date DESC " +
                "LIMIT 1;";

        List<Integer> idList =
                this.template.query(
                        sql2,
                        new MapSqlParameterSource(),
                        (rs, rowNum) -> rs.getInt("id")
                );

        if (idList.size() != 1) {
            throw new ResultError(BillingResults.ORDER_LIST_NO_SALES_FOUND);
        }

        final String sql3 =
                "INSERT INTO billing.sale_item (sale_id, movie_id, quantity) " +
                "SELECT :saleId, bc.movie_id, bc.quantity " +
                "FROM billing.cart bc " +
                "WHERE bc.user_id = :userId;";

        this.template.update(
                sql3,
                new MapSqlParameterSource()
                        .addValue("saleId", idList.get(0), Types.INTEGER)
                        .addValue("userId", userId.intValue(), Types.INTEGER)
        );

        final String sql4 =
                "DELETE FROM billing.cart " +
                "WHERE user_id = :userId;";

        this.template.update(
                sql4,
                new MapSqlParameterSource()
                        .addValue("userId", userId.intValue(), Types.INTEGER)
        );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new PaymentResponse()
                        .setResult(BillingResults.ORDER_COMPLETED));
    }

    public ResponseEntity<OrderListResponse> getOrderList(Long userId) {
        final String sql =
                "SELECT id, total, order_date " +
                "FROM billing.sale " +
                "WHERE user_id = :userId " +
                "ORDER BY order_date DESC LIMIT 5;";

        List<Sale> sales = this.template.query(
                sql,
                new MapSqlParameterSource()
                        .addValue("userId", userId.intValue(), Types.INTEGER),
                (rs, rowNum) -> new Sale()
                        .setSaleId(rs.getLong("id"))
                        .setTotal(rs.getBigDecimal("total"))
                        .setOrderDate(rs.getTimestamp("order_date").toInstant())
        );

        if (sales.isEmpty()) {
            throw new ResultError(BillingResults.ORDER_LIST_NO_SALES_FOUND);
        }

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new OrderListResponse()
                        .setResult(BillingResults.ORDER_LIST_FOUND_SALES)
                        .setSales(sales));
    }

    public ResponseEntity<CartResponse> getOrderDetail(Long saleId, Long userId, int premium) {
        final String sql =
            "SELECT DISTINCT bs.total, (SELECT JSON_ARRAYAGG(JSON_OBJECT( " +
                    "'unitPrice', t.unit_price, " +
                    "'quantity', t.quantity, " +
                    "'movieId', t.movie_id, " +
                    "'movieTitle', t.title, " +
                    "'backdropPath', t.backdrop_path, " +
                    "'posterPath', t.poster_path)) " +
                    "FROM (SELECT (IF(:premium = 0, bmp.unit_price, (bmp.unit_price * (1 - (bmp.premium_discount / 100.0))))) AS unit_price, bsi.quantity, bsi.movie_id, mm.title, mm.backdrop_path, mm.poster_path " +
                    "       FROM billing.sale_item bsi " +
                    "       JOIN billing.movie_price bmp ON bmp.movie_id = bsi.movie_id " +
                    "       JOIN movies.movie mm ON mm.id = bmp.movie_id " +
                    "       WHERE bsi.sale_id = :saleId) AS t) AS items " +
                    "FROM billing.sale bs " +
                    "JOIN billing.sale_item bsi ON bsi.sale_id = bs.id " +
                    "WHERE bs.id = :saleId AND bs.user_id = :userId;";

        try {
            CartResponse response = this.template.queryForObject(
                    sql,
                    new MapSqlParameterSource()
                            .addValue("saleId", saleId.intValue(), Types.INTEGER)
                            .addValue("userId", userId.intValue(), Types.INTEGER)
                            .addValue("premium", premium, Types.INTEGER),
                    this::orderDetailMapping);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(response);
        } catch (Exception e) {
            throw new ResultError(BillingResults.ORDER_DETAIL_NOT_FOUND);
        }
    }

    public CartResponse orderDetailMapping(ResultSet rs, Integer rowNumber) {
        List<Item> items;
        BigDecimal total = BigDecimal.ZERO;

        try {
            String jsonArrayString = rs.getString("items");

            if (jsonArrayString == null) {
                throw new ResultError(BillingResults.ORDER_DETAIL_NOT_FOUND);
            }

            Item[] itemArray = objectMapper.readValue(jsonArrayString, Item[].class);
            items = Arrays.stream(itemArray).collect(Collectors.toList());

            for (Item item : items) {
                total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }

        } catch (JsonProcessingException | SQLException e) {
            throw new RuntimeException("Failed to map 'items' to Item[]");
        }

        return new CartResponse()
                .setResult(BillingResults.ORDER_DETAIL_FOUND)
                .setItems(items)
                .setTotal(total);
    }
}
