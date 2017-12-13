package pub.tanzby.unieditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.view.View;

/**
 * Created by tan on 2017/12/11.
 */

public class UNIDragShadowBuilder extends View.DragShadowBuilder {
    private Drawable shadow;

    private UNIDragShadowBuilder() {
        super();
    }

    public static View.DragShadowBuilder fromResource(Context context, int drawableId) {
        UNIDragShadowBuilder builder = new UNIDragShadowBuilder();

        builder.shadow = context.getResources().getDrawable(drawableId);
        if (builder.shadow == null) {
            throw new NullPointerException("Drawable from id is null");
        }

        builder.shadow.setBounds(0, 0, builder.shadow.getMinimumWidth(), builder.shadow.getMinimumHeight());

        return builder;
    }

    public static View.DragShadowBuilder fromBitmap(Context context, Bitmap bmp) {
        if (bmp == null) {
            throw new IllegalArgumentException("Bitmap cannot be null");
        }

        UNIDragShadowBuilder builder = new UNIDragShadowBuilder();

        builder.shadow = new BitmapDrawable(context.getResources(), bmp);
        builder.shadow.setBounds(0, 0, builder.shadow.getMinimumWidth(), builder.shadow.getMinimumHeight());

        return builder;
    }

    public static View.DragShadowBuilder fromUNI(Context context,UNIElementView uni)
    {
        if (uni==null)  {
            throw new IllegalArgumentException("uni cannot be null");
        }

        uni.setDrawingCacheEnabled(true);
        Bitmap tBitmap = uni.getDrawingCache();
        Matrix matrix = new Matrix();
        // 缩放原图
        matrix.postScale(uni.getScaleX(), uni.getScaleY());
        // 向左旋转45度，参数为正则向右旋转
        matrix.postRotate(uni.getRotation());
        tBitmap = tBitmap.createBitmap(tBitmap,0,0,uni.getWidth(),uni.getHeight(),matrix,false);
        return fromBitmap(context,tBitmap);
    }

    @Override
    public void onDrawShadow(Canvas canvas) {
        shadow.draw(canvas);
    }

    @Override
    public void onProvideShadowMetrics(Point shadowSize, Point shadowTouchPoint) {
        shadowSize.x = shadow.getMinimumWidth();
        shadowSize.y = shadow.getMinimumHeight();

        shadowTouchPoint.x = (int)(shadowSize.x / 2);
        shadowTouchPoint.y = (int)(shadowSize.y / 2);
    }
}
