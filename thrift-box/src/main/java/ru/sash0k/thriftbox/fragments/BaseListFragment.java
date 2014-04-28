package ru.sash0k.thriftbox.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;

import ru.sash0k.thriftbox.R;


/**
 * Общий предок для фрагментов-списков
 * Created by sash0k on 02.04.14.
 */
public abstract class BaseListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    protected static final int LOADER_ID = 1;

    // Адаптер списка
    protected CursorAdapter mAdapter;
    // ============================================================================

    @Override
    public void onActivityCreated(Bundle savedState) {
        super.onActivityCreated(savedState);
        setEmptyText(getString(R.string.list_empty));
        setHasOptionsMenu(true);
        setListShown(false);
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
}
