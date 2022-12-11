import Config from "backend/config.json";
import Axios from "axios";

export async function orderPayment(accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.cartBaseUrl,
        url: Config.order.orderPayment,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function orderComplete(orderRequest, accessToken) {
    const requestBody = {
        paymentIntentId: orderRequest
    };

    const options = {
        method: "POST",
        baseURL: Config.cartBaseUrl,
        url: Config.order.orderComplete,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function orderList(accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.cartBaseUrl,
        url: Config.order.orderList,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export async function getOrderDetail(saleId, accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.cartBaseUrl,
        url: Config.order.orderDetail + saleId,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    };

    return Axios.request(options);
}

export default {
    orderPayment,
    orderComplete,
    orderList,
    getOrderDetail
}