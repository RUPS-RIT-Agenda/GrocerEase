const express = require('express');
const router = express.Router();
const Store = require('../models/Store.js');

router.get('/', async (req, res) => {
console.log("Called stores");
    try {
        const stores = await Store.find();
        res.status(200).json(stores);
    } catch (error) {
        console.error('Error fetching stores:', error);
        res.status(500).json({ error: 'Failed to fetch stores' });
    }
});

module.exports = router;
