import React from "react";
import styled from "styled-components";

import Login from "pages/Login";
import Home from "pages/Home";
import Register from "pages/Register";
import MovieDetail from "pages/MovieDetail";
import Cart from "pages/Cart";
import Checkout from "pages/Checkout";
import OrderComplete from "pages/OrderComplete";
import Orders from "pages/Orders";

import {Route, Routes} from "react-router-dom";

const StyledDiv = styled.div`
  display: flex;
  justify-content: center;

  width: 100vw;
  height: 100vh;
  padding: 25px;

  background: #ffffff;
  box-shadow: inset 0 3px 5px -3px #000000;
`

const Content = () => {
    return (
        <StyledDiv>
            <Routes>
                <Route path ="/register" element={<Register/>}/>
                <Route path="/login" element={<Login/>}/>
                <Route path="/" element={<Home/>}/>
                <Route path="/movies/search" element={<Home/>}/>
                <Route path="/movie/:movieId" element={<MovieDetail/>}/>
                <Route path="/cart" element={<Cart/>}/>
                <Route path="/checkout" element={<Checkout/>}/>
                <Route path="/order/complete" element={<OrderComplete/>}/>
                <Route path="/order/list" element={<Orders/>}/>
            </Routes>
        </StyledDiv>
    );
}

export default Content;
