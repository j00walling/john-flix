package com.github.klefstad_teaching.cs122b.billing.rest;

import com.github.klefstad_teaching.cs122b.billing.model.request.CartRequest;
import com.github.klefstad_teaching.cs122b.billing.model.response.CartResponse;
import com.github.klefstad_teaching.cs122b.billing.repo.BillingRepo;
import com.github.klefstad_teaching.cs122b.billing.util.Validate;
import com.github.klefstad_teaching.cs122b.core.error.ResultError;
import com.github.klefstad_teaching.cs122b.core.result.BillingResults;
import com.github.klefstad_teaching.cs122b.core.security.JWTManager;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
public class CartController
{
    private final BillingRepo repo;

    @Autowired
    public CartController(BillingRepo repo)
    {
        this.repo = repo;
    }

    @PostMapping({"/cart/insert"})
    public ResponseEntity<CartResponse> cartInsert (
            @AuthenticationPrincipal SignedJWT user,
            @RequestBody CartRequest request ) throws ParseException {

        // Checking quantities
        Validate.checkQuantity(request.getQuantity());

        // Check if the item is already in the cart
        if (repo.itemInCart(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID), request.getMovieId())) {
            throw new ResultError(BillingResults.CART_ITEM_EXISTS);
        }

        // Insert item into cart
        return repo.insertItem(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID), request.getMovieId(), request.getQuantity());
    }

    @PostMapping({"/cart/update"})
    public ResponseEntity<CartResponse> cartUpdate (
            @AuthenticationPrincipal SignedJWT user,
            @RequestBody CartRequest request ) throws ParseException {

        // Checking quantities
        Validate.checkQuantity(request.getQuantity());

        // Check if the item is not in the cart
        if (!repo.itemInCart(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID), request.getMovieId())) {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }

        // Update cart
        return repo.updateCart(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID), request.getMovieId(), request.getQuantity());
    }

    @DeleteMapping({"/cart/delete/{movieId}"})
    public ResponseEntity<CartResponse> cartDelete (
            @AuthenticationPrincipal SignedJWT user,
            @PathVariable Long movieId ) throws ParseException {

        // Check if the item is not in the cart
        if (!repo.itemInCart(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID), movieId)) {
            throw new ResultError(BillingResults.CART_ITEM_DOES_NOT_EXIST);
        }

        // Delete item from cart
        return repo.deleteItem(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID), movieId);
    }

    @GetMapping({"/cart/retrieve"})
    public ResponseEntity<CartResponse> cartRetrieve (
            @AuthenticationPrincipal SignedJWT user ) throws ParseException {

        int premium = 0;

        List<String> userRoles = user.getJWTClaimsSet().getStringListClaim(JWTManager.CLAIM_ROLES);
        for (String role : userRoles) {
            if (role.equalsIgnoreCase("premium")) {
                premium = 1;
                break;
            }
        }

        return repo.retrieveCart(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID), premium);
    }

    @PostMapping({"/cart/clear"})
    public ResponseEntity<CartResponse> cartClear (
            @AuthenticationPrincipal SignedJWT user ) throws ParseException {

        // Check if cart is empty
        repo.cartIsEmpty(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID));

        // Clear cart
        return repo.clearCart(user.getJWTClaimsSet().getLongClaim(JWTManager.CLAIM_ID));
    }
}
