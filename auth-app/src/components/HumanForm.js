import {useState} from "react";

const HumanForm = ({ onHumanCreated }) => {
    const [height, setHeight] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        const human = { height };
        onHumanCreated(human);
    };

    return (
        <div>
            <h3>Governor (Human)</h3>
            <form onSubmit={handleSubmit}>

                <div>
                    <label>Height:</label>
                    <input
                        type="number"
                        value={height}
                        onChange={(e) => setHeight(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">Save Governor</button>
            </form>
        </div>
    );
};
export default HumanForm;
