import axios from "axios";
import logGetMessage from "./logGetMessage.tsx";

const API_URL: string = 'http://localhost:8080/api/v1/onlinesushishop';

export const getMainCategories = async () => {
    // console.log("getMainCategories called");
    try {
        const response = await axios.get(`${API_URL}/main-category/non-hidden`);
        logGetMessage("Main categories", response.data);
        return response.data;
    } catch (error) {
        console.error('Error during fetch main categories: ', error);
        return [];
    }
}
