import React, { useEffect, useState } from "react";
import Box from '@mui/material/Box';
import Chip from '@mui/material/Chip';
import Button from '@mui/material/Button';
import Grid from '@mui/material/Grid';
import Stack from '@mui/material/Stack';
import Divider from '@mui/material/Divider';
import Typography from '@mui/material/Typography';
import CheckIcon from '@mui/icons-material/Check';
import Order from "../backend/order"

import { useUser } from "../hook/User";
import { green } from '@mui/material/colors';
import { useNavigate } from "react-router-dom";

const OrderComplete = () => {
    const navigate = useNavigate();
    const { accessToken } = useUser();

    const paymentIntent = new URLSearchParams(window.location.search).get(
        "payment_intent"
    );

    const completeOrder = () => {
        Order.orderComplete(paymentIntent, accessToken);
    }

    useEffect(() => completeOrder(), []);

    return (
        <Box sx={{ width: '100%', maxWidth: 500, bgcolor: 'background.paper' }}>
            <Box sx={{ my: 3, mx: 2 }}>
                <Typography fontSize="3.5vw" gutterBottom variant="h4" component="div">
                    Thank you!
                </Typography>
                <Grid container alignItems="center" spacing={1.5}>
                    <Grid item paddingBottom={0.65}>
                        <CheckIcon sx={{ color: green[500], fontSize: 30 }}/>
                    </Grid>
                    <Grid item>
                        <Typography gutterBottom variant="h4" fontSize={22} sx={{ color: green[500] }} component="div">
                            Your order was successful.
                        </Typography>
                    </Grid>
                </Grid>
                <Typography color="text.secondary" variant="body2">
                    Lorem ipsum dolor sit amet, consectetur adipisicing elit. A, eveniet illo non officia provident voluptatem. Accusantium cum dicta doloremque eum, explicabo hic non nostrum odit quos repellat sed velit voluptates!
                </Typography>
            </Box>
            <Divider variant="middle" />
            <Box sx={{ mt: 0.2, ml: 0.5, '& button': { m: 0.3 } }}>
                <Button variant="text" onClick={() => navigate("/movies/search")}>Home</Button>
                <Button variant="text" onClick={() => navigate("/order/list")}>Order History</Button>
            </Box>

        </Box>
    );
}

export default OrderComplete;