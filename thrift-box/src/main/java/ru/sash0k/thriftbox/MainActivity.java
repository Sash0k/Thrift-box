package ru.sash0k.thriftbox;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroupOverlay;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.viewpagerindicator.PageIndicator;

import ru.sash0k.thriftbox.fragments.ExpensesFragment;
import ru.sash0k.thriftbox.fragments.InputFragment;
import ru.sash0k.thriftbox.fragments.SettingsFragment;
import ru.sash0k.thriftbox.numpad.AnimatorListenerWrapper;

public class MainActivity extends Activity {
    private static Typeface roubleSupportedTypeface;

    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

    protected LinearLayout mDisplayView;
    private Animator mCurrentAnimator;

    void cancelAnimation() {
        if (mCurrentAnimator != null) {
            mCurrentAnimator.end();
        }
    }

    public void reveal(View sourceView, int colorRes, final AnimatorListenerWrapper listener) {
        final ViewGroupOverlay groupOverlay =
                (ViewGroupOverlay) getWindow().getDecorView().getOverlay();

        final Rect displayRect = new Rect();
        mDisplayView.getGlobalVisibleRect(displayRect);

        // Make reveal cover the display and status bar.
        final View revealView = new View(this);
        revealView.setBottom(displayRect.bottom);
        revealView.setLeft(displayRect.left);
        revealView.setRight(displayRect.right);
        revealView.setBackgroundColor(getResources().getColor(colorRes));
        groupOverlay.add(revealView);

        final int[] clearLocation = new int[2];
        sourceView.getLocationInWindow(clearLocation);
        clearLocation[0] += sourceView.getWidth() / 2;
        clearLocation[1] += sourceView.getHeight() / 2;

        final int revealCenterX = clearLocation[0] - revealView.getLeft();
        final int revealCenterY = clearLocation[1] - revealView.getTop();

        final double x1_2 = Math.pow(revealView.getLeft() - revealCenterX, 2);
        final double x2_2 = Math.pow(revealView.getRight() - revealCenterX, 2);
        final double y_2 = Math.pow(revealView.getTop() - revealCenterY, 2);
        final float revealRadius = (float) Math.max(Math.sqrt(x1_2 + y_2), Math.sqrt(x2_2 + y_2));

        final Animator revealAnimator =
                ViewAnimationUtils.createCircularReveal(revealView,
                        revealCenterX, revealCenterY, 0.0f, revealRadius);
        revealAnimator.setDuration(
                getResources().getInteger(android.R.integer.config_longAnimTime));

        final Animator alphaAnimator = ObjectAnimator.ofFloat(revealView, View.ALPHA, 0.0f);
        alphaAnimator.setDuration(
                getResources().getInteger(android.R.integer.config_mediumAnimTime));
        alphaAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                listener.onAnimationStart();
            }
        });

        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(revealAnimator).before(alphaAnimator);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                groupOverlay.remove(revealView);
                mCurrentAnimator = null;
            }
        });

        mCurrentAnimator = animatorSet;
        animatorSet.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDisplayView = (LinearLayout) findViewById(R.id.activity_main);

        roubleSupportedTypeface = Typeface.createFromAsset(getAssets(), Utils.ROUBLE_FONT);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        PageIndicator indicator = (PageIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
    }
    // ============================================================================

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        // If there's an animation in progress, end it immediately to ensure the state is
        // up-to-date before the pending user interaction is handled.
        cancelAnimation();
    }

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
     * A {@link android.support.v13.app.FragmentPagerAdapter} that returns a fragment corresponding to
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
