import Config from "backend/config.json";
import Axios from "axios";

async function insertCart(insertRequest, accessToken) {
    const requestBody = {
        movieId: insertRequest.movieId,
        quantity: insertRequest.quantity
    };

    const options = {
        method: "POST",
        baseURL: Config.cartBaseUrl,
        url: Config.billing.insertCart,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

export async function retrieveCart(accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.cartBaseUrl,
        url: Config.billing.retrieveCart,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

async function cartUpdate(cartRequest, accessToken) {
    const requestBody = {
        movieId: cartRequest.movieId,
        quantity: cartRequest.quantity
    };

    const options = {
        method: "POST",
        baseURL: Config.cartBaseUrl,
        url: Config.billing.updateCart,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

async function cartDelete(deleteRequest, accessToken) {
    const requestBody = {
        movieId: deleteRequest.movieId
    }

    const options = {
        method: "DELETE",
        baseURL: Config.cartBaseUrl,
        url: Config.billing.deleteCart + deleteRequest.movieId,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

async function clearCart(accessToken) {
    const options = {
        method: "POST",
        baseURL: Config.cartBaseUrl,
        url: Config.billing.clearCart,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

export default {
    insertCart,
    retrieveCart,
    cartUpdate,
    cartDelete,
    clearCart
}