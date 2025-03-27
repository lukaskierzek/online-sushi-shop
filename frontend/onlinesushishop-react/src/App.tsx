import './App.css'
import {Box, Container} from "@mui/material";
import Header from "./components/Header/Header.tsx";
import Navbar from "./components/Navbar/Navbar.tsx";
import Footer from "./components/Footer/Footer.tsx";
import {BrowserRouter} from "react-router";
import Main from "./components/Main/Main.tsx";


function App() {
    return (
        <>
            <Container maxWidth={"lg"}>
                <BrowserRouter>
                    <header>
                        <Box>
                            <Header/>
                        </Box>
                    </header>
                    <nav>
                        <Box>
                            <Navbar/>
                        </Box>
                    </nav>
                    <main>
                        <Box className="main-box-margin">
                            <Main/>
                        </Box>
                    </main>
                    <footer>
                        <Box>
                            <Footer/>
                        </Box>
                    </footer>
                </BrowserRouter>
            </Container>
        </>
    )
}

export default App
