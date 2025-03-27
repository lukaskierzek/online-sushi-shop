import {useParams} from "react-router";
import {useEffect, useState} from "react";
import Item from "../interfaces/Item.tsx";
import {getItemsByCategory} from "../services/Api.tsx";
import ItemCardGrid from "../components/ItemCardGrid.tsx";

export default function ByCategoryItem() {
    const [itemsByCategory, setItemsByCategory] = useState<Item[]>([]);
    const [loading, setLoading] = useState<boolean>(true)
    const [error, setError] = useState<string | null>(null);
    const {categoryName} = useParams();

    useEffect(() => {
        const fetchAllItemsByCategory = async () => {
            try {
                const responseItemsByCategory = await getItemsByCategory(categoryName);
                setItemsByCategory(responseItemsByCategory);
            } catch (e) {
                console.error(`Error during fetch all items by category '${categoryName}': ${e}`);
                setError((e as Error).message);
            } finally {
                setLoading(false);
            }
        }

        fetchAllItemsByCategory();
    }, [categoryName])

    const renderContentByCategoryItem = () => {
        if (loading) return <p>Loading items...</p>;
        if (error) return <p>Error {error}</p>;
        if (itemsByCategory.length === 0) return <p>There is no item by category {categoryName}</p>;

        return (
            <>
                <ItemCardGrid items={itemsByCategory}/>
            </>
        );
    };


    return (
        renderContentByCategoryItem()
    );
};
