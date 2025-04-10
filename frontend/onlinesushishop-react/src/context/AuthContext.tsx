import {createContext, ReactNode, useContext, useState} from "react";
import {useNavigate} from "react-router";
import {jwtDecode} from "jwt-decode";

interface AuthContextType {
    login: (newToken: string) => void;
    logout: () => void;
    isAuthenticated: boolean;
    role: string | null;
}

interface DecodedToken {
    role: string;

    [key: string]: any;
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

    const [role, setRole] = useState<string | null>(() => {
        const savedToken = localStorage.getItem("token");
        if (savedToken) {
            try {
                const decoded = jwtDecode<DecodedToken>(savedToken);
                return decoded.role;
            } catch {
                return null;
            }
        }
        return null;
    });

    const isAuthenticated = !!token;
    const navigate = useNavigate();

    const login = (newToken: string) => {
        localStorage.setItem("token", newToken);
        setToken(newToken);

        try {
            const decoded = jwtDecode<DecodedToken>(newToken);
            setRole(decoded.role);
        } catch {
            setRole(null);
        }

        navigate("/admin");
        window.location.reload();
    }

    const logout = () => {
        localStorage.removeItem("token");
        setToken(null);
        setRole(null);
        navigate("/login");
        window.location.reload();
    }

    return (
        <AuthContext.Provider value={{login, logout, isAuthenticated, role}}>
            {children}
        </AuthContext.Provider>
    );
}
