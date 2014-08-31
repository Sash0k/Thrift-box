package ru.sash0k.thriftbox;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import com.viewpagerindicator.PageIndicator;

import ru.sash0k.thriftbox.fragments.ExpensesFragment;
import ru.sash0k.thriftbox.fragments.InputFragment;
import ru.sash0k.thriftbox.fragments.SettingsFragment;

public class MainActivity extends FragmentActivity {
    private static Typeface roubleSupportedTypeface;

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        roubleSupportedTypeface = Typeface.createFromAsset(getAssets(), Utils.ROUBLE_FONT);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        PageIndicator indicator = (PageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
    }
    // ============================================================================

    /**
     * Обработка ввода виртуальной клавиатуры
     */
    public void virtualKeyboardClick(View view) {
        TextView valueTV = (TextView)mViewPager.findViewById(R.id.enter_value);
        final String digit = view.getTag().toString();
        if (valueTV != null) {
            String query = valueTV.getText().toString();
            if (getString(R.string.backspace).equals(digit)) {
                final int len = query.length() - 1;
                query = query.substring(0, (len < 0) ? 0 : len);
                // TODO: чистить комментарий тут
            } else query += digit;
            valueTV.setText(query);
        }
    }
    // ============================================================================

    /**
     * Отображение символа рубля из кастомного шрифта
     */
    public SpannableStringBuilder parseRouble(CharSequence value) {
        if (roubleSupportedTypeface == null) return null;
        else {
            SpannableStringBuilder resultSpan = new SpannableStringBuilder(value);
            for (int i = 0; i < resultSpan.length(); i++) {
                if (resultSpan.charAt(i) == Utils.ROUBLE) {
                    TypefaceSpan2 roubleTypefaceSpan = new TypefaceSpan2(roubleSupportedTypeface);
                    resultSpan.setSpan(roubleTypefaceSpan, i, i + 1, 0);
                }
            }
            return resultSpan;
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
