package com.rodolfonavalon.shaperipplelibrary.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Image<T> extends BaseShape {

    private Bitmap bitmap;
    private Rect rect;

    private final T imageSource;

    public Image(T imageSource) {
        this.imageSource = imageSource;
    }

    @Override
    public void onSetup(Context context, Paint shapePaint) {
        this.rect = new Rect();
        if (imageSource instanceof Integer) {
            this.bitmap = BitmapFactory.decodeResource(context.getResources(), (Integer) imageSource);
        } else if (imageSource instanceof String) {
//            if (!TextUtils.isEmpty((String) imageSource)) {
//                try {
//                    this.bitmap = Glide.with(context)
//                            .asBitmap()
//                            .load((String) imageSource)
//                            .submit().get();
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                }

            new Exception("Need to implement load from url");
        } else if (imageSource instanceof Bitmap) {
            bitmap = (Bitmap) imageSource;
        }
    }

    @Override
    public void onDraw(Canvas canvas, int x, int y, float radiusSize, int color, int rippleIndex, Paint shapePaint) {
        int currentImageSize = (int) radiusSize;

        // Get the current alpha channel of the color
        int currentAlpha = 0xFF & (color >> 24);
        shapePaint.setAlpha(currentAlpha);

        this.rect.set(x - currentImageSize, y - currentImageSize, x + (int) radiusSize, y + (int) radiusSize);
        canvas.drawBitmap(bitmap, null, this.rect, shapePaint);
    }
}
