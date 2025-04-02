import {Navigate, Route, Routes} from "react-router";
import Admin from "../../pages/Admin.tsx";
import ByCategoryItem from "../../pages/ByCategoryItem.tsx";
import DetailItem from "../../pages/DetailItem.tsx";
import NotFoundPage from "../../pages/NotFoundPage.tsx";
import {Subcategory} from "../../enums/subcategory.tsx";
import OnSaleItem from "../../pages/OnSaleItem.tsx";
import EditItem from "../../pages/EditItem.tsx";
import PrivateRoute from "../PrivateRoute.tsx";
import Login from "../../pages/Login.tsx";

export default function Main() {
    const defaultPageLink: string = `/menu/category/${Subcategory.NEW_ITEM}`;
    return (
        <Routes>
            <Route path="/login" element={<Login/>}></Route>

            <Route path="/" element={<Navigate to={defaultPageLink} replace/>}/>

            <Route element={<PrivateRoute/>}>
                <Route path="/admin" element={<Admin/>}/>
                <Route path="/menu/item/:itemId/edit" element={<EditItem/>}/>
            </Route>

            <Route path="/menu/item/sale" element={<OnSaleItem/>}/>
            <Route path="/menu/category/:categoryName" element={<ByCategoryItem/>}/>
            <Route path="/menu/item/:itemId" element={<DetailItem/>}/>

            <Route path="*" element={<NotFoundPage/>}/>
        </Routes>
    );
};
