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

    public void setPropertyAndBitmap(Property prop,Bitmap thumb)
    {
        if (this.getParent() == null){
            Log.i(TAG+" reflashProperty"," 未添加到画板");
            setVisibility(INVISIBLE);
        }
        else
        {
            mProperty = prop.clone();
            setX(mProperty.x);
            setY(mProperty.y);
            setVisibility(VISIBLE);
            setAlpha(mProperty.opacity);
            setImageBitmap(mThumb = thumb);
        }
    }

    private void updatePropoty()
    {
        mProperty.x = (int)getX();
        mProperty.y = (int)getY();
        mProperty.opacity = getAlpha();
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


    /**
     * 重写函数
     */

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
        // TODO
    }

    @Override
    protected void onAttachedToWindow() {

        int mini = (int) GraphicsTools.dip2px(getContext(),(int)DEFAULT_WIDTH);
        setMinimumHeight(mini);
        setMinimumWidth(mini);
        super.onAttachedToWindow();
    }
}
