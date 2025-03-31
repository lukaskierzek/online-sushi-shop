import {useEffect, useState} from "react";
import {getAllItems} from "../services/Api.tsx";
import Item from "../interfaces/Item.tsx";
import {
    Button,
    Paper,
    Table,
    TableBody,
    TableCell,
    TableContainer,
    TableHead,
    TableRow,
    Typography
} from "@mui/material";
import {Subcategory} from "../enums/subcategory.tsx";
import {NavLink} from "react-router";

export default function Admin() {
    const [allItem, setAllItem] = useState<Item[]>([])
    const [loading, setLoading] = useState<boolean>(true)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        const fetchAllItem = async () => {
            try {
                const resultGetAllItem = await getAllItems();
                setAllItem(resultGetAllItem);
            } catch (e) {
                console.error(`Error during fetch all item: ${e}`);
                setError((e as Error).message);
            } finally {
                setLoading(false);
            }
        }

        fetchAllItem();
    }, []);

    const renderContent = () => {
        if (loading) return <p>Loading all items...</p>
        if (error) return <p>Error: {error}</p>
        if (allItem.length === 0) return <p>There is no items!</p>

        return (
            <TableContainer component={Paper} style={{maxHeight: 700}}>
                <Table aria-label="items table">
                    <TableHead>
                        <TableRow>
                            <TableCell align="center">Id</TableCell>
                            <TableCell align="center">Name</TableCell>
                            <TableCell align="center">Actual price</TableCell>
                            <TableCell align="center">Old price</TableCell>
                            <TableCell align="center">Name</TableCell>
                            <TableCell align="center">Img URL</TableCell>
                            <TableCell align="center">Description</TableCell>
                            <TableCell align="center">Is hidden</TableCell>
                            <TableCell align="center">Subcategories</TableCell>
                            <TableCell align="center">Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {allItem.map((row: Item, index: number) => (
                            <TableRow
                                key={index}
                                sx={{
                                    backgroundColor: index % 2 === 0 ? "#f5f5f5" : "#e0e0e0",
                                }}
                            >
                                <TableCell component="th" scope="row" align="center">
                                    {row.itemId}
                                </TableCell>
                                <TableCell align="center">{row.itemName}</TableCell>
                                <TableCell align="center">{row.itemActualPrice}</TableCell>
                                <TableCell align="center">{row.itemOldPrice}</TableCell>
                                <TableCell align="center">{row.itemMainCategory}</TableCell>
                                <TableCell align="center">
                                    <img src={row.itemImageUrl} loading="lazy" alt={`Photo of ${row.itemName}`}/>
                                </TableCell>
                                <TableCell align="center"
                                           className="item-description-width">{row.itemComment}</TableCell>
                                <TableCell align="center">{(row.itemIsHidden === 0 ? 'no' : 'YES')}</TableCell>
                                <TableCell align="center">
                                    {row.itemSubcategories.map((sub) => (
                                        <li className={`item-subcategories
                                        ${sub.subcategoryName === Subcategory.BESTSELLER ? 'bestseller' : sub.subcategoryName.toLowerCase()}`}
                                        >{sub.subcategoryName}</li>
                                    ))}
                                </TableCell>
                                <TableCell align="center">
                                    <Button>
                                        <NavLink to={`/menu/item/${row.itemId}/edit`}>
                                            <Typography color="info">EDIT</Typography>
                                        </NavLink>
                                    </Button>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        );
    };

    return (
        <>
            {renderContent()}
        </>
    );
}
