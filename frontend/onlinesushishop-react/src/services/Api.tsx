import axios from "axios";
import logGetMessage from "./logGetMessage.tsx";

const API_URL: string = 'http://localhost:8080/api/v1/onlinesushishop';
const API_URL_AUTH: string = 'http://localhost:8080/api/v1/onlinesushishop/auth';

export const getMainCategories = async () => {
    try {
        const response = await axios.get(`${API_URL}/main-category/non-hidden`);
        logGetMessage("Main categories", response.data);
        return response.data;
    } catch (error) {
        console.error('Error during fetch main categories: ', error);
        return [];
    }
};

export const getAllItems = async () => {
    try {
        const responseGetAllItems = await axios.get(`${API_URL}/item`);
        logGetMessage("All items", responseGetAllItems.data);
        return responseGetAllItems.data
    } catch (error) {
        console.error(`Error during fetch all items: ${error}`);
        return [];
    }
};

export const getItemsByCategory = async (category: string | undefined) => {
    try {
        const responseGetItemsByCategory = await axios.get(`${API_URL}/item/non-hidden/by-category?category=${category}`);
        logGetMessage("Items by category", responseGetItemsByCategory.data);
        return responseGetItemsByCategory.data;
    } catch (error) {
        console.error(`Error during fetch items by category: ${error}`);
        return [];
    }
};

export const getItemById = async (id: number | undefined) => {
    try {
        const responseGetItemById = await axios.get(`${API_URL}/item/${id}`);
        logGetMessage("Item by Id", responseGetItemById.data);
        return responseGetItemById.data;
    } catch (error) {
        console.error(`Error during fetch itemById: ${error}`);
        return [];
    }
};

export const getItemsOnSale = async () => {
    try {
        const responseGetItemsOnSale = await axios.get(`${API_URL}/item/non-hidden/sale`);
        logGetMessage("All items on sale", responseGetItemsOnSale.data);
        return responseGetItemsOnSale.data;
    } catch (error) {
        console.error(`Error during fetch all items on sale: ${error}`);
        return [];
    }
};

export const getItem = async (itemId: string | undefined) => {
    try {
        const reponseGetItem = await axios.get(`${API_URL}/item/${itemId}`);
        logGetMessage("Item by id", reponseGetItem.data);
        return reponseGetItem.data;
    } catch (error) {
        console.error(`Error during fetch item: ${error}`);
        return [];
    }
};

export const getSubcategories = async () => {
    try {
        const responseGetSubcategories = await axios.get(`${API_URL}/subcategory/non-hidden`);
        logGetMessage("All subcategories", responseGetSubcategories.data);
        return responseGetSubcategories.data;
    } catch (error) {
        console.error(`Error during fetch subcategories: ${error}`);
        return [];
    }
};

export const putItem = async (itemFromForm) => {
    try {
        const responsePutItemFromForm = await axios.put(`${API_URL}/item/${itemFromForm.itemId}`, {
            "itemName": itemFromForm.name,
            "itemActualPrice": itemFromForm.actualPrice,
            "itemOldPrice": itemFromForm.oldPrice,
            "itemImageUrl": itemFromForm.imageURL,
            "itemComment": itemFromForm.description,
            "itemMainCategoryId": itemFromForm.mainCategory,
            "itemIsHidden": itemFromForm.isHidden,
            "itemSubcategoriesId": itemFromForm.subcategories
        })
        return responsePutItemFromForm;
    } catch (error) {
        console.error(`Error during put item: ${error}`);
        throw error;
    }
};

export const postItem = async (itemFromForm) => {
    try {
        const responsePostItemFromForm = await axios.post(`${API_URL}/item`, {
            "itemName": itemFromForm.name,
            "itemActualPrice": itemFromForm.actualPrice,
            "itemOldPrice": itemFromForm.oldPrice,
            "itemImageUrl": itemFromForm.imageURL,
            "itemComment": itemFromForm.description,
            "itemMainCategoryId": itemFromForm.mainCategory,
            "itemIsHidden": itemFromForm.isHidden,
            "itemSubcategoriesId": itemFromForm.subcategories
        })
        return responsePostItemFromForm;
    } catch (error) {
        console.error(`Error during post item: ${error}`);
        throw error;
    }
}

export const login = async (username: string, password: string) => {
    try {
        const responseLogin = await axios.post(`${API_URL_AUTH}/login`, {
            username: username,
            password: password
        });

        return responseLogin;
    } catch (error: unknown) {
        if (axios.isAxiosError(error)) {
            if (error.response?.status === 401) {
                throw new Error(error.response?.data || "Wrong username or password!");
            }
            throw new Error("An unexpected error occurred.");
        }

    }
}
