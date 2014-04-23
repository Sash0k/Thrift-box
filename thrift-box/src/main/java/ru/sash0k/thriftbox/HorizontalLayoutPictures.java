package ru.sash0k.thriftbox;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;


/**
 * Класс, загружающий массив ссылок на картинки в строку
 *
 * @author sash0k
 */
public class HorizontalLayoutPictures extends LinearLayout {

    /**
     * Настройки класса
     */
    // ============================================================================
    // Параметры изображения
    final int pic_width = 64;
    final int pic_height = 64;
    final int padding = 5;
    // ============================================================================

    // Подгрузка ресурсов в массив для таблицы
    private static final int[] mImages = {
            R.drawable.trolley, R.drawable.filling, R.drawable.housing, R.drawable.restaurants,
            R.drawable.transport, R.drawable.culture, R.drawable.air, R.drawable.medicine, R.drawable.star};


    private final Context mContext;


    /**
     * Конструкторы класса
     *
     * @param context
     */
    // ============================================================================
    public HorizontalLayoutPictures(Context context) {
        super(context);
        mContext = context;
        build();
    }

    public HorizontalLayoutPictures(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        build();
    }

    public HorizontalLayoutPictures(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        build();
    }
    // ============================================================================

    /**
     * Метод загрузки новой картинки
     */
    // ============================================================================
    public void build() {
        for (int i = 0; i < mImages.length; i++)
            addView(getImageView(i));
    }

    // ============================================================================

    /**
     * Получение изображения по ссылке
     *
     * @param i - номер изображения в массиве
     * @return - ImageView
     */
    // ============================================================================
    ImageView getImageView(final int i) {
        final ImageView imageView = new ImageView(mContext);
        imageView.setLayoutParams(new LayoutParams(pic_width, pic_height));
        //imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setPadding(padding, padding, padding, padding);
        imageView.setImageResource(mImages[i]);

        OnClickListener listener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                Utils.log("selected category: " + i);
                imageView.setBackgroundResource(android.R.drawable.list_selector_background);
            }
        };
        imageView.setOnClickListener(listener);
        return imageView;
    }
    // ============================================================================
}
