import Subcategory from "./Subcategory.tsx";

export default interface Item {
    itemActualPrice: number,
    itemComment: string,
    itemId: number,
    itemImageUrl: string,
    itemIsHidden: number,
    itemMainCategory: string,
    itemName: string,
    itemOldPrice: number,
    itemSubcategories: Subcategory[]
}
