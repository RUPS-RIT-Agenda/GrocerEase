const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const cors = require('cors');
const authRoutes = require('./routes/user');

const app = express();

const MONGODB_URI = "mongodb+srv://user:IigChwsYtIpq8R21@cluster0.o50mfr6.mongodb.net/grocerease?retryWrites=true&w=majority";
const PORT = 6000;

app.use(bodyParser.json());
app.use(cors());

app.use('/api/user', authRoutes);

mongoose.connect(MONGODB_URI, { useNewUrlParser: true, useUnifiedTopology: true })
    .then(() => console.log("MongoDB connected"))
    .catch(err => console.error("MongoDB connection error:", err));


app.listen(PORT, () => console.log(`Server running on port ${PORT}`));
