const express = require('express');
const Item = require('../models/Item');
const router = express.Router();

router.post('/', async (req, res) => {
    const { name, description, subcategory, company } = req.body;

    if (!name || !description || !subcategory || !company) {
        return res.status(400).json({ error: "Please fill in all fields" });
    }

    try {
        const item = new Item({
            name,
            description,
            subcategory,
            company,
        });

        await item.save();
        res.status(201).json({ message: "Item created successfully", item });
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Server error" });
    }
});

router.get('/', async (req, res) => {
    try {
        const items = await Item.find();
        res.status(200).json(items);
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Server error" });
    }
});

module.exports = router;
