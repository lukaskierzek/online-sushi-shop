import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import './Styles/index.css'
import App from './App.tsx'
import {AuthProvider} from "./context/AuthContext.tsx"
import {BrowserRouter} from "react-router";

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <BrowserRouter>
            <AuthProvider>
                <App/>
            </AuthProvider>
        </BrowserRouter>
    </StrictMode>,
)
