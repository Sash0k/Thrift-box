package ru.sash0k.thriftbox.fragments;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget_fixed.CursorTreeAdapter;

import ru.sash0k.thriftbox.AdapterExpenses;
import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.Utils;
import ru.sash0k.thriftbox.database.DB;

/**
 * Список расходов
 * Created by sash0k on 28.04.14.
 */
public class ExpensesFragment extends ExpandableListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "ExpensesFragment";
    private static final int LOADER_ID = -1;

    // Адаптер списка
    protected CursorTreeAdapter mAdapter;

    public static ExpensesFragment newInstance() {
        ExpensesFragment f = new ExpensesFragment();
        //Bundle arguments = new Bundle();
        //arguments.putBoolean(TAG, widgetMode);
        //f.setArguments(arguments);
        return f;
    }
    // ============================================================================

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new AdapterExpenses(getActivity());
        setListAdapter(mAdapter);
    }
    // ============================================================================

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        setEmptyText(getString(R.string.list_empty));
        setHasOptionsMenu(false);
        setListShown(false);

        // styling listView
        final ExpandableListView list = getListView();
        final Drawable divider = getResources().getDrawable(R.drawable.list_divider);
        list.setSelector(R.drawable.list_selector_holo_light);
        list.setDivider(divider);
        list.setChildDivider(divider);
        list.setDividerHeight(getPx(1));
        list.setOnChildClickListener(this);

        LoaderManager lm = getLoaderManager();
        if (lm != null) lm.initLoader(LOADER_ID, null, this);
    }
    // ============================================================================

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        super.onChildClick(parent, v, groupPosition, childPosition, id);
        Cursor cursor = ((AdapterExpenses) getListAdapter()).getChild(groupPosition, childPosition);
        final int row_id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
        final String date = cursor.getString(cursor.getColumnIndex(DB.DATE));
        final int category = cursor.getInt(cursor.getColumnIndex(DB.CATEGORY));
        final long value = cursor.getLong(cursor.getColumnIndex(DB.VALUE));
        //cursor.close(); TODO?
        Utils.log("onChildClick id = " + row_id);

        DeleteConfirmDialog dialog = DeleteConfirmDialog.newInstance(row_id, date, category, value);
        dialog.show(getFragmentManager(), DeleteConfirmDialog.TAG);
        return true;
    }
    // ============================================================================

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            final String[] columns = {BaseColumns._ID, "SUM(" + DB.VALUE + ") AS " + DB.VALUE, DB.DATE};
            final String selection = DB.DATE + " = " + DB.DATE + " GROUP BY (" + DB.DATE + ")"; // hack: GROUP BY
            return new CursorLoader(getActivity(), DB.getUri(DB.EXPENSES_VIEW), columns, selection, null, DB.TIMESTAMP + " DESC");
    }
    // ============================================================================

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
        mAdapter.changeCursor(data);
    }
    // ============================================================================

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }
    // ============================================================================

    private int getPx(int value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) ((float) value * scale + 0.5f);
    }
    // ============================================================================
}