import {createContext, ReactNode, useContext, useState} from "react";
import {useNavigate} from "react-router";

interface AuthContextType {
    login: (newToken: string) => void;
    logout: () => void;
    isAuthenticated: boolean;

}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
    const context = useContext(AuthContext);

    if (!context) {
        throw new Error("'useAuth' must be used within an AuthProvider!");
    }

    return context;
};

export const AuthProvider = ({children}: { children: ReactNode }) => {
    const [token, setToken] = useState<string | null>(localStorage.getItem("token"));
    const isAuthenticated = !!token;
    const navigate = useNavigate();

    const login = (newToken: string) => {
        localStorage.setItem("token", newToken);
        setToken(newToken);
        navigate("/admin");
        window.location.reload();
    }

    const logout = () => {
        localStorage.removeItem("token");
        setToken(null);
        navigate("/login");
        window.location.reload();
    }

    return (
        <AuthContext.Provider value={{login, logout, isAuthenticated}}>
            {children}
        </AuthContext.Provider>
    );
}
