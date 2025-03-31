import {Typography} from "@mui/material";
import Subcategory from "../interfaces/Subcategory.tsx";
import {Subcategory as subcategoryEnum} from "../enums/subcategory.tsx";
import Item from "../interfaces/Item.tsx";

const renderPriceAndSubcategory = (item: Item) => {
    return (
        <>
            <Typography variant="subtitle1" component="span">
                {item.itemActualPrice < item.itemOldPrice ? (
                    <>
                        <span className={'item-old-price'}>{item.itemOldPrice} zł</span>
                        <span className={'item-actual-price'}>{item.itemActualPrice} zł</span>
                    </>
                ) : (
                    <>
                        <span className={'item-old-price'}></span>
                        <span className={'item-actual-price'}>{item.itemActualPrice} zł</span>
                    </>
                )}
            </Typography>
            <ul className={'ul-subcategories'}>
                {item.itemSubcategories.map((sub: Subcategory) => (
                    <li className={`li-subcategories item-subcategories ${sub.subcategoryName === subcategoryEnum.BESTSELLER ? 'bestseller' : sub.subcategoryName.toLowerCase()}`}>{sub.subcategoryName}</li>
                ))}
            </ul>
        </>
    );
}

export default renderPriceAndSubcategory;
