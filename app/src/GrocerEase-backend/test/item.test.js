const assert = require('node:assert');
const request = require('supertest');
const app = require('../test'); 
const Item = require('../models/Item'); 

describe('Item API Tests', () => {
    beforeEach(async () => {
        await Item.deleteMany({});
    });

    describe('POST /api/item', () => {
        it('should create a new item with valid data', async () => {
            const payload = {
                name: 'Apple',
                description: 'A juicy red apple.',
                subcategory: 'Fruits',
                company: 'Nature\'s Farm',
            };

            const res = await request(app).post('/api/item').send(payload);

            assert.strictEqual(res.status, 201, 'Response status should be 201');
            assert.strictEqual(res.body.message, 'Item created successfully');
            assert.strictEqual(res.body.item.name, 'Apple');
            assert.strictEqual(res.body.item.description, 'A juicy red apple.');
            assert.strictEqual(res.body.item.subcategory, 'Fruits');
            assert.strictEqual(res.body.item.company, 'Nature\'s Farm');

            console.log('Tested creating an item with valid data.');
        });

        it('should return a 400 error for missing fields', async () => {
            const payload = { name: 'Apple' };

            const res = await request(app).post('/api/item').send(payload);

            assert.strictEqual(res.status, 400, 'Response status should be 400');
            assert.strictEqual(res.body.error, 'Please fill in all fields');

            console.log('Tested creating an item with missing fields.');
        });
    });

    describe('GET /api/item', () => {
        it('should retrieve all items', async () => {
            const sampleItems = [
                { name: 'Milk', description: 'Fresh milk.', subcategory: 'Dairy', company: 'Milk Co' },
                { name: 'Bread', description: 'Whole wheat bread.', subcategory: 'Bakery', company: 'Bread Co' },
            ];
            await Item.insertMany(sampleItems);

            const res = await request(app).get('/api/item');

            assert.strictEqual(res.status, 200, 'Response status should be 200');
            assert.strictEqual(res.body.length, 2, 'Should return 2 items');
            assert.strictEqual(res.body[0].name, 'Milk');
            assert.strictEqual(res.body[1].name, 'Bread');

            console.log('Tested retrieving all items.');
        });

        it('should return an empty array if no items exist', async () => {
            const res = await request(app).get('/api/item');

            assert.strictEqual(res.status, 200, 'Response status should be 200');
            assert.deepStrictEqual(res.body, [], 'Response should be an empty array');

            console.log('Tested retrieving items when none exist.');
        });
    });

    describe('GET /api/item/:subcategory', () => {
        it('should retrieve items by subcategory', async () => {
            const sampleItems = [
                { name: 'Milk', description: 'Fresh milk.', subcategory: 'Dairy', company: 'Milk Co' },
                { name: 'Cheese', description: 'Aged cheese.', subcategory: 'Dairy', company: 'Cheese Co' },
                { name: 'Bread', description: 'Whole wheat bread.', subcategory: 'Bakery', company: 'Bread Co' },
            ];
            await Item.insertMany(sampleItems);

            const res = await request(app).get('/api/item/Dairy');

            assert.strictEqual(res.status, 200, 'Response status should be 200');
            assert.strictEqual(res.body.length, 2, 'Should return 2 items');
            assert.strictEqual(res.body[0].subcategory, 'Dairy');
            assert.strictEqual(res.body[1].subcategory, 'Dairy');

            console.log('Tested retrieving items by subcategory.');
        });

        it('should return 404 if no items are found for a subcategory', async () => {
            const res = await request(app).get('/api/item/NonexistentCategory');

            assert.strictEqual(res.status, 404, 'Response status should be 404');
            assert.strictEqual(res.body.message, 'No items found for this subcategory');

            console.log('Tested retrieving items for a non-existent subcategory.');
        });
    });
});
