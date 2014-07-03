package ru.sash0k.thriftbox.fragments;

import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.SparseIntArray;
import android.widget.ExpandableListView;
import android.widget.ListView;

import ru.sash0k.thriftbox.AdapterExpenses;
import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.Utils;
import ru.sash0k.thriftbox.database.DB;

/**
 * Список расходов
 * Created by sash0k on 28.04.14.
 */
public class ExpensesFragment extends ExpandableListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "ExpensesFragment";
    private static final int LOADER_ID = -1;

    // Адаптер списка
    protected AdapterExpenses mAdapter;


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

        mAdapter = new AdapterExpenses(this, getActivity());
        setListAdapter(mAdapter);
        setRetainInstance(true);
    }
    // ============================================================================

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            final String[] columns = {BaseColumns._ID, "SUM(" + DB.VALUE + ") AS " + DB.VALUE, DB.DATE};
            final String selection = DB.DATE + " = " + DB.DATE + " GROUP BY (" + DB.DATE + ")"; // hack: GROUP BY
            return new CursorLoader(getActivity(), DB.getUri(DB.EXPENSES_VIEW), columns, selection, null, DB.TIMESTAMP + " DESC");
        } else {
            final String date = args.getString(DB.DATE);
            return new CursorLoader(getActivity(), DB.getUri(DB.EXPENSES_VIEW), null, DB.DATE + "=?", new String[]{date}, DB.TIMESTAMP + " DESC");
        }
    }
    // ============================================================================

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final int id = loader.getId();

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }

        if (id != LOADER_ID) {
            // child cursor
            if (!data.isClosed()) {
                Utils.log("data.getCount() " + data.getCount());
                SparseIntArray groupMap = mAdapter.getGroupMap();
                try {
                    int groupPos = groupMap.get(id);
                    Utils.log("onLoadFinished() for groupPos " + groupPos);
                    mAdapter.setChildrenCursor(groupPos, data);
                } catch (NullPointerException e) {
                    Utils.log("Adapter expired, try again on the next query: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else mAdapter.changeCursor(data);
    }
    // ============================================================================

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
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

        LoaderManager lm = getLoaderManager();
        if (lm != null) lm.initLoader(LOADER_ID, null, this);
    }
    // ============================================================================

    private int getPx(int value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) ((float) value * scale + 0.5f);
    }
    // ============================================================================
}
