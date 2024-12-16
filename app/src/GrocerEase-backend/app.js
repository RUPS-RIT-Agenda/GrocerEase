const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const cors = require('cors');
const authRoutes = require('./routes/user');
const itemRoutes = require('./routes/item');
const categoryRoutes = require('./routes/category');

const app = express();

const MONGODB_URI = "mongodb+srv://user:IigChwsYtIpq8R21@cluster0.o50mfr6.mongodb.net/grocerease?retryWrites=true&w=majority";
const PORT = 6000;

app.use(bodyParser.json());
app.use(cors());

app.use('/api/user', authRoutes);
app.use('/api/item', itemRoutes)
app.use('/api/category', categoryRoutes)

mongoose.connect(MONGODB_URI)
    .then(() => {
        console.log("Mongo db connected successfully");
        app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
    })
    .catch(err => console.error("MongoDB connection error:", err));
