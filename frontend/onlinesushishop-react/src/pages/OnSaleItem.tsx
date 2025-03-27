import {useEffect, useState} from "react";
import Item from "../interfaces/Item.tsx";
import {getItemsOnSale} from "../services/Api.tsx";
import ItemCardGrid from "../components/ItemCardGrid.tsx";

export default function OnSaleItem() {
    const [itemOnSale, setItemOnSale] = useState<Item[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchAllItemsOnSale = async () => {
            try {
                const responseFetchAllItemsOnSale = await getItemsOnSale();
                setItemOnSale(responseFetchAllItemsOnSale);
            } catch (e) {
                console.error(`Error during fetch all items on sale: ${e}`);
                setError((e as Error).message);
            } finally {
                setLoading(false);
            }
        }

        fetchAllItemsOnSale();
    }, [])

    const renderContentItemsOnSale = () => {
        if (loading) return <p>Loading...</p>;
        if (error) return <p>Error: {error}</p>;
        if (itemOnSale.length === 0) return <p>There is no items on sale!:(</p>

        return (
            <ItemCardGrid items={itemOnSale}/>
        );

    }

    return (
        <>
            {renderContentItemsOnSale()}
        </>
    );
};
