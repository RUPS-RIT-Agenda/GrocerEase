const express = require('express');
const router = express.Router();
const Store = require('../models/Store.js');
const User = require('../models/User.js');

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

router.post('/favorites', async (req, res) => {
    const { userId, storeId } = req.body;

    try {
        const store = await Store.findById(storeId);
        if (!store) {
            return res.status(404).json({ error: 'Store not found' });
        }

        const user = await User.findById(userId);
        if (!user) {
            return res.status(404).json({ error: 'User not found' });
        }

        if (user.favorites.includes(storeId)) {
            return res.status(400).json({ error: 'Store is already in favorites' });
        }

        user.favorites.push(storeId);
        await user.save();

        res.status(200).json({ message: 'Store added to favorites', favorites: user.favorites });
    } catch (error) {
        console.error('Error adding store to favorites:', error);
        res.status(500).json({ error: 'Failed to add store to favorites' });
    }
});

router.get('/favorites/:userId', async (req, res) => {
    const { userId } = req.params;

    try {
        const user = await User.findById(userId).populate('favorites');
        if (!user) {
            return res.status(404).json({ error: 'User not found' });
        }

        res.status(200).json({ favorites: user.favorites });
    } catch (error) {
        console.error('Error fetching favorite stores:', error);
        res.status(500).json({ error: 'Failed to fetch favorite stores' });
    }
});

module.exports = router;
