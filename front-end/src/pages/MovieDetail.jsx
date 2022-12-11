import React, {useEffect, useState} from "react";
import styled from "styled-components";
import Movies from "backend/movies";
import Billing from "backend/billing";
import {useUser} from "../hook/User";
import {useNavigate, useParams} from "react-router-dom";
import Button from "@material-ui/core/Button";
import AddIcon from "@material-ui/icons/Add";
import RemoveIcon from "@material-ui/icons/Remove";
import Badge from "@material-ui/core/Badge";
import {Box} from "@material-ui/core";

const Title = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`

const Column = styled.div`
  display: flex;
  flex-direction: column;
  gap: 8px;
  max-width: 350px;
`

const Row = styled.div`
  display: flex;
  flex-direction: row;
  gap: 25px;
  padding-top: 5px;
`

const Text = styled.div`
  display: flex;
  gap: .3rem;
  text-align: center;
`

const MovieDetail = () => {
    const { accessToken } = useUser();
    const [ movie, setMovie ] = React.useState(0);
    const [ genres, setGenres ] = React.useState(0);
    const [ persons, setPersons ] = React.useState(0);
    const [ showMore, setShowMore ] = React.useState(0);
    const [itemCount, setItemCount] = React.useState(0);
    const { movieId } = useParams();
    const navigate = useNavigate();

    useEffect(() => getMovie(), []);

    const getMovie = () => {
        Movies.getMovieDetail(movieId, accessToken)
            .then(response => {
                setMovie(response.data.movie);
                setGenres(response.data.genres);
                setPersons(response.data.persons);
            })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)))

    }

    const addCart = (movieId, quantity) => {
        const payload = {
            movieId: movieId,
            quantity: quantity
        }

        Billing.insertCart(payload, accessToken)
            .then(() => navigate("/cart"))
            .catch(error => console.log(JSON.stringify(error.response.data, null, 2)));
    }

    return (
        <Title>
            <h1>{movie.title}</h1>
            <br/>
            <Row>
                <img src={"https://image.tmdb.org/t/p/w300" + movie.posterPath} />
                <Column>
                    <Text>
                        <h4>Year: </h4>
                        <p>{movie.year}</p>
                    </Text>
                    <Text>
                        <h4>Director: </h4>
                        <p>{movie.director}</p>
                    </Text>
                    <Text>
                        <h4>IMDB Rating: </h4>
                        <p>{movie.rating}/10</p>
                    </Text>
                    {/*<Text>*/}
                        {/*<h4>Cast: </h4>*/}
                        {/*{showMore*/}
                        {/*    ? persons.map((person, i) =>*/}
                        {/*            <Text> {i+1 !== persons.length ? (person.name + `, `) : person.name}</Text>*/}
                        {/*        )*/}
                        {/*    : persons.slice(0, 3).map((person, i) =>*/}
                        {/*        <Text> {i+1 !== persons.length ? (person.name + `, `) : (person.name + `...`)}</Text>*/}
                        {/*    )}*/}
                        {/*<button onClick={() => setShowMore((s) => !s)}>Toggle More</button>*/}
                    {/*</Text>*/}
                    {/*<Text>*/}
                        {/*<h4>Genre(s): </h4>*/}
                        {/*{genres.map((genre, i) =>*/}
                        {/*    <Text> {i+1 !== genres.length ? (genre.name + `, `) : genre.name}</Text>*/}
                        {/*)}*/}
                    {/*</Text>*/}
                    <br/>
                    <h4>Overview</h4>
                    <p>{movie.overview}</p>
                    <br/>
                    <div>
                        <Box sx={{ '& button': { m: 1 } }}>
                            <Button variant="outlined" size="large"
                                onClick={() => {
                                    setItemCount(Math.max(itemCount - 1, 0));
                                }}
                            >
                                <RemoveIcon fontSize="small" />
                            </Button>
                            <Badge color="secondary" badgeContent={itemCount}>
                                <Button variant="outlined" size="medium"
                                    onClick={() => addCart(movie.id, itemCount)}
                                >
                                    {"Cart"}
                                </Button>
                            </Badge>
                            <Button variant="outlined" size="large"
                                onClick={() => {
                                    setItemCount(itemCount + 1);
                                }}
                            >
                                <AddIcon fontSize="small" />
                            </Button>
                        </Box>
                    </div>
                </Column>
            </Row>
        </Title>
    );
}

export default MovieDetail;