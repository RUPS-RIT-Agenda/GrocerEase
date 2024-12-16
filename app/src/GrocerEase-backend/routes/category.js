const express = require('express');
const Category = require('../models/Category');
const router = express.Router();

router.post('/', async (req, res) => {
    const { name, subcategories } = req.body;

    if (!name || !Array.isArray(subcategories) || subcategories.length === 0) {
        return res.status(400).json({ error: "Name and at least one subcategory are required" });
    }

    try {
        const category = new Category({
            name,
            subcategories,
        });

        await category.save();
        res.status(201).json({ message: "Category created successfully", category });
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Server error" });
    }
});

router.get('/', async (req, res) => {
    try {
        const categories = await Category.find();
        res.status(200).json(categories);
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Server error" });
    }
});

module.exports = router;
