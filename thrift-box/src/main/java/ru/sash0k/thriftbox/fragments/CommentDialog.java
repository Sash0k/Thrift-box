package ru.sash0k.thriftbox.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import ru.sash0k.thriftbox.R;

/**
 * Ввод комментария к расходу
 * Created by sash0k on 25.08.14.
 */
public class CommentDialog extends DialogFragment {
    public static final String TAG = "CommentDialog";
    public static final int CODE = 1;
    private EditText editComment;

    public static CommentDialog newInstance(CharSequence value) {
        CommentDialog f = new CommentDialog();
        Bundle arguments = new Bundle();
        arguments.putCharSequence(TAG, value);
        f.setArguments(arguments);
        return f;
    }
    // ============================================================================

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Context context = new ContextThemeWrapper(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        editComment = new EditText(context);
        editComment.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editComment.setSingleLine(true);
        editComment.setText(getArguments().getCharSequence(TAG));
        editComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                        (actionId == EditorInfo.IME_ACTION_DONE)) {
                    returnResult(DialogInterface.BUTTON_POSITIVE);
                    dismiss();
                }
                return false;
            }
        });
        editComment.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context)
                .setMessage(R.string.comment)
                .setView(editComment)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        returnResult(which);
                    }
                })
                .setNegativeButton(android.R.string.no, null);
        return builder.create();
    }
    // ============================================================================

    /**
     * Возврат комментария фрагменту
     */
    private void returnResult(int which) {
        if (editComment != null) {
            Intent result = new Intent();
            result.putExtra(TAG, editComment.getText().toString().trim());
            getTargetFragment().onActivityResult(getTargetRequestCode(), which, result);
        }
    }
    // ============================================================================
}
