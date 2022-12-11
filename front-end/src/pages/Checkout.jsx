import React, { useEffect, useState } from "react";
import { Elements } from "@stripe/react-stripe-js";
import Order, { orderPayment } from "../backend/order";

import { useUser } from "../hook/User";
import { loadStripe } from "@stripe/stripe-js";
import { useNavigate } from "react-router-dom";

import CheckoutForm from "./CheckoutForm";

const stripePromise = loadStripe("pk_test_51L1JlYBccHamQUZXn7btQamsfCCZBuEjzzt2GKS2HVZQg1vZ90GRysMLgo564GXuHFuj6MIIGKn23liuE4CmFkL300tzR08K1H");

const Checkout = () => {
    const navigate = useNavigate();

    const { accessToken } = useUser();
    const [ clientSecret, setClientSecret ] = useState("");
    const [ paymentIntentId, setPaymentIntentId ] = useState("");

    const appearance = {
        theme: 'stripe',
        labels: 'floating'
    };

    const options = {
        clientSecret,
        appearance
    };

    // useEffect(() => {
    //     if (accessToken == null) {
    //         navigate("/login");
    //     }
    // });

    useEffect(() => {
        Order.orderPayment(accessToken)
            .then(response => {
                setPaymentIntentId(response.data.paymentIntentId);
                console.log("Checkout PaymentId -> " + paymentIntentId);
                setClientSecret(response.data.clientSecret);
                console.log("Checkout client sec -> " + clientSecret);
            })
            .catch(error => console.log(error.response.data, null, 2));
    }, []);

    return (
        <div className="Checkout">
            {clientSecret && (
                <Elements options={options} stripe={stripePromise}>
                    <CheckoutForm />
                </Elements>
            )}
        </div>
    );
}

export default Checkout;
