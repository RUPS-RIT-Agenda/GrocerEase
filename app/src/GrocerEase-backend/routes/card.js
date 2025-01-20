const express = require('express');
const multer = require('multer');
const Card = require('../models/Card');
const path = require('path');
const router = express.Router();

router.post('/save',async (req, res) => {
    try {
        const {barcodeNum, shopName, cardImg} = req.body;

        const newCard = new Card({
            barcodeNum,
            shopName,
            cardImg: cardImg,
        });

        await newCard.save();
        res.status(201).json({message: 'Card saved successfully', card: newCard});
    } catch (error) {
        console.log(error);
    
        res.status(500).json({message: 'Error saving card', error});
    }
});

router.get('/get', async (req, res) => {
    try {
        const cards = await Card.find();
        res.status(200).json(cards);
    } catch (error) {
        res.status(500).json({message: 'Error retrieving cards', error});
    }
});

module.exports = router;
