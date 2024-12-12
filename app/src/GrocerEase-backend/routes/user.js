const express = require('express');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const router = express.Router();

router.post('/register', async (req, res) => {
    console.log("kdsjdfkgkdgfj");
    const { username, email, password, profile_image } = req.body;
    console.log("REGISTER API..");
    console.log(req.body);

    if (!username || !email || !password) {
        return res.status(400).json({ error: "Please fill in all fields" });
    }

    try {
        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return res.status(400).json({ error: "User already exists" });
        }

        //const hashedPassword = await bcrypt.hash(password, 10);

        const user = new User({
            username,
            email,
            password: password,
            profile_image,
        });

        await user.save();
        res.status(201).json({ message: "User registered successfully" });
    } catch (error) {
        res.status(500).json({ error: "Server error" });
    }
});

router.post('/login', async (req, res) => {
    const { email, password } = req.body;
    console.log("LOGIN API..");
    console.log(req.body);

    // Basic validation of input
    if (!email || !password) {
        return res.status(400).json({ error: "Please fill in all fields" });
    }

    try {
        // Attempt to find user by email
        const user = await User.findOne({ email });
        if (!user) {
            return res.status(400).json({ error: "Invalid email or password" });
        }

        // Compare the provided password with the stored password (already hashed)
        if (password !== user.password) {
            return res.status(400).json({ error: "Invalid email or password" });
        }

        // Generate JWT token with the user ID and an expiry time of 1 hour
        const token = jwt.sign(
            { userId: user._id },
            process.env.JWT_SECRET || 'your_secret_key', // Ensure you have a secret key
            { expiresIn: '1h' }
        );

        // Respond with the token and the user ID
        res.status(200).json({
            message: "Login successful",
            token,
            userId: user._id
        });

    } catch (error) {
        console.error("Login error:", error);
        res.status(500).json({ error: "Server error, please try again later" });
    }
});


router.get('/profile', async (req, res) => {
    console.log("profile API..");

    const token = req.headers['authorization']?.split(' ')[1];

    if (!token) {
        return res.status(401).json({ error: "Unauthorized access" });
    }

    try {
        const decoded = jwt.verify(token, process.env.JWT_SECRET || 'your_secret_key');
        const userId = decoded.userId;

        const user = await User.findById(userId).select('-password');
        if (!user) {
            return res.status(404).json({ error: "User not found" });
        }

        const responseData = {
            username: user.username,
            email: user.email,
            profile_image: user.profile_image,
        };

        console.log("Response Data:", responseData);

        res.status(200).json(responseData);
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Server error" });
    }
});


module.exports = router;
