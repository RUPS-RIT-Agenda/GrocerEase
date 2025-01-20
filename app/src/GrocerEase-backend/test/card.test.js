const assert = require('node:assert');
const request = require('supertest');
const app = require('../test');
const Card = require('../models/Card'); 

describe('Card API Tests', () => {
    beforeEach(async () => {
        await Card.deleteMany({});
    });

    describe('POST /api/card/save', () => {
        it('should create a new card with valid data', async () => {
            const payload = {
                barcodeNum: '1234567890',
                shopName: 'Test Shop',
                cardImg: 'https://lh5.googleusercontent.com/proxy/nTDEtnOS000-Z2spbWfiuSxGJX9qwWo_BOiDUOTwDAeIon2AQUfP28wAn9pSWFXw1THhbzxa6GDMo8xDvwzr',
            };

            const res = await request(app).post('/api/card/save').send(payload);

            assert.strictEqual(res.status, 201, 'Response status should be 201');
            assert.strictEqual(res.body.message, 'Card saved successfully');
            assert.strictEqual(res.body.card.barcodeNum, '1234567890');
            assert.strictEqual(res.body.card.shopName, 'Test Shop');
            assert.strictEqual(res.body.card.cardImg, 'https://lh5.googleusercontent.com/proxy/nTDEtnOS000-Z2spbWfiuSxGJX9qwWo_BOiDUOTwDAeIon2AQUfP28wAn9pSWFXw1THhbzxa6GDMo8xDvwzr');

            console.log('Tested saving a card with valid data.');
        });

        it('should handle missing required fields gracefully', async () => {
            const payload = { shopName: 'Test Shop' }; 

            const res = await request(app).post('/api/card/save').send(payload);

            assert.strictEqual(res.status, 500, 'Response status should be 500');
            assert.strictEqual(res.body.message, 'Error saving card');

            console.log('Tested saving a card with missing required fields.');
        });
    });

    describe('GET /api/card/get', () => {
        it('should retrieve all saved cards', async () => {
            const sampleCards = [
                { barcodeNum: '111', shopName: 'Shop A', cardImg: 'img-a.jpg' },
                { barcodeNum: '222', shopName: 'Shop B', cardImg: 'img-b.jpg' },
            ];
            await Card.insertMany(sampleCards);

            const res = await request(app).get('/api/card/get');

            assert.strictEqual(res.status, 200, 'Response status should be 200');
            assert.strictEqual(res.body.length, 2, 'Should return 2 cards');
            assert.strictEqual(res.body[0].shopName, 'Shop A');
            assert.strictEqual(res.body[1].shopName, 'Shop B');

            console.log('Tested retrieving all cards.');
        });

        it('should return an empty array if no cards exist', async () => {
            const res = await request(app).get('/api/card/get');

            assert.strictEqual(res.status, 200, 'Response status should be 200');
            assert.deepStrictEqual(res.body, [], 'Response should be an empty array');

            console.log('Tested retrieving cards when none exist.');
        });
    });
});
