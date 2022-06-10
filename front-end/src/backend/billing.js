import Config from "backend/config.json";
import Axios from "axios";

async function insertCart(insertRequest, accessToken) {
    const requestBody = {
        movieId: insertRequest.movieId,
        quantity: insertRequest.quantity
    };

    const options = {
        method: "GET",
        baseURL: Config.billingBaseUrl,
        url: Config.billing.insertCart,
        data: requestBody,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

export default {
    insertCart
}