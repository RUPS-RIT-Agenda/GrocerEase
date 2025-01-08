const mongoose = require('mongoose');

const storeSchema = new mongoose.Schema({
    brand: { type: String, required: true },
    name: { type: String, required: true },
    imageUrl: { type: String, required: true },
    address: { type: String, required: true },
    phoneNumber: { type: String, required: true },
    coordinates: {
        lat: { type: Number, required: true },
        lng: { type: Number, required: true },
    },
});

module.exports = mongoose.model('Store', storeSchema);
