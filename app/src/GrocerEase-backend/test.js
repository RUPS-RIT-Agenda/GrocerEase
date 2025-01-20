const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const cors = require('cors');
const authRoutes = require('./routes/user');
const itemRoutes = require('./routes/item');
const categoryRoutes = require('./routes/category');
const listRoutes = require('./routes/list');
const cardRoutes = require('./routes/card');
const storeRoutes = require('./routes/store');

const app = express();

const MONGODB_TEST_URI = "mongodb+srv://user:IigChwsYtIpq8R21@cluster0.o50mfr6.mongodb.net/test?retryWrites=true&w=majority";
mongoose.connect(MONGODB_TEST_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
})
    .then(() => console.log("Connected to MongoDB test database"))
    .catch(err => console.error("MongoDB connection error:", err));

app.use(bodyParser.json({ limit: '10000kb' }));
app.use(cors());

app.use('/api/user', authRoutes);
app.use('/api/item', itemRoutes);
app.use('/api/category', categoryRoutes);
app.use('/api/list', listRoutes);
app.use('/api/card', cardRoutes);
app.use('/api/store', storeRoutes);

module.exports = app; 