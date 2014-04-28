package ru.sash0k.thriftbox.categories;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import ru.sash0k.thriftbox.R;
import ru.sash0k.thriftbox.Utils;


/**
 * Класс, реализующий список категорий
 *
 * @author sash0k
 */
public class Categories extends LinearLayout {

    /**
     * Настройки класса
     */
    // ============================================================================
    private static final int[] mImages = {R.drawable.star,
            R.drawable.trolley, R.drawable.filling, R.drawable.housing, R.drawable.restaurants,
            R.drawable.transport, R.drawable.culture, R.drawable.air, R.drawable.medicine};

    // Параметры изображения
    final static int pic_width = 48;
    final static int pic_height = 48;
    final static int padding = 4;
    final static int selection_color = Color.rgb(0xFB, 0xD2, 0xB5);

    private int getPx(int value) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) ((float) value * scale + 0.5f);
    }
    // ============================================================================

    private int selected = -1;

    public int getSelected() {
        return selected;
    }

    public void setSelected(int position) {
        if (position != selected) {
            Utils.log("Categories: select " + position);
            unselect(selected);
            select(position);
            selected = position;
        }
    }

    private final Context mContext;
    private final String[] categories;

    /**
     * Конструкторы класса
     *
     * @param context
     */
    // ============================================================================
    public Categories(Context context) {
        super(context);
        mContext = context;
        categories = context.getResources().getStringArray(R.array.categories);
        build();
    }

    public Categories(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        categories = context.getResources().getStringArray(R.array.categories);
        build();
    }

    public Categories(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        categories = context.getResources().getStringArray(R.array.categories);
        build();
    }
    // ============================================================================

    /**
     * Метод загрузки новой картинки
     */
    // ============================================================================
    private void build() {
        for (int i = 0; i < mImages.length; i++)
            addView(getImageView(i));
        setSelected(0);
    }

    // ============================================================================

    /**
     * Получение изображения по ссылке
     *
     * @param position - номер изображения в массиве
     * @return - ImageView
     */
    // ============================================================================
    ImageView getImageView(int position) {
        final ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new LayoutParams(getPx(pic_width), getPx(pic_height)));
        //imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        final int px = getPx(padding);
        imageView.setPadding(px, px, px, px);
        imageView.setTag(position);
        imageView.setImageResource(mImages[position]);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //v.setBackgroundColor(selection_color);
                setSelected((Integer) view.getTag());
            }
        });

        imageView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(mContext, categories[(Integer)view.getTag()], Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        return imageView;
    }
    // ============================================================================

    private void unselect(int position) {
        View v = findViewWithTag(position);
        if (v != null) v.setBackgroundColor(Color.TRANSPARENT);
    }
    // ============================================================================

    private void select(int position) {
        View v = findViewWithTag(position);
        if (v != null) v.setBackgroundColor(selection_color);
    }
    // ============================================================================

}
