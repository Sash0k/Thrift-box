package ru.sash0k.thriftbox;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorTreeAdapter;
import android.widget.TextView;

import ru.sash0k.thriftbox.database.DB;

/**
 * Адаптер списка расходов
 * Created by sash0k on 28.04.14.
 */
public class AdapterExpenses extends CursorTreeAdapter {
    private final LayoutInflater mInflater;
    private final String[] categories;

    private final Fragment mFragment;
    protected final SparseIntArray mGroupMap;

    public AdapterExpenses(Fragment fragment, Context context) {
        super(null, context, true);
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.categories = context.getResources().getStringArray(R.array.categories);

        this.mFragment = fragment;
        this.mGroupMap = new SparseIntArray();
    }
    // ============================================================================

    //Accessor method
    public SparseIntArray getGroupMap() {
        return mGroupMap;
    }

    @Override
    protected View newGroupView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.listitem_expense, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.date = (TextView) view.findViewById(R.id.expense_date);
        viewHolder.value = (TextView) view.findViewById(R.id.expense_value);
        viewHolder.date_col = cursor.getColumnIndexOrThrow(DB.DATE);
        viewHolder.value_col = cursor.getColumnIndexOrThrow(DB.VALUE);
        view.setTag(viewHolder);
        return view;
    }
    // ============================================================================

    @Override
    protected void bindGroupView(View view, Context context, Cursor cursor, boolean b) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.date.setText(cursor.getString(viewHolder.date_col));
        viewHolder.value.setText(Utils.formatValue(cursor.getLong(viewHolder.value_col)) + context.getString(R.string.ruble));
    }
    // ============================================================================

    @Override
    protected View newChildView(Context context, Cursor cursor, boolean b, ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.listitem_expense, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.category = (TextView) view.findViewById(R.id.expense_category);
        viewHolder.value = (TextView) view.findViewById(R.id.expense_value);

        viewHolder.category_col = cursor.getColumnIndexOrThrow(DB.CATEGORY);
        viewHolder.value_col = cursor.getColumnIndexOrThrow(DB.VALUE);
        view.setTag(viewHolder);
        return view;
    }
    // ============================================================================

    @Override
    protected void bindChildView(View view, Context context, Cursor cursor, boolean b) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.category.setText(categories[cursor.getInt(viewHolder.category_col)]);
        viewHolder.value.setText(Utils.formatValue(cursor.getLong(viewHolder.value_col)) + context.getString(R.string.ruble));
    }
    // ============================================================================

    @Override
    protected Cursor getChildrenCursor(Cursor cursor) {
        final int groupPos = cursor.getPosition();
        final int groupId = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
        final String date  = cursor.getString(cursor.getColumnIndex(DB.DATE));
        Utils.log("date = " + date);

        mGroupMap.put(groupId, groupPos);
        Bundle args = new Bundle();
        args.putString(DB.DATE, date);
        LoaderManager lm = mFragment.getLoaderManager();
        Loader loader = lm.getLoader(groupId);
        if (loader != null && !loader.isReset()) {
            lm.restartLoader(groupId, args, (LoaderManager.LoaderCallbacks<Object>) mFragment);
        } else {
            lm.initLoader(groupId, args, (LoaderManager.LoaderCallbacks<Object>) mFragment);
        }
        return null;
    }
    // ============================================================================

    private static class ViewHolder {
        private TextView category;
        private TextView date;
        private TextView value;
        private int date_col, category_col, value_col;
    }
    // ============================================================================
}
