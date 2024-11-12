import React from 'react';
import { Link } from 'react-router-dom';

const CityActionSelector = () => {
    return (
        <div>
            <h2>What would you like to do?</h2>
            <div>
                <Link to="/cities/create">
                    <button>Create a New City</button>
                </Link>
            </div>
            <div>
                <Link to="/cities/update">
                    <button>Update an Existing City</button>
                </Link>
            </div>
        </div>
    );
};

export default CityActionSelector;
