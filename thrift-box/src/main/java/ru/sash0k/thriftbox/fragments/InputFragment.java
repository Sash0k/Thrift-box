package ru.sash0k.thriftbox.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
                        valueTV.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        categories = (Categories) context.findViewById(R.id.categories);
        if (state != null) {
            valueTV.setText(state.getString(DB.VALUE));
            categories.setSelected(state.getInt(DB.CATEGORY));
        }
        Button enter = (Button) context.findViewById(R.id.enter_button);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (valueTV != null) {
                    final String textValue = valueTV.getText().toString();
                    final int value = Utils.parseCurrency(textValue);
                    if (value > 0) {
                        DB.insertItem(context, value, categories.getSelected());
                        Toast.makeText(context, getString(R.string.enter_value_done) + " " + textValue + getString(R.string.ruble), Toast.LENGTH_SHORT).show();
                        valueTV.setText("");
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
        state.putInt(DB.CATEGORY, categories.getSelected());
    }
    // ============================================================================

}
