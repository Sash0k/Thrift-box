package ru.sash0k.thriftbox;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;

import de.nenick.expandablerecyclerview.ExpandableCursorTreeAdapter;
import de.nenick.expandablerecyclerview.indicator.ExpandableItemIndicator;

import ru.sash0k.thriftbox.database.DB;

public class ExpensesAdapter extends ExpandableCursorTreeAdapter<ExpensesAdapter.GroupHolder, ExpensesAdapter.DetailsHolder> {
    private final Context mContext;
    private final String[] categories;

    public ExpensesAdapter(Context context) {
        super(context);
        this.mContext = context;
        this.categories = context.getResources().getStringArray(R.array.categories);
        //this.mQueryHandler = new QueryHandler(context, this);
    }

    //private final QueryHandler mQueryHandler;


    static class GroupHolder extends ExpandableCursorTreeAdapter.ListItemHolder<Cursor> {
        private final ExpandableItemIndicator indicator;
        private final TextView date;
        private final TextView value;

        GroupHolder(View v) {
            super(v);
            indicator = v.findViewById(R.id.expense_indicator);
            date = v.findViewById(R.id.expense_date);
            value = v.findViewById(R.id.expense_value);
        }

        @Override
        public void onBind(Cursor content) {
            syncExpansionIndicator();
            itemView.setClickable(true);

            String date = content.getString(content.getColumnIndex(DB.DATE));
            long value = content.getLong(content.getColumnIndex(DB.VALUE));

            this.date.setText(date);
            this.value.setText(Utils.formatValue(value) + Utils.ROUBLE);
        }

        private void syncExpansionIndicator() {
            final int expandState = getExpandStateFlags();
            if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
                boolean isExpanded = (expandState & ExpandableItemConstants.STATE_FLAG_IS_EXPANDED) != 0;
                boolean animateIndicator = ((expandState & ExpandableItemConstants.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) != 0);
                indicator.setExpandedState(isExpanded, animateIndicator);
            }
        }
    }

    static class DetailsHolder extends ExpandableCursorTreeAdapter.ListItemHolder<Cursor> {
        private final String[] categories;
        private final TextView category;
        private final TextView comment;
        private final TextView value;

        DetailsHolder(View v, String[] categories) {
            super(v);
            this.categories = categories;
            category = v.findViewById(R.id.expense_category);
            comment = v.findViewById(R.id.expense_comment);
            value = v.findViewById(R.id.expense_value);
        }

        @Override
        public void onBind(Cursor content) {
            String category = categories[content.getInt(content.getColumnIndex(DB.CATEGORY))];
            String comment = content.getString(content.getColumnIndex(DB.COMMENT));
            long value = content.getLong(content.getColumnIndex(DB.VALUE));

            this.category.setText(category);
            this.comment.setText(comment);
            this.value.setText(Utils.formatValue(value) + Utils.ROUBLE);
        }
    }

    static class DataLoader extends AsyncTaskLoader<Cursor> {
        private final String table;
        private final String[] args;
        private final String selection;
        private final String order;

        public DataLoader(Context context, String table,String selection,  String[] args, String order) {
            super(context);
            this.table = table;
            this.selection = selection;
            this.args = args;
            this.order = order;
        }

        @Override
        public Cursor loadInBackground() {
            return DB.INSTANCE.getReadableDatabase().query(table, null, selection, args, null, null, order);
        }
    }

    @Override
    protected Cursor getChildrenCursor(Cursor groupCursor) {
        final String date = groupCursor.getString(groupCursor.getColumnIndex(DB.DATE));

        return new DataLoader(mContext, DB.EXPENSES_VIEW, DB.DATE + "=?", new String[]{date}, DB.TIMESTAMP + " DESC").loadInBackground();
    }

    @Override
    public GroupHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_expense, parent, false);
        return new GroupHolder(view);
    }

    @Override
    public DetailsHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new DetailsHolder(view, categories);
    }

//    private static final class QueryHandler extends AsyncQueryHandler {
//        private ExpandableCursorTreeAdapter mAdapter;
//
//        QueryHandler(Context context, ExpandableCursorTreeAdapter adapter) {
//            super(context.getContentResolver());
//            this.mAdapter = adapter;
//        }
//
//        @Override
//        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//            if (token == 1) {
//                if (cursor != null && cursor.moveToFirst()) {
//                    Utils.log("setChildrenCursor count:" + cursor.getCount());
//                    int groupPosition = (Integer) cookie;
//                    mAdapter.setChildrenCursor(groupPosition, cursor);
//                }
//            } else {
//                if (cursor != null && cursor.moveToFirst()) {
//                    Utils.log("setGroupCursor count:" + cursor.getCount());
//                    mAdapter.setGroupCursor(cursor);
//                }
//            }
//        }
//    }
}
