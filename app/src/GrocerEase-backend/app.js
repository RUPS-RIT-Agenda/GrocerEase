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

const MONGODB_URI = "mongodb+srv://user:IigChwsYtIpq8R21@cluster0.o50mfr6.mongodb.net/grocerease?retryWrites=true&w=majority";
const PORT = 6000;

app.use(bodyParser.json({
    limit: '10000kb'
}));
app.use(cors());

app.use('/api/user', authRoutes);
app.use('/api/item', itemRoutes);
app.use('/api/category', categoryRoutes);
app.use('/api/list', listRoutes);
app.use('/api/card', cardRoutes);
app.use('/api/store', storeRoutes);

mongoose.connect(MONGODB_URI, {
    connectTimeoutMS: 10000,  // 10 seconds timeout
})
.then(() => {
    console.log("Mongo db connected successfully");
    app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
})
.catch(err => console.error("MongoDB connection error:", err));