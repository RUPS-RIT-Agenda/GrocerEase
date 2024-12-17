package com.prvavaja.grocerease.lists

import com.prvavaja.grocerease.model.GroceryList
import java.util.UUID
class ListOfGroceryLists {

    private val lists = mutableListOf<GroceryList>()
    fun addList(list: GroceryList) {//add
        lists.add(list)
    }

    fun getList(uuid: UUID): GroceryList? {//get a specific list from grocery lists
        return lists.find { it.uuid == uuid }
    }

    fun updateList(updatedList: GroceryList) {//update
        val index = lists.indexOfFirst { it.uuid == updatedList.uuid }
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