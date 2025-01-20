// models/User.js
const mongoose = require('mongoose');

const userSchema = new mongoose.Schema({
    username: { type: String, required: true, unique: true },
    email: { type: String, required: true, unique: true },
    password: { type: String, required: true },
    profile_image: { type: String }, // Store URI or file path
    cards: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Card' }], // Reference to Card model
});

module.exports = mongoose.model('User', userSchema);
