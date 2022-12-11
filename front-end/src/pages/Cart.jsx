import React, {useEffect, useState} from "react";
import Billing from "backend/billing";
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TableRow from '@mui/material/TableRow';
import Paper from '@mui/material/Paper';
import styled from "styled-components";
import RemoveIcon from "@material-ui/icons/Remove";
import Button from "@material-ui/core/Button";
import Badge from "@material-ui/core/Badge";
import AddIcon from "@material-ui/icons/Add";
import TableCell from '@mui/material/TableCell'

import { Box, IconButton } from "@mui/material";
import { useUser } from "../hook/User";
import { useNavigate } from "react-router-dom";


const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
`

const Text = styled.div`
  display: flex;
  gap: .3rem;
  text-align: center;
`

const importItems = (movieTitle, unitPrice, quantity, movieId) => {
    return { movieTitle, unitPrice, quantity, movieId };
}

let rows = [];

const Cart = () => {

    const [ cart, setCart ] = React.useState([]);
    const [ total, setTotal ] = React.useState(0);

    const { accessToken } = useUser();

    const navigate = useNavigate();

    const viewCart = () => {
        Billing.retrieveCart(accessToken)
            .then(response => {
                if (response.data.result.code !== 3004) {
                    setCart(response.data.items);
                }

                setTotal(response.data.total);
            })
            .catch(error => console.log(error.response.data, null, 2));
    }

    const updateMovie = (movieId, quantity) => {
        const payload = {
            movieId: movieId,
            quantity: quantity
        }

        Billing.cartUpdate(payload, accessToken)
            .then(
                response => {
                    setCart(response.data.items);
                    setTotal(response.data.total);
                }
            )
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));

        window.location.reload();
    }

    const deleteMovie = (movieId, quantity) => {
        const payload = {
            movieId: movieId,
            quantity: quantity
        }

        Billing.cartDelete(payload, accessToken)
            .then(
                response => {
                    setCart(response.data.items);
                    setTotal(response.data.total);
                })
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));

        window.location.reload();
    }

    const clearCart = (accessToken) => {
        Billing.clearCart(accessToken)
            .catch(error => alert(JSON.stringify(error.response.data, null, 2)));

        window.location.reload();
    }

    rows = [];
    for (let i = 0; i < cart.length; i++) {
        rows.push(importItems(cart[i].movieTitle, cart[i].unitPrice, cart[i].quantity, cart[i].movieId));
    }

    useEffect(() => viewCart(), []);

    return (
        <StyledDiv>
            <TableContainer component={Paper}>
                <Table sx={{ minWidth: 700 }} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell><h3><b>Movie Title</b></h3></TableCell>
                            <TableCell align="right"><h3><b>Price</b></h3></TableCell>
                            <TableCell align="right"><h3><b>Quantity</b></h3></TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {rows.map((item) => (
                            <TableRow
                                key={item.movieTitle}
                                sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                            >
                                <TableCell component="th" scope="row"><span style={{fontSize: 15}}>{item.movieTitle}</span></TableCell>
                                <TableCell align="right"><span style={{fontSize: 15}}>${new Intl.NumberFormat('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format((item.quantity * item.unitPrice))}</span></TableCell>
                                <TableCell align="right">
                                    <Box sx={{ '& button': { m: 0.3 } }}>
                                        <Button variant="outlined" size="large"
                                                onClick={() => {
                                                    if (item.quantity == 1) {
                                                        deleteMovie(item.movieId, item.quantity);
                                                    } else {
                                                        updateMovie(item.movieId, item.quantity-1);
                                                    }
                                                }}
                                        >
                                            <RemoveIcon fontSize="small" />
                                        </Button>
                                        <Badge color="secondary" badgeContent={item.quantity}>
                                            <Button variant="outlined" size="large"
                                                    onClick={() => {
                                                        if (item.quantity != 10) {
                                                            updateMovie(item.movieId, item.quantity+1);
                                                        }
                                                    }}>
                                                <AddIcon fontSize="small" />
                                            </Button>
                                        </Badge>
                                    </Box>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
            <br/>
            <TableContainer component={Paper}>
                <Table sx={{ maxWidth: 800 }} aria-label="simple table">
                    <TableHead>
                        <TableRow>
                            <TableCell>
                                <Text>
                                    <h3><b>Cart Total: </b></h3>
                                    <span style={{ fontSize: 17, paddingTop: 0.5 }}>
                                        ${new Intl.NumberFormat('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(total === undefined ? 0 : total)}
                                    </span>
                                </Text>
                            </TableCell>
                            <TableCell align="right">
                                <Box sx={{ '& button': { m: 1 } }}>
                                    <Button size="small" variant="contained"
                                        onClick={() => clearCart(accessToken)}
                                    >Clear Cart</Button>
                                    <Button size="small" variant="contained"
                                        onClick={() => navigate("/checkout")}
                                    >Checkout</Button>
                                </Box>
                            </TableCell>
                        </TableRow>
                    </TableHead>
                </Table>
            </TableContainer>
        </StyledDiv>
    );
}

export default Cart;