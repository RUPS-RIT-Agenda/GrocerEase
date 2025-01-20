const assert = require('node:assert');
const request = require('supertest');
const app = require('../test'); 
const Category = require('../models/Category');

describe('Category API Tests', () => {
    before(async () => {
        console.log('Connecting to the test database...');
    });

    beforeEach(async () => {
        await Category.deleteMany({});
    });

    describe('POST /api/category', () => {
        it('should create a new category with valid data', async () => {
            const payload = { name: 'Fruits', subcategories: ['Citrus', 'Berries'] };
            const res = await request(app).post('/api/category').send(payload);

            assert.strictEqual(res.status, 201, 'Response status should be 201');
            assert.strictEqual(res.body.message, 'Category created successfully');
            assert.strictEqual(res.body.category.name, 'Fruits');
            assert.deepStrictEqual(res.body.category.subcategories, ['Citrus', 'Berries']);

            console.log('Tested creating a category with valid data.');
        });

        it('should return a 400 error for invalid data', async () => {
            const payload = { name: '', subcategories: [] };
            const res = await request(app).post('/api/category').send(payload);

            assert.strictEqual(res.status, 400, 'Response status should be 400');
            assert.strictEqual(res.body.error, 'Name and at least one subcategory are required');

            console.log('Tested creating a category with invalid data.');
        });
    });

    describe('GET /api/category', () => {
        it('should retrieve all categories', async () => {
            const sampleCategories = [
                { name: 'Vegetables', subcategories: ['Leafy', 'Root'] },
                { name: 'Dairy', subcategories: ['Milk', 'Cheese'] },
            ];
            await Category.insertMany(sampleCategories);

            const res = await request(app).get('/api/category');

            assert.strictEqual(res.status, 200, 'Response status should be 200');
            assert.strictEqual(res.body.length, 2, 'Response body should have 2 categories');
            assert.strictEqual(res.body[0].name, 'Vegetables');
            assert.deepStrictEqual(res.body[0].subcategories, ['Leafy', 'Root']);
            assert.strictEqual(res.body[1].name, 'Dairy');
            assert.deepStrictEqual(res.body[1].subcategories, ['Milk', 'Cheese']);

            console.log('Tested retrieving all categories.');
        });

        it('should return an empty array when no categories exist', async () => {
            const res = await request(app).get('/api/category');

            assert.strictEqual(res.status, 200, 'Response status should be 200');
            assert.deepStrictEqual(res.body, [], 'Response body should be an empty array');

            console.log('Tested retrieving categories when none exist.');
        });
    });
});
