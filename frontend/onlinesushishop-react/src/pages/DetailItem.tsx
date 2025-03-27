import {useParams} from "react-router";

export default function detailItem() {
    const {itemName} = useParams();

    return (
        <p>Details of {itemName}</p>
    );

}

//TODO: details item
