const assert = require('node:assert');
const request = require('supertest');
const mongoose = require('mongoose');
const app = require('../test'); 
const List = require('../models/List');
const Item = require('../models/Item');

describe('List API Tests', () => {
    let testUserId;
    let testItemId;

    before(async () => {
        testUserId = new mongoose.Types.ObjectId(); 
        testItemId = new mongoose.Types.ObjectId();

        await Item.create({
            _id: testItemId,
            name: 'Sample Item',
            description: 'Test description',
            subcategory: 'Test Subcategory',
            company: 'Test Company',
        });
    });

    after(async () => {
        await List.deleteMany({});
        await Item.deleteMany({});
    });

    beforeEach(async () => {
        await List.deleteMany({});
    });

    describe('POST /create-empty-list', () => {
        it('should create an empty list with valid data', async () => {
            const payload = {
                userID: testUserId,
                company: 'Test Company',
                name: 'Test List',
                description: 'This is a test list',
                date: new Date(),
            };

            const res = await request(app).post('/api/list/create-empty-list').send(payload);

            assert.strictEqual(res.status, 201);
            assert.strictEqual(res.body.message, 'List created successfully');
            assert.strictEqual(res.body.list.name, 'Test List');
            assert.strictEqual(res.body.list.company, 'Test Company');

            console.log('Tested creating an empty list with valid data.');
        });

        it('should return a 400 error if date is missing', async () => {
            const payload = {
                userID: testUserId,
                company: 'Test Company',
                name: 'Test List',
                description: 'This is a test list',
            };

            const res = await request(app).post('/api/list/create-empty-list').send(payload);

            assert.strictEqual(res.status, 400);
            assert.strictEqual(res.body.message, 'Date is required');

            console.log('Tested creating a list without a date.');
        });
    });

    describe('GET /usersLists/:userID', () => {
        it('should retrieve all lists for a user', async () => {
            await List.create({
                name: 'User List',
                description: 'List for testing retrieval',
                userID: testUserId,
                company: 'Test Company',
                date: new Date(),
                listOfItems: [],
            });

            const res = await request(app).get(`/api/list/usersLists/${testUserId}`);

            console.log('Retrieved lists:', res.body.lists);

            assert.strictEqual(res.status, 200);
            assert.strictEqual(res.body.lists.length, 1);
            assert.strictEqual(res.body.lists[0].name, 'User List');

            console.log('Tested retrieving lists for a user.');
        });

        it('should return 404 if no lists exist for the user', async () => {
            const unknownUserId = new mongoose.Types.ObjectId();

            const res = await request(app).get(`/api/list/usersLists/${unknownUserId}`);

            assert.strictEqual(res.status, 404);
            assert.strictEqual(res.body.message, 'No lists found for this user');

            console.log('Tested retrieving lists for a non-existent user.');
        });
    });

    describe('POST /:listId/add-item', () => {
        let testListId;

        beforeEach(async () => {
            const list = await List.create({
                name: 'List with Items',
                description: 'Test list',
                userID: testUserId,
                company: 'Test Company',
                date: new Date(),
                listOfItems: [],
            });
            testListId = list._id;
        });

        it('should add an item to a list', async () => {
            const payload = {
                itemId: testItemId,
                bought: false,
                note: 'Test note',
                quantity: '2',
            };

            const res = await request(app).post(`/api/list/${testListId}/add-item`).send(payload);

            assert.strictEqual(res.status, 200);
            assert.strictEqual(res.body.message, 'Item added to list');
            assert.strictEqual(res.body.list.listOfItems.length, 1);
            assert.strictEqual(res.body.list.listOfItems[0].note, 'Test note');

            console.log('Tested adding an item to a list.');
        });

        it('should return 404 if the list does not exist', async () => {
            const unknownListId = new mongoose.Types.ObjectId();
            const payload = { itemId: testItemId };

            const res = await request(app).post(`/api/list/${unknownListId}/add-item`).send(payload);

            assert.strictEqual(res.status, 404);
            assert.strictEqual(res.body.message, 'List not found');

            console.log('Tested adding an item to a non-existent list.');
        });
    });

    describe('PATCH /:listId/update-item/:itemId', () => {
        let testListId;

        beforeEach(async () => {
            const list = await List.create({
                name: 'Updatable List',
                description: 'Test list',
                userID: testUserId,
                company: 'Test Company',
                date: new Date(),
                listOfItems: [
                    { itemId: testItemId, bought: false, quantity: '1' },
                ],
            });
            testListId = list._id;
        });

        it('should update an item in a list', async () => {
            const payload = { bought: true, quantity: '3' };

            const res = await request(app)
                .patch(`/api/list/${testListId}/update-item/${testItemId}`)
                .send(payload);

            assert.strictEqual(res.status, 200);
            assert.strictEqual(res.body.message, 'Item updated successfully');
            assert.strictEqual(res.body.list.listOfItems[0].bought, true);
            assert.strictEqual(res.body.list.listOfItems[0].quantity, '3');

            console.log('Tested updating an item in a list.');
        });

        it('should return 404 if the item does not exist in the list', async () => {
            const unknownItemId = new mongoose.Types.ObjectId();

            const res = await request(app)
                .patch(`/api/list/${testListId}/update-item/${unknownItemId}`)
                .send({ bought: true });

            assert.strictEqual(res.status, 404);
            assert.strictEqual(res.body.message, 'List or item not found');

            console.log('Tested updating a non-existent item in a list.');
        });
    });

    describe('DELETE /:listId', () => {
        let testListId;

        beforeEach(async () => {
            const list = await List.create({
                name: 'Deletable List',
                description: 'Test list',
                userID: testUserId,
                company: 'Test Company',
                date: new Date(),
                listOfItems: [],
            });
            testListId = list._id;
        });

        it('should delete a list', async () => {
            const res = await request(app).delete(`/api/list/${testListId}`);

            assert.strictEqual(res.status, 200);
            assert.strictEqual(res.body.message, 'List deleted successfully');

            console.log('Tested deleting a list.');
        });

        it('should return 404 if the list does not exist', async () => {
            const unknownListId = new mongoose.Types.ObjectId();

            const res = await request(app).delete(`/api/list/${unknownListId}`);

            assert.strictEqual(res.status, 404);
            assert.strictEqual(res.body.message, 'List not found');

            console.log('Tested deleting a non-existent list.');
        });
    });
});
