// models/Card.js
const mongoose = require('mongoose');

const cardSchema = new mongoose.Schema({
    barcodeNum: { type: String, required: false },
    shopName: { type: String, required: false },
    cardImg: { type: String, required: true}
}, {
    timestamps: true
});

const Card = mongoose.model('Card', cardSchema);

module.exports = Card;
