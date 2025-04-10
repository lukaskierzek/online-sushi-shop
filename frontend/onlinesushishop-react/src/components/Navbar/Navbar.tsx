import {useEffect, useState} from "react";
import {getMainCategories} from "../../services/Api.tsx";
import {Button, Stack} from "@mui/material";
import {Subcategory} from "../../enums/subcategory.tsx";
import {NavLink} from "react-router";
import MainCategory from "../../interfaces/MainCategory.tsx";
import {useAuth} from "../../context/AuthContext.tsx";
import {Role} from "../../enums/role.tsx";


export default function Navbar() {
    const [mainCategories, setMainCategories] = useState<MainCategory[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);
    const {isAuthenticated, logout, role} = useAuth();

    useEffect(() => {
        const fetchMainCategoriesData = async () => {
            try {
                const result = await getMainCategories();
                setMainCategories(result);
            } catch (error) {
                console.error('Error during fetch main category: ', error)
                setError((error as Error).message)
            } finally {
                setLoading(false);
            }
        };

        fetchMainCategoriesData();
    }, []);

    // const handleLogout = () => {
    //     localStorage.removeItem("token");
    //     window.location.reload();
    // }

    const renderContent = () => {
        if (loading) return <p>Loading categories...</p>;
        if (error) return <p>Error: {error}</p>;
        if (mainCategories.length === 0) return <p>There is no categories!</p>;

        return (
            <Stack
                direction="row"
                sx={{
                    alignItems: "center",
                    justifyContent: "center",
                }}
            >

                {!isAuthenticated ? (
                    <Button>
                        <NavLink to='/login'>Sign in</NavLink>
                    </Button>
                ) : (
                    <>
                        <Button onClick={logout}>
                            Sign out
                        </Button>
                        {role === Role.ADMIN && (<Button>
                            <NavLink to="/admin">Admin page</NavLink>
                        </Button>)}
                    </>
                )}

                <Button>
                    <NavLink
                        to={`/menu/category/${Subcategory.NEW_ITEM}`}>{Subcategory.NEW_ITEM.replace("-", " ")}!</NavLink>
                </Button>
                <Button>
                    <NavLink to={`/menu/item/sale`}>SALE!</NavLink>
                </Button>
                {mainCategories.map((mc) => (
                    <Button>
                        <NavLink to={`/menu/category/${mc.mainCategoryName}`}>{mc.mainCategoryName}</NavLink>
                    </Button>
                ))}
            </Stack>
        );
    };

    return (
        <>
            {renderContent()}
        </>
    );
};
