package pub.tanzby.unieditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import com.uni.utils.GraphicsTools;
import com.uni.utils.Property;

/**
 * Created by tan on 2017/12/8.
 */

public class UNIElementView extends AppCompatImageView {

    Property mProperty;
    Boolean  hasAddedToCavans;
    Bitmap   mThumb;
    String   mUrl;

    static int DEFAULT_WIDTH = 50;
    static String TAG = "UNIElementView";

    public UNIElementView(Context context) {
        super(context);
        UNIElementViewInit();
    }

    public UNIElementView(Context context, AttributeSet attrs) {
        super(context, attrs);
        UNIElementViewInit();
    }

    public UNIElementView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        UNIElementViewInit();
    }

    private void UNIElementViewInit()
    {
        mProperty = new Property();

        hasAddedToCavans = false;

        setVisibility(INVISIBLE);

    }

    public void setProperty(Property prop)
    {
        mProperty = prop.clone();
        setX(mProperty.x * GraphicsTools.dipScale());
        setY(mProperty.y * GraphicsTools.dipScale());
        setScaleX(mProperty.scale);
        setScaleY(mProperty.scale);
        setVisibility(VISIBLE);
        setAlpha(mProperty.opacity);
        setRotation(mProperty.rotation);
        updatePropoty();
    }

    private void updatePropoty()
    {
        mProperty.x = (int)(getX() / GraphicsTools.dipScale());
        mProperty.y = (int)(getY() / GraphicsTools.dipScale());
        mProperty.opacity = getAlpha();
        mProperty.scale = getScaleX();
        mProperty.rotation = getRotation();
    }

    public void setPositionTo(int x, int y)
    {
        setX(x - getWidth());
        setY(y - getHeight());
        setVisibility(VISIBLE);
        updatePropoty();
    }

    public void moveBy(int x, int y)
    {
        setX(getX()+x);
        setY(getY()+y);
        setVisibility(VISIBLE);
        updatePropoty();
    }

    public void setURL(String url)
    {
        mUrl = url;
    }


    /**
     * 重写函数
     */

    public void SetScale(float scale){
        setScaleX(scale);
        setScaleY(scale);
        mProperty.scale = scale;
    }

    @Override
    public void scrollTo(int x, int y) {
        super.scrollTo(x, y);
        setVisibility(VISIBLE);
        updatePropoty();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mThumb = bm;
    }

    @Override
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
        mProperty.opacity=alpha;
    }

    @Override
    public void setRotation(float rotation) {
        super.setRotation(rotation);
        mProperty.rotation = rotation;
    }

    @Override
    protected void onAttachedToWindow() {

        int mini = (int) GraphicsTools.dip2px(getContext(),(int)DEFAULT_WIDTH);
        setMinimumHeight(mini);
        setMinimumWidth(mini);
        super.onAttachedToWindow();
    }

    public UNIElementView clone(Context context)
    {
        UNIElementView copy = new UNIElementView(context);
        copy.setImageBitmap(mThumb);
        copy.setURL(mUrl);
        copy.mProperty = mProperty.clone();
        return copy;
    }

}
