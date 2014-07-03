package ru.sash0k.thriftbox.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

import ru.sash0k.thriftbox.AdapterExpenses;
import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.database.DB;

/**
 * Список расходов
 * Created by sash0k on 28.04.14.
 */
public class ExpensesFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final String TAG = "ExpensesFragment";
    private static final int LOADER_ID = 1;

    // Адаптер списка
    protected CursorAdapter mAdapter;


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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] columns = {BaseColumns._ID, "SUM(" + DB.VALUE + ") AS " + DB.VALUE, DB.DATE};
        final String selection = DB.DATE + " = " + DB.DATE + " GROUP BY (" + DB.DATE + ")"; // hack: GROUP BY
        return new CursorLoader(getActivity(), DB.getUri(DB.EXPENSES_VIEW), columns, selection, null, DB.TIMESTAMP + " DESC");
    }
    // ============================================================================

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);

        if (isResumed()) {
            setListShown(true);
        } else {
            setListShownNoAnimation(true);
        }
    }
    // ============================================================================

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
    // ============================================================================


    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        setEmptyText(getString(R.string.list_empty));
        setHasOptionsMenu(false);
        setListShown(false);

        LoaderManager lm = getLoaderManager();
        if (lm != null) lm.initLoader(LOADER_ID, null, this);
    }
    // ============================================================================
}
