import './App.css'
import {Box, Container} from "@mui/material";
import Header from "./components/Header/Header.tsx";
import Navbar from "./components/Navbar/Navbar.tsx";
import Footer from "./components/Footer/Footer.tsx";
import Main from "./components/Main/Main.tsx";

function App() {
    return (
        <>
            <Container maxWidth="lg">
                <Box>
                    <Header/>
                </Box>
                <Box>
                    <Navbar/>
                </Box>
                <Box>
                    <Main/>
                </Box>
                <Box>
                    <Footer/>
                </Box>
            </Container>
        </>
    )
}

export default App
