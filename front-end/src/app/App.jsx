import React from "react";
import styled from "styled-components";

import Content from 'app/Content';
import NavBar from 'app/NavBar';

import { UserProvider } from "hook/User";


const StyledDiv = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  width: 100%;
  height: 100%;
`

const App = () => {
    return (
        <UserProvider>
                <StyledDiv>
                    <NavBar/>
                    <Content/>
                </StyledDiv>
        </UserProvider>
    );
};

export default App;
