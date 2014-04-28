package ru.sash0k.thriftbox.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import ru.sash0k.thriftbox.AdapterExpenses;
import ru.sash0k.thriftbox.database.DB;

/**
 * Список расходов
 * Created by sash0k on 28.04.14.
 */
public class ExpensesFragment extends BaseListFragment {
    private static final String TAG = "ExpensesFragment";

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
        return new CursorLoader(getActivity(), DB.getUri(DB.EXPENSES_TABLE), null, null, null, DB.TIMESTAMP + " DESC");
    }
    // ============================================================================

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);

        LoaderManager lm = getLoaderManager();
        if (lm != null) lm.initLoader(LOADER_ID, null, this);
    }
    // ============================================================================
}
