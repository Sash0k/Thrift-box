package ru.sash0k.thriftbox

import mobin.expandablerecyclerview.adapters.ExpandableRecyclerViewAdapter
import ru.sash0k.thriftbox.database.DB

data class ExpensesDetail(val id: Int, val category: Int, val date: String, val value: Long, val comment: String?)

data class ExpensesGroup(val date: String, val value: Long) : ExpandableRecyclerViewAdapter.ExpandableGroup<ExpensesDetail>() {

    override fun getExpandingItems(): List<ExpensesDetail> = mutableListOf<ExpensesDetail>().apply {
        val cursor = DB.readableDatabase.rawQuery("SELECT * FROM ${DB.EXPENSES_VIEW} WHERE ${DB.DATE} =? ORDER BY ${DB.TIMESTAMP} DESC", arrayOf(date))
        if (cursor != null) {
            while (cursor.moveToNext()) {
                this.add(ExpensesDetail(
                        id = cursor.getInt(cursor.getColumnIndex(DB.ID)),
                        category = cursor.getInt(cursor.getColumnIndex(DB.CATEGORY)),
                        date = cursor.getString(cursor.getColumnIndex(DB.DATE)),
                        value = cursor.getLong(cursor.getColumnIndex(DB.VALUE)),
                        comment = cursor.getString(cursor.getColumnIndex(DB.COMMENT))
                ))
            }
            cursor.close()
        }
    }
}