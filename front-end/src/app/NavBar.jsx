import React from "react";
import styled from "styled-components";
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';

import { NavLink } from "react-router-dom";
import {green} from "@mui/material/colors";

const StyledNav = styled.nav`
  display: flex;
  justify-content: center;

  width: calc(100vw - 10px);
  height: 50px;
  padding: 5px;

  background-color: #fff;
`;

const StyledNavLink = styled(NavLink)`
  padding: 10px;
  font-size: 25px;
  color: #000;
  text-decoration: none;
`;

const NavBar = () => {
    return (
        <StyledNav>
            <StyledNavLink to="/register">
                Register
            </StyledNavLink>
            <StyledNavLink to="/login">
                Login
            </StyledNavLink>
            <StyledNavLink to="/">
                Home
            </StyledNavLink>
            <StyledNavLink to="/order/list">
                Order History
            </StyledNavLink>
            <StyledNavLink to="/cart">
                <ShoppingCartIcon sx={{ fontSize: 30 }}/>
            </StyledNavLink>
        </StyledNav>
    );
}

export default NavBar;
