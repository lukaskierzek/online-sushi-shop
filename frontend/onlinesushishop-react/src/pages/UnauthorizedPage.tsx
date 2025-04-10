import {Typography} from "@mui/material";

const UnauthorizedPage = () => {
    return (
        <>
            <Typography variant="h2">
                403
            </Typography>
            <Typography variant="body1">
                You do not have permission to access this page!
            </Typography>
        </>
    )
}

export default UnauthorizedPage;
