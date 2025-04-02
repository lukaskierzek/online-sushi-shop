import {useState} from "react";
import {login as loginPost} from "../services/Api.tsx";
import {Box, Button, CircularProgress, TextField, Typography} from "@mui/material";
import {useAuth} from "../context/AuthContext.tsx";

const Login = () => {
    const [email, setEmail] = useState<string>("")
    const [password, setPassword] = useState<string>("")
    const [error, setError] = useState<string>("")
    const [loading, setLoading] = useState<boolean>(false);

    const {login} = useAuth()

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();
        setError("");
        setLoading(true);

        try {
            const responseLogin = await loginPost(email, password);

            if (responseLogin && responseLogin.data) {
                login(responseLogin.data);
            } else {
                setError("Unexpected response from server.")
            }
        } catch (e) {
            setError(e.message)
        } finally {
            setLoading(false);
        }

    }

    return (
        <Box
            component="form"
            onSubmit={handleLogin}
            sx={{
                display: "flex",
                flexDirection: "column",
                gap: 2,
                margin: '0 auto',
                backgroundColor: 'white',
                maxWidth: 400,
            }}
        >
            <TextField
                type="text"
                label="Username"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Enter your username"
                variant="outlined"
                required
                sx={{
                    marginTop: 2,
                }}
                disabled={loading}
            />
            <TextField
                type="password"
                label="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
                variant="outlined"
                required
                disabled={loading}
            />

            {error &&
                <Typography color="error" style={{marginTop: '1rem'}}>
                    {error}
                </Typography>
            }

            {loading ? (
                <CircularProgress sx={{alignSelf: "center"}}/> // Wyświetlenie wskaźnika ładowania
            ) : (
                <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    sx={{alignSelf: "center"}}
                >
                    Sign in
                </Button>
            )}

        </Box>
    );
}

export default Login;
