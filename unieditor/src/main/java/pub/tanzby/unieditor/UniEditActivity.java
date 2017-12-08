package pub.tanzby.unieditor;

import android.content.ClipDescription;
import android.graphics.Color;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;


public class UniEditActivity extends AppCompatActivity
    implements
        View.OnTouchListener,
        ScaleGestureDetector.OnScaleGestureListener

{
    /*
     * 视图元素
     */
    ConstraintLayout MENUVIEW;      // 左侧滑动菜单
    RelativeLayout   CAVANS;        // 主屏画板
    RelativeLayout   MAINContent;   // 主屏
    DrawerLayout     ROOT;          // 根

    /*
     * 管理器及变量
     */
    UNIElementViewManager mMangeer;
    ScaleGestureDetector mScaleGestureDetector;

    // 拖动需要
    int lastX;
    int lastY;
    float SCALE_FACTOR;
    boolean isDrawerOpen;

    private final String TAG = "EDITOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uni_editor_basic_layout);

        Init();

        mainEvenBinding();
    }

    /**
     * 元素初始化绑定
     */
    private void Init()
    {
        MENUVIEW = findViewById(R.id.uniEditor_layout_left);
        CAVANS   = findViewById(R.id.uniEditor_layout_cavans);
        MAINContent = findViewById(R.id.uniEditor_layout_main);
        ROOT     = findViewById(R.id.uniEditor_layout_root);

        mScaleGestureDetector = new ScaleGestureDetector(this, this);
    }



    /**
     * 比较重要的事件绑定
     */
    private void mainEvenBinding()
    {
        CAVANS.setOnDragListener(new View.OnDragListener() {
            private Point startPoint;
            @Override
            public boolean onDrag(View v, DragEvent event) {
                if (mMangeer.isNoSelectElement())
                    return false;
                switch (event.getAction())
                {
                    case DragEvent.ACTION_DRAG_STARTED:
                        startPoint=new Point((int)event.getX(),(int)event.getY());
                        return event.getClipDescription().hasMimeType(
                                ClipDescription.MIMETYPE_TEXT_PLAIN);
                    case DragEvent.ACTION_DRAG_ENTERED:
                        CAVANS.setBackgroundColor(Color.WHITE);
                        return true;
                    case DragEvent.ACTION_DRAG_LOCATION:
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                        CAVANS.setBackgroundColor(Color.YELLOW);
                        return true;
                    case DragEvent.ACTION_DROP:
                        //TODO batch or initial place
                        return true;
                    case DragEvent.ACTION_DRAG_ENDED:
                        CAVANS.setBackgroundColor(Color.WHITE);
                        return true;
                    default:
                        break;
                }
                return false;
            }
        });
        MAINContent.setOnTouchListener(this);

    }

    /**
     * Called when a touch event is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     *
     * @param v     The view the touch event has been dispatched to.
     * @param event The MotionEvent object containing full information about
     *              the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int rawX = (int) event.getRawX();
        int rawY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = rawX;
                lastY = rawY;
                break;
            case MotionEvent.ACTION_MOVE:

                int offsetX = rawX - lastX;
                int offsetY = rawY - lastY;

                if(!isDrawerOpen)
                    MAINContent.scrollBy(-offsetX, -offsetY);

                lastX = rawX;
                lastY = rawY;

                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        boolean mScaleGes =mScaleGestureDetector.onTouchEvent(event);
        return mScaleGes;
    }

    /**
     * Responds to scaling events for a gesture in progress.
     * Reported by pointer motion.
     *
     * @param detector The detector reporting the event - use this to
     *                 retrieve extended info about event state.
     * @return Whether or not the detector should consider this event
     * as handled. If an event was not handled, the detector
     * will continue to accumulate movement until an event is
     * handled. This can be useful if an application, for example,
     * only wants to update scaling factors if the change is
     * greater than 0.01.
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale2Translaform = detector.getScaleFactor();
        float scaleX = (float) Math.tanh(CAVANS.getScaleX()*scale2Translaform)*1.3f;
        float scaleY = (float) Math.tanh(CAVANS.getScaleY()*scale2Translaform)*1.3f;
        SCALE_FACTOR = scaleX;

        //设置放大缩小锚点
        CAVANS.setPivotX(detector.getFocusX());
        CAVANS.setPivotY(detector.getFocusY());
        if(!isDrawerOpen)
            CAVANS.setScaleX(scaleX < 0.3f? 0.3f:scaleX);
        CAVANS.setScaleY(scaleY < 0.3f? 0.3f:scaleY);

        return false;
    }

    /**
     * Responds to the beginning of a scaling gesture. Reported by
     * new pointers going down.
     *
     * @param detector The detector reporting the event - use this to
     *                 retrieve extended info about event state.
     * @return Whether or not the detector should continue recognizing
     * this gesture. For example, if a gesture is beginning
     * with a focal point outside of a region where it makes
     * sense, onScaleBegin() may return false to ignore the
     * rest of the gesture.
     */
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    /**
     * Responds to the end of a scale gesture. Reported by existing
     * pointers going up.
     * <p>
     * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
     * and {@link ScaleGestureDetector#getFocusY()} will return focal point
     * of the pointers remaining on the screen.
     *
     * @param detector The detector reporting the event - use this to
     *                 retrieve extended info about event state.
     */
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

}
