const assert = require('node:assert');
const request = require('supertest');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const mongoose = require('mongoose');
const app = require('../test');
const User = require('../models/User');

describe('User API Tests', () => {
    let testUserId;
    let testToken;

    before(async () => {
        const hashedPassword = await bcrypt.hash('password123', 10);
        const user = await User.create({
            username: 'testuser',
            email: 'testuser@example.com',
            password: hashedPassword,
            profile_image: 'http://example.com/image.jpg',
        });
        testUserId = user._id;

        testToken = jwt.sign(
            { userId: testUserId },
            process.env.JWT_SECRET || 'your_secret_key',
            { expiresIn: '1h' }
        );
    });

    after(async () => {
        await User.deleteMany({});
    });

    describe('POST /api/user/register', () => {
        it('should register a new user with valid data', async () => {
            const payload = {
                username: 'newuser',
                email: 'newuser@example.com',
                password: 'password123',
                profile_image: 'http://example.com/newimage.jpg',
            };

            const res = await request(app).post('/api/user/register').send(payload);

            assert.strictEqual(res.status, 201, 'Expected response status to be 201');
            assert.strictEqual(res.body.message, 'User registered successfully');

            console.log('Tested registering a new user.');
        });

        it('should return 400 if required fields are missing', async () => {
            const payload = {
                email: 'incomplete@example.com',
            };

            const res = await request(app).post('/api/user/register').send(payload);

            assert.strictEqual(res.status, 400, 'Expected response status to be 400');
            assert.strictEqual(res.body.error, 'Please fill in all fields');

            console.log('Tested registering with missing fields.');
        });

        it('should return 400 if the user already exists', async () => {
            const payload = {
                username: 'testuser',
                email: 'testuser@example.com',
                password: 'password123',
            };

            const res = await request(app).post('/api/user/register').send(payload);

            assert.strictEqual(res.status, 400, 'Expected response status to be 400');
            assert.strictEqual(res.body.error, 'User already exists');

            console.log('Tested registering an existing user.');
        });
    });

    describe('POST /api/user/login', () => {
        it('should login a user with valid credentials', async () => {
            const payload = {
                email: 'testuser@example.com',
                password: 'password123',
            };

            const res = await request(app).post('/api/user/login').send(payload);

            assert.strictEqual(res.status, 200, 'Expected response status to be 200');
            assert.strictEqual(res.body.message, 'Login successful');
            assert(res.body.token, 'Token should be returned');
            assert(res.body.userId, 'User ID should be returned');

            console.log('Tested user login with valid credentials.');
        });

        it('should return 400 for invalid credentials', async () => {
            const payload = {
                email: 'testuser@example.com',
                password: 'wrongpassword',
            };

            const res = await request(app).post('/api/user/login').send(payload);

            assert.strictEqual(res.status, 400, 'Expected response status to be 400');
            assert.strictEqual(res.body.error, 'Invalid email or password');

            console.log('Tested user login with invalid credentials.');
        });

        it('should return 400 if required fields are missing', async () => {
            const payload = {
                email: '',
            };

            const res = await request(app).post('/api/user/login').send(payload);

            assert.strictEqual(res.status, 400, 'Expected response status to be 400');
            assert.strictEqual(res.body.error, 'Please fill in all fields');

            console.log('Tested user login with missing fields.');
        });
    });

    describe('GET /api/user/profile', () => {
        it('should fetch the user profile with a valid token', async () => {
            const res = await request(app)
                .get('/api/user/profile')
                .set('Authorization', `Bearer ${testToken}`);

            assert.strictEqual(res.status, 200, 'Expected response status to be 200');
            assert.strictEqual(res.body.username, 'testuser');
            assert.strictEqual(res.body.email, 'testuser@example.com');
            assert.strictEqual(res.body.profile_image, 'http://example.com/image.jpg');

            console.log('Tested fetching user profile with a valid token.');
        });

        it('should return 401 if the token is missing', async () => {
            const res = await request(app).get('/api/user/profile');

            assert.strictEqual(res.status, 401, 'Expected response status to be 401');
            assert.strictEqual(res.body.error, 'Unauthorized access');

            console.log('Tested fetching user profile without a token.');
        });
    });
});
