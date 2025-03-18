import {Typography} from "@mui/material";

export default function Footer() {
    const currentYear: number = new Date().getFullYear();

    return (
        <footer>
            <Typography variant="body2" component="span">
               © {currentYear}. MIT Licence. Author Łukasz Kierzek. <a href="https://github.com/lukaskierzek/online-sushi-shop" target="_blank" rel="noreffer">Source code.</a>
            </Typography>
        </footer>
    );
};
