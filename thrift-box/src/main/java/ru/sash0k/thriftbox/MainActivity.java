package ru.sash0k.thriftbox;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.viewpagerindicator.PageIndicator;

import ru.sash0k.thriftbox.fragments.ExpensesFragment;
import ru.sash0k.thriftbox.fragments.InputFragment;
import ru.sash0k.thriftbox.fragments.SettingsFragment;

public class MainActivity extends ActivityHelper {

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisplayView = (ViewGroup) findViewById(R.id.activity_main);
        /*
        if (!Utils.hasLollipop()) {
            mDisplayView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Utils.removeOnGlobalLayoutListenerCompat(mDisplayView, this);
                    revealColorView = new RevealColorView(MainActivity.this);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mDisplayView.getHeight());
                    revealColorView.setLayoutParams(params);
                    revealColorView.setBackgroundResource(android.R.color.transparent);
                    mDisplayView.addView(revealColorView);
                }
            });
        }*/

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        PageIndicator indicator = (PageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
    }
    // ============================================================================

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.eq:
                //onEquals();
                break;
            //case R.id.del:
            //    onDelete();
            //    break;
            //case R.id.clr:
            //    onClear();
            //    break;
            default:
                keyboardClick(((Button) view).getText().toString());
                break;
        }
    }

    /**
     * Ввод суммы
     */
    private void keyboardClick(String digit) {
        TextView valueTV = (TextView) mViewPager.findViewById(R.id.enter_value);
        if (valueTV != null) {
            String query = valueTV.getText().toString();
            if (getString(R.string.backspace).equals(digit)) {
                final int len = query.length() - 1;
                query = query.substring(0, (len < 0) ? 0 : len);
                if (len == 0) {
                    TextView commentTV = (TextView) mViewPager.findViewById(R.id.comment_value);
                    commentTV.setText("");
                    commentTV.setVisibility(View.GONE);
                }
            } else query += digit;
            valueTV.setText(query);
        }
    }
    // ============================================================================

    /**
     * Обработка ввода виртуальной клавиатуры
     */
    public void virtualKeyboardClick(View view) {
        final String digit = view.getTag().toString();
        keyboardClick(digit);
    }
    // ============================================================================

    /**
     * Обработка ввода аппаратной клавиатуры
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent keyEvent) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                keyboardClick(getString(R.string.zero));
                return true;
            case KeyEvent.KEYCODE_1:
                keyboardClick(getString(R.string.one));
                return true;
            case KeyEvent.KEYCODE_2:
                keyboardClick(getString(R.string.two));
                return true;
            case KeyEvent.KEYCODE_3:
                keyboardClick(getString(R.string.three));
                return true;
            case KeyEvent.KEYCODE_4:
                keyboardClick(getString(R.string.four));
                return true;
            case KeyEvent.KEYCODE_5:
                keyboardClick(getString(R.string.five));
                return true;
            case KeyEvent.KEYCODE_6:
                keyboardClick(getString(R.string.six));
                return true;
            case KeyEvent.KEYCODE_7:
                keyboardClick(getString(R.string.seven));
                return true;
            case KeyEvent.KEYCODE_8:
                keyboardClick(getString(R.string.eight));
                return true;
            case KeyEvent.KEYCODE_9:
                keyboardClick(getString(R.string.nine));
                return true;
            case KeyEvent.KEYCODE_PERIOD:
                keyboardClick(getString(R.string.dot));
                return true;
            case KeyEvent.KEYCODE_DEL:
                keyboardClick(getString(R.string.backspace));
                return true;
            default:
                return super.onKeyUp(keyCode, keyEvent);
        }
    }
    // ============================================================================

    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return InputFragment.newInstance();
                case 1:
                    return ExpensesFragment.newInstance();
                default:
                    return SettingsFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}
