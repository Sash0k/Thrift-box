package ru.sash0k.thriftbox

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.group_expense.view.*
import kotlinx.android.synthetic.main.group_expense.view.expense_value
import kotlinx.android.synthetic.main.item_expense.view.*
import mobin.expandablerecyclerview.adapters.ExpandableRecyclerViewAdapter

class ExpensesAdapter(groups: List<ExpensesGroup>, val listener: ChildClickListener) :
        ExpandableRecyclerViewAdapter<ExpensesDetail, ExpensesGroup, ExpensesAdapter.GroupViewHolder, ExpensesAdapter.DetailViewHolder>(
                groups as ArrayList<ExpensesGroup>, ExpandingDirection.VERTICAL) {

    private val categories = App.instance.resources.getStringArray(R.array.categories)

    override fun onCreateParentViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.group_expense, parent, false))
    }

    override fun onCreateChildViewHolder(child: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(LayoutInflater.from(child.context).inflate(R.layout.item_expense, child, false))
    }

    override fun onBindParentViewHolder(parentViewHolder: GroupViewHolder, expandableType: ExpensesGroup, position: Int) {
        parentViewHolder.itemView.expense_indicator.setExpandedState(expandableType.isExpanded, false)
        parentViewHolder.itemView.expense_date.text = expandableType.date
        parentViewHolder.itemView.expense_value.text = "${Utils.formatValue(expandableType.value)}${Utils.ROUBLE}"
    }

    override fun onBindChildViewHolder(childViewHolder: DetailViewHolder, expandedType: ExpensesDetail, expandableType: ExpensesGroup, position: Int) {
        childViewHolder.itemView.expense_category.text =  categories[expandedType.category]
        childViewHolder.itemView.expense_value.text = "${Utils.formatValue(expandedType.value)}${Utils.ROUBLE}"
        childViewHolder.itemView.expense_comment.text = expandedType.comment
    }

    override fun onExpandableClick(expandableViewHolder: GroupViewHolder, expandableType: ExpensesGroup) {
        expandableViewHolder.itemView.expense_indicator.setExpandedState(expandableType.isExpanded, true)
    }

    override fun onExpandedClick(expandableViewHolder: GroupViewHolder, expandedViewHolder: DetailViewHolder, expandedType: ExpensesDetail, expandableType: ExpensesGroup) {
        listener.onChildClick(expandedType)
    }
    
    class GroupViewHolder(v: View) : ExpandableRecyclerViewAdapter.ExpandableViewHolder(v)

    class DetailViewHolder(v: View) : ExpandableRecyclerViewAdapter.ExpandedViewHolder(v)

    interface ChildClickListener { fun onChildClick(item: ExpensesDetail) }
}