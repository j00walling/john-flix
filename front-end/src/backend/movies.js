import Config from "backend/config.json";
import Axios from "axios";

async function search(searchRequest, accessToken) {
    const queryParams = {
        title: searchRequest.title,
        year: searchRequest.year,
        director: searchRequest.director,
        genre: searchRequest.genre,
        limit: searchRequest.limit,
        page: searchRequest.page,
        orderBy: searchRequest.orderBy,
        direction: searchRequest.direction
    };

    const options = {
        method: "GET",
        baseURL: Config.moviesBaseUrl,
        url: Config.movies.search,
        params: queryParams,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

async function getMovieDetail(queryParams, movieId, accessToken) {
    const options = {
        method: "GET",
        baseURL: Config.moviesBaseUrl,
        url: Config.movies.getMovieDetail + movieId,
        params: queryParams,
        headers: {
            Authorization: "Bearer " + accessToken
        }
    }

    return Axios.request(options);
}

export default {
    search,
    getMovieDetail
}