package ru.sash0k.thriftbox;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView valueTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        valueTV = (TextView) findViewById(R.id.enter_value);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Обработка ввода виртуальной клавиатуры
     */
    public void virtualKeyboardClick(View view) {
        final String digit = view.getTag().toString();
        if (valueTV != null) {
            String query = valueTV.getText().toString();
            if (getString(R.string.backspace).equals(digit)) {
                final int len = query.length() - 1;
                query = query.substring(0, (len < 0) ? 0 : len);
            } else query += digit;
            valueTV.setText(query);
        }
    }
    // ============================================================================

    /**
     * Внесение суммы
     *
     * @param view
     */
    public void enterClick(View view) {
        if (valueTV != null) {
            final String sum = valueTV.getText().toString();
            valueTV.setText("");
            // TODO: проверка валидности и запись в БД
        }
    }
    // ============================================================================
}
