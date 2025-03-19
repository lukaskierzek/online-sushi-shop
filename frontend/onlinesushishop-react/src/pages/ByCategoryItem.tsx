import {useParams} from "react-router";

export default function ByCategoryItem() {
    const {categoryName} = useParams();

    return (
        <p>Hello to category: {categoryName}</p>
    );
};
