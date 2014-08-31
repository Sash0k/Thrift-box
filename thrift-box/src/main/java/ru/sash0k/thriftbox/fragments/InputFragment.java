package ru.sash0k.thriftbox.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ru.sash0k.thriftbox.MainActivity;
import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.Utils;
import ru.sash0k.thriftbox.categories.Categories;
import ru.sash0k.thriftbox.database.DB;

/**
 * Ввод сумм
 * Created by saash0k on 28.04.14.
 */
public class InputFragment extends Fragment {
    private static final String TAG = "InputFragment";

    private TextView valueTV;
    private Categories categories;
    private String comment;

    public static InputFragment newInstance() {
        return new InputFragment();
    }
    // ============================================================================

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        return inflater.inflate(R.layout.fragment_input, container, false);
    }
    // ============================================================================

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        final Activity context = getActivity();
        valueTV = (TextView) context.findViewById(R.id.enter_value);
        valueTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (valueTV.getRight() - valueTV.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        cleanValues();
                        return true;
                    }
                }
                return false;
            }
        });
        valueTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view instanceof TextView && ((TextView) view).length() != 0) {
                    CommentDialog dialog = CommentDialog.newInstance(comment);
                    dialog.setTargetFragment(InputFragment.this, CommentDialog.CODE);
                    dialog.show(getFragmentManager(), CommentDialog.TAG);
                }
            }
        });

        categories = (Categories) context.findViewById(R.id.categories);
        if (state != null) {
            valueTV.setText(state.getString(DB.VALUE));
            categories.setSelected(state.getInt(DB.CATEGORY));
            this.comment = state.getString(DB.COMMENT);
        }
        Button enter = (Button) context.findViewById(R.id.enter_button);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (valueTV != null) {
                    final String textValue = valueTV.getText().toString();
                    final int value = Utils.parseCurrency(textValue);
                    if (value > 0) {
                        DB.insertItem(context, value, categories.getSelected(), comment);
                        Toast.makeText(context, ((MainActivity)context).parseRouble(getString(R.string.enter_value_done) + " " + textValue + Utils.ROUBLE), Toast.LENGTH_SHORT).show();
                        cleanValues();
                        Utils.updateWidgets(context);
                    } else
                        Toast.makeText(context, getString(R.string.enter_value_invalid), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    // ============================================================================

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString(DB.VALUE, valueTV.getText().toString());
        state.putString(DB.COMMENT, comment);
        state.putInt(DB.CATEGORY, categories.getSelected());
    }
    // ============================================================================


    /**
     * Получение текста комментария из диалога
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CommentDialog.CODE) {
            if (resultCode == DialogInterface.BUTTON_POSITIVE) {
                this.comment = data.getStringExtra(CommentDialog.TAG);
            }
        }
    }
    // ============================================================================


    /**
     * Очистить значение поля ввода и комментария
     */
    public void cleanValues() {
        valueTV.setText("");
        this.comment = null;
    }
    // ============================================================================

}
