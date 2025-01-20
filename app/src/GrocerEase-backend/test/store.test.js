const assert = require('node:assert');
const request = require('supertest');
const mongoose = require('mongoose');
const app = require('../test'); 
const Store = require('../models/Store');
const User = require('../models/User');

describe('Store API Tests', () => {
    let testUserId;
    let testStoreId;

    before(async () => {
        const user = await User.create({
            username: 'testuser',
            email: 'testuser@example.com',
            password: 'password123',
            favorites: [], 
        });
        testUserId = user._id;

        const store = await Store.create({
            brand: 'Test Brand',
            name: 'Test Store',
            imageUrl: 'http://example.com/image.jpg',
            address: '123 Test Street',
            phoneNumber: '123-456-7890',
            coordinates: {
                lat: 40.7128,
                lng: -74.0060,
            },
        });
        testStoreId = store._id;
    });

    after(async () => {
        await Store.deleteMany({});
        await User.deleteMany({});
    });

    describe('GET /api/store', () => {
        it('should fetch all stores', async () => {
            const res = await request(app).get('/api/store');

            assert.strictEqual(res.status, 200);
            assert.strictEqual(res.body.length, 1);
            assert.strictEqual(res.body[0].name, 'Test Store');

            console.log('Tested fetching all stores.');
        });
    });

    describe('POST /api/store/favorites', () => {
        it('should return 404 if the store does not exist', async () => {
            const payload = {
                userId: testUserId,
                storeId: new mongoose.Types.ObjectId(),
            };

            const res = await request(app).post('/api/store/favorites').send(payload);

            assert.strictEqual(res.status, 404);
            assert.strictEqual(res.body.error, 'Store not found');

            console.log('Tested adding a non-existent store to favorites.');
        });

        it('should return 404 if the user does not exist', async () => {
            const payload = {
                userId: new mongoose.Types.ObjectId(),
                storeId: testStoreId,
            };

            const res = await request(app).post('/api/store/favorites').send(payload);

            assert.strictEqual(res.status, 404);
            assert.strictEqual(res.body.error, 'User not found');

            console.log('Tested adding a store to favorites for a non-existent user.');
        });
    });

    describe('GET /api/store/favorites/:userId', () => {
        it('should return 404 if the user does not exist', async () => {
            const unknownUserId = new mongoose.Types.ObjectId();

            const res = await request(app).get(`/api/store/favorites/${unknownUserId}`);

            assert.strictEqual(res.status, 404);
            assert.strictEqual(res.body.error, 'User not found');

            console.log('Tested fetching favorite stores for a non-existent user.');
        });
    });
});
