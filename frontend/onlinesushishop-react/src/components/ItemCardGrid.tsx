import Item from "../interfaces/Item.tsx";
import {Card, CardActionArea, CardActions, CardContent, CardMedia, Grid, Typography} from "@mui/material";
import {Link} from "react-router";
import renderPriceAndSubcategory from "./ItemPriceAndSubcategories.tsx";


const ItemCardGrid: React.FC<{ items: Item[] }> = ({items}) => {

    return (
        <Grid container spacing={2} sx={{
            display: 'flex',
            justifyContent: 'left',
            alignItems: "center",
        }}>
            {items.map((item: Item, index: number) => (
                <Grid size={{
                    xs: 12,
                    sm: 6,
                    md: 4
                }} key={index}>

                    <Card sx={{minWidth: 150, minHeight: 410, backgroundColor: "#a9a9a9"}}>
                        <CardActionArea
                            component={Link}
                            to={`/menu/item/${item.itemId}`}
                        >
                            <CardMedia
                                component="img"
                                image={item.itemImageUrl}
                                title={`Image of ${item.itemName}`}
                            />
                            <CardContent sx={{height: 125}}>
                                <Typography gutterBottom variant="h5" component="div">
                                    {item.itemName}
                                </Typography>
                                <Typography variant="body2" sx={{color: 'text.secondary'}}>
                                    {item.itemComment}
                                </Typography>

                            </CardContent>
                        </CardActionArea>
                        <CardActions>
                            {renderPriceAndSubcategory(item)}
                        </CardActions>
                    </Card>
                </Grid>
            ))}
        </Grid>
    );
}

export default ItemCardGrid;
