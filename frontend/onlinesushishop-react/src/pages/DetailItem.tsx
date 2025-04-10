import {NavLink, useParams} from "react-router";
import {useEffect, useState} from "react";
import Item from "../interfaces/Item.tsx";
import {getItem} from "../services/Api.tsx";
import {Button, CardMedia, Grid, Typography} from "@mui/material";
import renderPriceAndSubcategory from "../components/ItemPriceAndSubcategories.tsx";
import {useAuth} from "../context/AuthContext.tsx";
import {Role} from "../enums/role.tsx";

export default function DetailItem() {
    const [itemDetails, setItemDetails] = useState<Item>({
        itemActualPrice: 0,
        itemComment: "",
        itemId: 0,
        itemImageUrl: "",
        itemIsHidden: 0,
        itemMainCategory: "",
        itemName: "",
        itemOldPrice: 0,
        itemSubcategories: []
    });
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const {isAuthenticated, role} = useAuth();

    const {itemId} = useParams();

    useEffect(() => {
        const fetchItem = async () => {
            try {
                const responseFetchItem = await getItem(itemId);
                setItemDetails(responseFetchItem);
            } catch (error) {
                console.error(`Error during fetch item ${itemId}: ${error}`);
                setError((error as Error).message);
            } finally {
                setLoading(false);
            }
        }

        fetchItem();
    }, [itemId]);

    const renderItemDetailsPage = () => {
        if (loading) return <p>Loading items...</p>;
        if (error) return <p>Error {error}</p>;
        if (!itemId) return <p>There is no item by id {itemId}</p>;

        return (
            <>
                <Grid
                    container
                    spacing={2}
                    sx={{
                        backgroundColor: "#a9a9a9",
                    }}
                >
                    <Grid size={6} sx={{
                        minHeight: 300,
                    }}>
                        <CardMedia
                            component="img"
                            image={itemDetails.itemImageUrl}
                            title={`Image of ${itemDetails.itemName}`}
                            sx={{
                                height: "100%",
                            }}
                        />
                    </Grid>
                    <Grid size={6}>
                        <Typography variant="body1" color="textPrimary" component="p"
                                    sx={{textAlign: "left", margin: "20px 0 20px 0"}}>
                            {itemDetails.itemName}
                        </Typography>
                        <Typography variant="body2" color="textSecondary" component="p"
                                    sx={{textAlign: "left", margin: "20px 0 20px 0"}}>
                            {itemDetails.itemComment}
                        </Typography>
                        {renderPriceAndSubcategory(itemDetails)}

                        {isAuthenticated && role === Role.ADMIN && (
                            <Button>
                                <NavLink to={`/menu/item/${itemDetails.itemId}/edit`}>
                                    <Typography color="info">EDIT</Typography>
                                </NavLink>
                            </Button>)}
                    </Grid>
                </Grid>
            </>
        )
            ;
    }

    return (
        <>
            {renderItemDetailsPage()}
        </>
    );

}

//TODO: details item
