import {useEffect, useState} from "react";
import MainCategory from "../interfaces/MainCategory.tsx";
import Subcategory from "../interfaces/Subcategory.tsx";
import {getMainCategories, getSubcategories, postItem} from "../services/Api.tsx";
import axios from "axios";
import {
    Box,
    Button,
    Checkbox,
    FormControlLabel,
    FormGroup,
    FormLabel,
    Radio,
    RadioGroup,
    TextField,
    Typography
} from "@mui/material";

const AddItem = () => {
    const [mainCategory, setMainCategory] = useState<MainCategory[]>([])
    const [subcategory, setSubcategory] = useState<Subcategory[]>([])
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<string | null>(null);

    const [formState, setFormState] = useState({
        itemId: "",
        name: "",
        actualPrice: "",
        oldPrice: "",
        imageURL: "",
        description: "",
        mainCategory: "",
        isHidden: 1,
        subcategories: []
    })

    useEffect(() => {
        const fetchMainCategories = async () => {
            const responseFetchMainCategories = await getMainCategories();
            return responseFetchMainCategories;
        }

        const fetchSubcategories = async () => {
            const responseFetchSubcategories = await getSubcategories();
            return responseFetchSubcategories;
        }

        axios.all([fetchMainCategories(), fetchSubcategories()])
            .then(axios.spread((dataMainCategories, dataSubcategories) => {
                console.log(`dataMainCategories:`);
                console.log(dataMainCategories);
                setMainCategory(dataMainCategories);
                console.log(`dataSubcategories:`);
                console.log(dataSubcategories);
                setSubcategory(dataSubcategories);

                setLoading(false);

                setFormState((prevState) => ({
                    ...prevState,
                    subcategories: dataSubcategories.map((subcat) => ({
                        subcategoryId: subcat.subcategoryId,
                        subcategoryName: subcat.subcategoryName,
                        isChecked: false
                    }))
                }));

            }))
            .catch((error) => {
                console.error(`Error during fetch ${error}`);
                setError((error as Error).message);
            });
    }, [])

    const handleSubmit = async (e) => {
        e.preventDefault();
        const askConfirmAdd: boolean = confirm("Create item?")

        if (askConfirmAdd) {
            const formStateToPost = {...formState}

            formStateToPost.subcategories = formStateToPost.subcategories
                .filter((sub) => sub.isChecked)
                .map((sub) => sub.subcategoryId)
            ;

            formStateToPost.mainCategory = mainCategory
                .filter((maincat) => maincat.mainCategoryName === formStateToPost.mainCategory)
                .map((maincat) => maincat.mainCategoryId)[0]
            ;

            try {
                const responsePostFormState = await postItem(formStateToPost);
                console.info(responsePostFormState);
                alert("Item succesfully added!");
            } catch (error) {
                console.error("Error:", (error as any).response?.data || (error as Error).message);
            }

            console.info(`Form submitted: `);
            console.table(formStateToPost);
        }
    }

    const handleInputChange = (e, index = null) => {
        const {name, value, checked, type} = e.target;

        setFormState((prevState) => {
            const updatedState = {...prevState}

            if (type === "checkbox") {
                if (name === "isHidden") {
                    updatedState.isHidden = checked ? 1 : 0;
                } else if (index !== null) {
                    const updatedSubcategories = [...prevState.subcategories]
                    updatedSubcategories[index] = {
                        ...updatedSubcategories[index],
                        isChecked: checked
                    };
                    updatedState.subcategories = updatedSubcategories;
                }
            } else {
                updatedState[name] = value;
            }

            return updatedState;
        });
    }

    const renderAddItemPage = () => {
        if (loading) return <p>Loading components...</p>;
        if (error) return <p>Error {error}</p>;

        return (
            <>
                <Typography variant="h4">Add item</Typography>
                <form onSubmit={handleSubmit} style={{backgroundColor: "white"}}>
                    <TextField
                        label="Name"
                        name="name"
                        value={formState.name}
                        onChange={handleInputChange}
                        fullWidth
                        margin="normal"
                        required
                    />
                    <TextField
                        label="Actual price in PLN"
                        name="actualPrice"
                        value={formState.actualPrice}
                        onChange={handleInputChange}
                        fullWidth
                        margin="normal"
                        required
                        type="number"
                        slotProps={{
                            htmlInput: {
                                min: 1
                            }
                        }}
                    />
                    <TextField
                        label="Old price in PLN"
                        name="oldPrice"
                        value={formState.oldPrice}
                        onChange={handleInputChange}
                        fullWidth
                        margin="normal"
                        required
                        type="number"
                        slotProps={{
                            htmlInput: {
                                min: 1
                            }
                        }}
                    />
                    <TextField
                        label="Image URL"
                        name="imageURL"
                        value={formState.imageURL}
                        onChange={handleInputChange}
                        fullWidth
                        margin="normal"
                        required
                        multiline
                        variant="outlined"
                        rows={4}
                    />
                    <TextField
                        label="Description"
                        name="description"
                        value={formState.description}
                        onChange={handleInputChange}
                        fullWidth
                        margin="normal"
                        required
                        multiline
                        variant="outlined"
                        rows={4}
                    />

                    <FormLabel component="legend" sx={{marginTop: 2, textAlign: "left"}}>
                        Choose a main category:
                    </FormLabel>
                    <RadioGroup
                        name="mainCategory"
                        onChange={handleInputChange}
                        value={formState.mainCategory}
                    >
                        {mainCategory && mainCategory.length > 0 ? (
                            mainCategory.map((mc, index) => (
                                <FormControlLabel
                                    key={index}
                                    value={mc.mainCategoryName}
                                    control={<Radio/>}
                                    label={mc.mainCategoryName}
                                    sx={{
                                        color: "black"
                                    }}
                                />
                            ))
                        ) : (
                            <Typography variant="body2" color="error">No categories available!</Typography>
                        )}
                    </RadioGroup>

                    <FormLabel component="legend" sx={{marginTop: 2, textAlign: "left"}}>
                        Is hidden?
                    </FormLabel>
                    <FormControlLabel
                        control={
                            <Checkbox
                                name="isHidden"
                                checked={formState.isHidden === 1}
                                onChange={handleInputChange}
                            />
                        }
                        label="Is hidden"
                        sx={{
                            color: "black",
                            textAlign: "left",
                            display: "flex",
                            justifyContent: "flex-start",
                        }}

                    />

                    <FormLabel component="legend" sx={{marginTop: 2, textAlign: "left"}}>
                        Choose a subcategories:
                    </FormLabel>
                    <FormGroup>
                        {formState.subcategories.map((subcat, index) => (
                            <FormControlLabel
                                key={subcat.subcategoryId}
                                control={
                                    <Checkbox
                                        checked={subcat.isChecked}
                                        onChange={(e) => handleInputChange(e, index)}
                                    />
                                }
                                label={subcat.subcategoryName}
                                sx={{
                                    color: "black"
                                }}
                            />
                        ))}
                    </FormGroup>

                    <Button type="submit" variant="contained" color="primary">
                        Add item
                    </Button>
                </form>
                <Box
                    sx={{backgroundColor: "blue", color: 'white', borderRadius: '4px', mt: 4, p: 2}}>
                    <pre>{JSON.stringify(formState, null, 2)}</pre>
                </Box>
            </>
        );
    }

    return (
        <>
            {renderAddItemPage()}
        </>
    );
};

export default AddItem;
