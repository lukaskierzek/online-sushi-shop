import './App.css'
import {Box, Container} from "@mui/material";
import Header from "./components/Header/Header.tsx";
import Navbar from "./components/Navbar/Navbar.tsx";
import Footer from "./components/Footer/Footer.tsx";
import {BrowserRouter, Navigate, Route, Routes} from "react-router";
import ByCategoryItem from "./pages/ByCategoryItem.tsx";
import {Subcategory} from "./enums/subcategory.tsx";
import NotFoundPage from "./pages/NotFoundPage.tsx";


function App() {
    const defaultPageLink: string = `/menu/category/${Subcategory.NEW_ITEM}`;

    return (
        <>
            <Container maxWidth="lg">
                <BrowserRouter>
                    <Box>
                        <Header/>
                    </Box>
                    <Box>
                        <Navbar/>
                    </Box>
                    <Box>
                        <Routes>
                            <Route path="/" element={<Navigate to={defaultPageLink} replace />} />
                            <Route path="/menu/category/:categoryName" element={<ByCategoryItem/>}/>
                            <Route path="*" element={<NotFoundPage />} />
                        </Routes>
                    </Box>
                    <Box>
                        <Footer/>
                    </Box>
                </BrowserRouter>
            </Container>
        </>
    )
}

export default App
