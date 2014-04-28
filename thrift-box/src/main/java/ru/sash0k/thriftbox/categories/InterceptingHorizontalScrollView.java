package ru.sash0k.thriftbox.categories;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/**
 * Created by sash0k on 05.12.13.
 * Доработка для горизонтального скролла внутри ViewPager
 * https://gist.github.com/brandondenney/b8ddd655664eb295129d
 */
public class InterceptingHorizontalScrollView extends HorizontalScrollView {

    public InterceptingHorizontalScrollView(Context context) {
        super(context);
    }

    public InterceptingHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptingHorizontalScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (getParent() != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }
}
