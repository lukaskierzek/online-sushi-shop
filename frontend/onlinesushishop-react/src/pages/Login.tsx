import {useState} from "react";
import {useNavigate} from "react-router";
import {login as loginPost} from "../services/Api.tsx";
import {Box, Button, TextField, Typography} from "@mui/material";
import {useAuth} from "../context/AuthContext.tsx";

const Login = () => {
    const [email, setEmail] = useState<string>("")
    const [password, setPassword] = useState<string>("")
    const [error, setError] = useState<string>("")
    const navigate = useNavigate();

    const {login} = useAuth()

    const handleLogin = async (e: React.FormEvent) => {
        e.preventDefault();

        setError("");

        try {
            const responseLogin = await loginPost(email, password);

            if (responseLogin && responseLogin.data) {
                login(responseLogin.data);
            } else {
                setError("Unexpected response from server.")
            }
        } catch (e) {
            setError(e.message)
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
            />
            <TextField
                type="password"
                label="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Enter your password"
                variant="outlined"
                required
            />

            {error &&
                <Typography color="error" style={{marginTop: '1rem'}}>
                    {error}
                </Typography>
            }

            <Button
                type="submit"
                variant="contained"
                color="primary"
                sx={{alignSelf: "center"}}
            >
                Sign in
            </Button>
        </Box>
    );
}

export default Login;
