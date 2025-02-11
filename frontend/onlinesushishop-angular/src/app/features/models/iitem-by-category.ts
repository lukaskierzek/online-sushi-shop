import {ISubCategories} from './isub-categories';

export interface IItemByCategory {
  "itemId": number,
  "itemName": string,
  "itemActualPrice": number,
  "itemOldPrice": number,
  "itemImageUrl": string,
  "itemIsHidden": number,
  "itemComment": string,
  "itemMainCategory": string,
  "itemSubcategories": ISubCategories[]
}
