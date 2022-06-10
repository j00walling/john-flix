import React, {useState} from "react";
import styled from "styled-components";
import Movies from "backend/movies";

import { useForm } from "react-hook-form";
import { useUser } from "../hook/User";
// import { useTable } from "react-table";
import localStorage from "local-storage";

// export default function BasicTable({ columns, data }) {
//     const {
//         getTableProps, // Sends the needed props to your table.
//         getTableBodyProps, // Sends needed props to your table body
//         headerGroups, // Returns normalized header groups
//         rows, // rows for the table based on the data passed
//         prepareRow // Prepare the row in order to be displayed.
//     } = useTable({
//         columns,
//         data
//     });
// }

const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`

const Home = () => {
    const { accessToken } = useUser();
    const { register, getValues, handleSubmit } = useForm();
    const [ message, setMessage ] = React.useState("");
    const [ page, changePage ] = React.useState(1);

    const submitSearch = () => {
        const movieTitle = getValues("movieTitle");
        const movieYear = getValues("movieYear");
        const movieDirector = getValues("movieDirector");
        const movieGenre = getValues("movieGenre");

        const orderBy = getValues("orderBy");
        const direction = getValues("direction");
        const limit = getValues("limit");

        const payLoad = {
            title: movieTitle !== "" ? movieTitle : null,
            year: movieYear !== "" ? movieYear : null,
            director: movieDirector !== "" ? movieDirector : null,
            genre: movieGenre !== "" ? movieGenre : null,
            limit: limit,
            page: page,
            orderBy: orderBy,
            direction: direction
        }

        Movies.search(payLoad, accessToken)
            .then(response => {
                setMessage(response.data.movies);
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))
    }

    return (
        <StyledDiv>
            <input {...register("movieTitle")} type={"movieTitle"} placeholder="Enter Movie Title..."/>
            <input {...register("movieYear")} type={"movieYear"} placeholder="Enter Movie Year..."/>
            <input {...register("movieDirector")} type={"movieDirector"} placeholder="Enter Movie Director..."/>
            <input {...register("movieGenre")} type={"movieGenre"} placeholder="Enter Movie Genre..."/>
            <button onClick={handleSubmit(submitSearch)}>Search</button>
            <br/>
            <select {...register("orderBy")} type={"orderBy"}>
                <option value="title">Sort By Title</option>
                <option value="rating">Sort By Rating</option>
                <option value="year">Sort By Year</option>
            </select>
            <select {...register("direction")} type={"direction"}>
                <option value="asc">Order By Ascending</option>
                <option value="desc">Order By Descending</option>
            </select>
            <select {...register("limit")} type={"limit"}>
                <option value="10">Display Up To 10 Movies</option>
                <option value="25">Display Up To 25 Movies</option>
                <option value="50">Display Up To 50 Movies</option>
                <option value="100">Display Up To 100 Movies</option>
            </select>
            <br/>
            {!!message && message.map(message => (
                <StyledDiv>
                    <table width = "100%" height = "70"
                           text-align={"center"} border={"1 px solid black"}>
                        <tr>
                            <th>Title</th>
                            <th>Year</th>
                            <th>Director</th>
                            {/*<th>Genre</th>*/}
                        </tr>
                        <tr>
                            <td>
                                <a href={"/movie/" + message.id}>{message.title}</a>
                            </td>
                            <td>{message.year}</td>
                            <td>{message.director}</td>
                            {/*{/<td>{searchResult.genre}</td>/}*/}
                        </tr>
                    </table>
                    {/*<button onClick={() => navigate("/movie/" + message.id)}> More Info </button>*/}
                </StyledDiv>
            ))}
            <br/>
            <button onClick={() => {changePage(page + 1)}} id="pageButton">Next ({page})</button>
            <button onClick={() => changePage(page - 1)} id="pageButton">Back</button>
        </StyledDiv>
    );
}

export default Home;