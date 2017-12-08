package pub.tanzby.unieditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;

import com.uni.utils.Graphicstools;
import com.uni.utils.Property;

/**
 * Created by tan on 2017/12/8.
 */

public class UNIElementView extends AppCompatImageView {

    Property mProperty;
    Boolean  hasAddedToCavans;
    Bitmap   mThumb;
    int  ID;


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

    public Integer setID(){return ID;}
    public void setID(Integer curID){ID = curID;}

    private void UNIElementViewInit()
    {
        mProperty = new Property();
        /*
          默认值设定
         */
        mProperty.x = 0;
        mProperty.y = 0;
        mProperty.mode = Property.Mode.Linear;
        hasAddedToCavans = false;

        /*
          默认行为设定
         */
        setVisibility(INVISIBLE);

    }

    public void setPropertyAndBM(Property prop,Bitmap thumb)
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

    public void setPositionByDelta(Integer deltaX,Integer deltaY)
    {
        setX(getX()+deltaX);
        setY(getY()+deltaY);
        setVisibility(VISIBLE);
        updatePropoty();
    }
    public void setPositionByCoor(int x, int y)
    {
        setX(x-getWidth()/2);
        setY(y-getHeight()/2);
        setVisibility(VISIBLE);
        updatePropoty();
    }

    private void updatePropoty()
    {
        mProperty.x = (int)getX();
        mProperty.y = (int)getY();
        mProperty.opacity = getAlpha();
    }

    public void setThumb(Bitmap bmp)
    {
        mThumb = bmp;
        setImageBitmap(mThumb);
    }




    /**
     * 重写函数
     */

    @Override
    protected void onAttachedToWindow() {

        ViewGroup.LayoutParams ll = this.getLayoutParams();
        ll.height=ll.width =  DEFAULT_WIDTH;
        setLayoutParams(ll);
        super.onAttachedToWindow();
    }
}
