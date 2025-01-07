// models/List.js
const mongoose = require('mongoose');

const listItemSchema = new mongoose.Schema({
    itemId: { type: mongoose.Schema.Types.ObjectId, required: true, ref: 'Item' },
    bought: { type: Boolean, default: false },
    note: { type: String, default: null },
    quantity: { type: String, default: '1' } 
});

const listSchema = new mongoose.Schema({
    name: { type: String, required: true },
    description: { type: String, required: true },
    userID: { type: mongoose.Schema.Types.ObjectId, required: true, ref: 'User' },
    company: { type: String, required: true },
    date: { type: Date, required: true },
    listOfItems: [listItemSchema] 
});

module.exports = mongoose.model('List', listSchema);

