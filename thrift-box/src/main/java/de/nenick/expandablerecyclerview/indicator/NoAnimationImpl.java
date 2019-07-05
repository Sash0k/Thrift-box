package de.nenick.expandablerecyclerview.indicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import ru.sash0k.thriftbox.R;

class NoAnimationImpl extends ExpandableItemIndicator.Impl {
    private ImageView mImageView;

    @Override
    public void onInit(Context context, AttributeSet attrs, int defStyleAttr, ExpandableItemIndicator thiz) {
        View v = LayoutInflater.from(context).inflate(R.layout.erv__widget_expandable_item_indicator, thiz, true);
        mImageView = (ImageView) v.findViewById(R.id.image_view);
    }

    @Override
    public void setExpandedState(boolean isExpanded, boolean animate) {
        int resId = (isExpanded) ? R.mipmap.erv__ic_action_navigation_collapse : R.mipmap.erv__ic_action_navigation_expand;
        mImageView.setImageResource(resId);
    }
}
