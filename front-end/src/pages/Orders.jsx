import React, { useEffect, useState } from "react";
import Order from "backend/order";

import CardActions from '@mui/material/CardActions';
import Box from '@mui/material/Box';
import Card from '@mui/material/Card';
import CardContent from '@mui/material/CardContent';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import styled from "styled-components";
import Grid from '@mui/material/Grid';
import Link from '@mui/material/Link';

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

const importItems = (saleId, total, orderDate) => {
    return { saleId, total, orderDate };
}

let rows = [];
let options = { year: 'numeric', month: 'long', day: 'numeric' };

const Orders = () => {
    const [ sales, setSales ] = React.useState([]);

    const { accessToken } = useUser();

    const navigate = useNavigate();

    const getOrderList = () => {
        Order.orderList(accessToken)
            .then(response => {
                if (response.data.result.code !== 3081) {
                    setSales(response.data.sales);
                }
            })
            .catch(error => console.log(error.response.data, null, 2));
    }

    rows = [];
    for (let i = 0; i < sales.length; i++) {
        rows.push(importItems(sales[i].saleId, sales[i].total, sales[i].orderDate));
    }

    useEffect(() => getOrderList(), []);

    return (
        <StyledDiv>
            <Typography fontSize="2vw" gutterBottom variant="h4" component="div">
                Your Orders
            </Typography>
            <Box sx={{ minWidth: "800px" }}>
                {rows.map((sale) =>
                    <Card sx={{ marginBottom: "12px!important", borderRadius: "4px 4px 0 0", border: "1px #D5D9D9 solid" }}>
                        <CardContent sx={{ backgroundColor: "#F0F2F2" }}>
                            <Grid container paddingBottom={0.6}>
                                <Grid item xs={2.5}>
                                    <Typography color="text.secondary" variant="h3" fontSize={16}>Order Date</Typography>
                                </Grid>
                                <Grid item xs={3}>
                                    <Typography color="text.secondary" variant="h3" fontSize={16}>Total</Typography>
                                </Grid>
                                <Grid item xs={6.5}>
                                    <Grid container direction="row-reverse">
                                        <Grid item><Typography color="text.secondary" variant="h3" fontSize={16}>Order # {sale.saleId}</Typography></Grid>
                                    </Grid>
                                </Grid>
                            </Grid>
                            <Grid container paddingBottom={0.4}>
                                <Grid item xs={2.5}>
                                    <Typography color="text.primary" variant="h3" fontSize={18}>
                                        { new Date(sale.orderDate).toLocaleDateString("en-US", options) }
                                    </Typography>
                                </Grid>
                                <Grid item xs={3}>
                                    <Typography color="text.primary" variant="h3" fontSize={18}>
                                        ${new Intl.NumberFormat('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(sale.total)}
                                    </Typography>
                                </Grid>

                                <Grid item xs={6.5}>
                                    <Grid container direction="row-reverse">
                                        <Grid item>
                                            <Link href={"/order/detail/" + sale.saleId} fontSize={18} underline="hover">Order Details</Link>
                                        </Grid>
                                    </Grid>
                                </Grid>
                            </Grid>
                        </CardContent>
                        <CardContent>

                        </CardContent>
                        <CardActions>
                            <Button size="small">Learn More</Button>
                        </CardActions>
                    </Card>
                )}
            </Box>
        </StyledDiv>
    );
    // return (
    //     <StyledDiv>
    //         <TableContainer component={Paper}>
    //             <Table sx={{ minWidth: 700 }} aria-label="simple table">
    //                 <TableHead>
    //                     <TableRow>
    //                         <TableCell><h3><b>Order</b></h3></TableCell>
    //                         <TableCell align="right"><h3><b>Price</b></h3></TableCell>
    //                         <TableCell align="right"><h3><b>Quantity</b></h3></TableCell>
    //                     </TableRow>
    //                 </TableHead>
    //                 <TableBody>
    //                     {rows.map((item) => (
    //                         <TableRow
    //                             key={item.movieTitle}
    //                             sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
    //                         >
    //                             <TableCell component="th" scope="row"><span style={{fontSize: 15}}>{item.movieTitle}</span></TableCell>
    //                             <TableCell align="right"><span style={{fontSize: 15}}>${new Intl.NumberFormat('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format((item.quantity * item.unitPrice))}</span></TableCell>
    //                             <TableCell align="right">
    //                                 <Box sx={{ '& button': { m: 0.3 } }}>
    //                                     <Button variant="outlined" size="large"
    //                                             onClick={() => {
    //                                                 if (item.quantity == 1) {
    //                                                     deleteMovie(item.movieId, item.quantity);
    //                                                 } else {
    //                                                     updateMovie(item.movieId, item.quantity-1);
    //                                                 }
    //                                             }}
    //                                     >
    //                                         <RemoveIcon fontSize="small" />
    //                                     </Button>
    //                                     <Badge color="secondary" badgeContent={item.quantity}>
    //                                         <Button variant="outlined" size="large"
    //                                                 onClick={() => {
    //                                                     if (item.quantity != 10) {
    //                                                         updateMovie(item.movieId, item.quantity+1);
    //                                                     }
    //                                                 }}>
    //                                             <AddIcon fontSize="small" />
    //                                         </Button>
    //                                     </Badge>
    //                                 </Box>
    //                             </TableCell>
    //                         </TableRow>
    //                     ))}
    //                 </TableBody>
    //             </Table>
    //         </TableContainer>
    //         <br/>
    //         <TableContainer component={Paper}>
    //             <Table sx={{ maxWidth: 800 }} aria-label="simple table">
    //                 <TableHead>
    //                     <TableRow>
    //                         <TableCell>
    //                             <Text>
    //                                 <h3><b>Cart Total: </b></h3>
    //                                 <span style={{ fontSize: 17, paddingTop: 0.5 }}>
    //                                     ${new Intl.NumberFormat('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 }).format(total === undefined ? 0 : total)}
    //                                 </span>
    //                             </Text>
    //                         </TableCell>
    //                         <TableCell align="right">
    //                             <Box sx={{ '& button': { m: 1 } }}>
    //                                 <Button size="small" variant="contained"
    //                                         onClick={() => clearCart(accessToken)}
    //                                 >Clear Cart</Button>
    //                                 <Button size="small" variant="contained"
    //                                         onClick={() => navigate("/checkout")}
    //                                 >Checkout</Button>
    //                             </Box>
    //                         </TableCell>
    //                     </TableRow>
    //                 </TableHead>
    //             </Table>
    //         </TableContainer>
    //     </StyledDiv>
    // );
}

export default Orders;