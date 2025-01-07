package com.prvavaja.grocerease.lists

import com.prvavaja.grocerease.model.GroceryList
class ListOfGroceryLists {

    private val lists = mutableListOf<GroceryList>()
    fun addList(list: GroceryList) {
        lists.add(list)
    }

    fun getList(id: String): GroceryList? {
        return lists.find { it.id == id }
    }

    fun updateList(updatedList: GroceryList) {
        val index = lists.indexOfFirst { it.id == updatedList.id }
        if (index != -1) {
            lists[index] = updatedList
        }
    }
    fun removeList(list: GroceryList) {//remove
        lists.remove(list)
    }
    fun getAllLists(): MutableList<GroceryList> {//get all
        for (list in lists) {
            //println(list.toString())
        }
        return lists
    }
    fun size(): Int {
        return lists.size
    }
    fun getLastList(): GroceryList {
        return lists.last()
    }
}