const express = require('express');
const mongoose = require('mongoose');
const List = require('../models/List');

const router = express.Router();

router.post('/create-empty-list', async (req, res) => {
    try {
        const { userID, company, name, description, date } = req.body;

        if (!date) {
            return res.status(400).json({ message: 'Date is required' });
        }

        const newList = new List({
            name: name || 'My List',
            description: description || 'Default Description',
            userID,
            company,
            date,
            listOfItems: []
        });

        await newList.save();
        res.status(201).json({ message: 'List created successfully', list: newList });
    } catch (error) {
        res.status(500).json({ message: 'Error creating list', error });
    }
});

router.get('/usersLists/:userID', async (req, res) => {
    try {
        const { userID } = req.params;
        const { filterBy, raw } = req.query;

        let filter;

        console.log("users list called");

        if (mongoose.isValidObjectId(userID)) {
            filter = { userID: new mongoose.Types.ObjectId(userID) };
        } else {
            filter = { userID };
        }

        if (filterBy === 'bought') {
            filter['listOfItems.bought'] = true;
        } else if (filterBy) {
            filter.company = filterBy;
        }

        const userLists = await List.find(filter)
            .sort(filterBy === 'created' ? { date: -1 } : {})
            .populate('listOfItems.itemId') 
            .exec();

        if (!userLists.length) {
            return res.status(404).json({ message: 'No lists found for this user' });
        }

        if (raw === 'true') {
            return res.status(200).json(userLists);
        }

        res.status(200).json({ message: 'User lists retrieved successfully', lists: userLists });
    } catch (error) {
        console.error('Error retrieving user lists:', error);
        res.status(500).json({ message: 'Error retrieving user lists', error });
    }
});


router.post('/:listId/add-item', async (req, res) => {
    try {
        const { listId } = req.params;
        const { itemId, bought = false, note = null, quantity = '1' } = req.body;

        const updatedList = await List.findByIdAndUpdate(
            listId,
            { $push: { listOfItems: { itemId, bought, note, quantity } } },
            { new: true }
        );

        if (!updatedList) {
            return res.status(404).json({ message: 'List not found' });
        }

        res.status(200).json({ message: 'Item added to list', list: updatedList });
    } catch (error) {
        res.status(500).json({ message: 'Error adding item to list', error });
    }
});

router.patch('/:listId/update-item/:itemId', async (req, res) => {
    try {
        const { listId, itemId } = req.params;
        const { bought, quantity } = req.body;

        const updateFields = {};
        if (bought !== undefined) updateFields['listOfItems.$.bought'] = bought;
        if (quantity) updateFields['listOfItems.$.quantity'] = quantity;

        const updatedList = await List.findOneAndUpdate(
            { _id: listId, 'listOfItems.itemId': itemId },
            { $set: updateFields },
            { new: true }
        );

        if (!updatedList) {
            return res.status(404).json({ message: 'List or item not found' });
        }

        res.status(200).json({ message: 'Item updated successfully', list: updatedList });
    } catch (error) {
        res.status(500).json({ message: 'Error updating item', error });
    }
});

router.delete('/:listId', async (req, res) => {
    try {
        const { listId } = req.params;

        const deletedList = await List.findByIdAndDelete(listId);

        if (!deletedList) {
            return res.status(404).json({ message: 'List not found' });
        }

        res.status(200).json({ message: 'List deleted successfully', list: deletedList });
    } catch (error) {
        res.status(500).json({ message: 'Error deleting list', error });
    }
});

module.exports = router;
