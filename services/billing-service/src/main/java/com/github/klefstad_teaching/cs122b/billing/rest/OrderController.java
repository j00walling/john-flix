package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.model.request.OrderRequest;
import com.github.klefstad_teaching.cs122b.billing.model.response.CartResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.OrderListResponse;
import com.github.klefstad_teaching.cs122b.billing.model.response.PaymentResponse;
import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.issuing.Cardholder;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

@RestController
public class OrderController
{
    private final BillingRepo repo;
    private final Validate    validate;

    @Autowired
    public OrderController(BillingRepo repo,Validate validate)
    {
        this.repo = repo;
        this.validate = validate;
    }

    @GetMapping({"/order/payment"})
    public ResponseEntity<PaymentResponse> orderPayment(
            @AuthenticationPrincipal SignedJWT user ) throws ParseException, StripeException {

        int premium = 0;

        List<String> userRoles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        for (String role : userRoles) {
            if (role.equalsIgnoreCase("premium")) {
                premium = 1;
                break;
            }
        }

        // Check if cart is empty
        repo.cartIsEmpty(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID));

        // Get cart total
        Long total = Objects.requireNonNull(repo.retrieveCart(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID), premium).getBody()).getTotal().longValue();

        // Get movies
        String titles = repo.getCartTitles(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID));

        // Get user id
        String userId = Long.toString(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID));

        try {
            PaymentIntentCreateParams paymentIntentCreateParams =
                    PaymentIntentCreateParams
                            .builder()
                            .setCurrency("USD")
                            .setDescription(titles)
                            .setAmount(total)
                            .putMetadata("userId", userId)
                            .setAutomaticPaymentMethods(
                                    PaymentIntentCreateParams.AutomaticPaymentMethods
                                            .builder()
                                            .setEnabled(true)
                                            .build()
                            )
                            .build();

            PaymentIntent paymentIntent = PaymentIntent.create(paymentIntentCreateParams);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new PaymentResponse()
                            .setResult(BillingResults.ORDER_PAYMENT_INTENT_CREATED)
                            .setPaymentIntentId(paymentIntent.getId())
                            .setClientSecret(paymentIntent.getClientSecret()));
        } catch (HttpServerErrorException.InternalServerError e) {
            throw new ResultError(BillingResults.STRIPE_ERROR);
        }
    }

    @PostMapping({"/order/complete"})
    public ResponseEntity<PaymentResponse> orderComplete(
            @AuthenticationPrincipal SignedJWT user,
            @RequestBody OrderRequest request ) throws StripeException, ParseException {

        PaymentIntent paymentIntent = PaymentIntent.retrieve(request.getPaymentIntentId());

        // Verify status
        if (!paymentIntent.getStatus().equalsIgnoreCase("succeeded")) {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_NOT_SUCCEEDED);
        }

        // Verify correct user
        if (!String.valueOf(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID)).equals(paymentIntent.getMetadata().get("userId"))) {
            throw new ResultError(BillingResults.ORDER_CANNOT_COMPLETE_WRONG_USER);
        }

        // Create sale
        int premium = 0;

        List<String> userRoles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        for (String role : userRoles) {
            if (role.equalsIgnoreCase("premium")) {
                premium = 1;
                break;
            }
        }

        BigDecimal total = Objects.requireNonNull(repo.retrieveCart(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID), premium).getBody()).getTotal();

        return repo.createSale(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID), total);
    }

    @GetMapping({"/order/list"})
    public ResponseEntity<OrderListResponse> orderList (
            @AuthenticationPrincipal SignedJWT user ) throws ParseException {

        return repo.getOrderList(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID));
    }

    @GetMapping({"/order/detail/{saleId}"})
    public ResponseEntity<CartResponse> orderDetailBySaleId (
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long saleId ) throws ParseException {

        int premium = 0;

        List<String> userRoles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        for (String role : userRoles) {
            if (role.equalsIgnoreCase("premium")) {
                premium = 1;
                break;
            }
        }

        return repo.getOrderDetail(saleId, user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID), premium);
    }
}

