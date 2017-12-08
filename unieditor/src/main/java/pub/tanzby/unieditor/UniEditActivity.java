package pub.tanzby.unieditor;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.uni.utils.Graphicstools;
import com.uni.utils.Property;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


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

    Button bnt_play;
    Button bnt_next;
    Button bnt_prev;

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

    UNIMenuElementAdapter mAdapter;
    List<UNIElementView> u = new ArrayList<>();

    private final String TAG = "EDITOR";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uni_editor_basic_layout);

        Init();

        setMenu(this);

        mainEvenBinding();
    }

    /**
     * 元素初始化绑定
     */
    private void Init()
    {
        //注册EventBus
//        EventBus.getDefault().register(this);

        MENUVIEW = findViewById(R.id.uniEditor_layout_left);
        CAVANS   = findViewById(R.id.uniEditor_layout_cavans);
        MAINContent = findViewById(R.id.uniEditor_layout_main);
        ROOT     = findViewById(R.id.uniEditor_layout_root);

        bnt_play = findViewById(R.id.bnt_editor_play);
        bnt_next = findViewById(R.id.bnt_editor_next);
        bnt_prev = findViewById(R.id.bnt_editor_prev);

        mMangeer = new UNIElementViewManager(this,CAVANS);
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
                        if (mMangeer.isWaitToPlace()) {
                            mMangeer.addToCavansFromMenu((int)event.getX(),(int)event.getY());
                        }
                        else {
                            mMangeer.batchMove(event.getX()-startPoint.x,
                                    event.getY()-startPoint.y);
                        }
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
//        MAINContent.setOnDragListener(new View.OnDragListener() {
//            public boolean onDrag(View v, DragEvent event) {
//                switch (event.getAction()) {
//                    case DragEvent.ACTION_DRAG_STARTED:
//                        return true;
//                    case DragEvent.ACTION_DRAG_ENTERED:
//                        return true;
//                    case DragEvent.ACTION_DRAG_LOCATION:
//                        return true;
//                    case DragEvent.ACTION_DRAG_EXITED:
//                        return true;
//                    case DragEvent.ACTION_DROP:
//                        Log.i(TAG,"顶层的激活了");
//                        return false;
//                    case DragEvent.ACTION_DRAG_ENDED:
//                        return true;
//                    default:
//                        break;
//                }
//                return false;
//            }
//        });
        ROOT.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) { }

            @Override
            public void onDrawerOpened(View drawerView) { isDrawerOpen = true; }

            @Override
            public void onDrawerClosed(View drawerView) { isDrawerOpen = false; }

            @Override
            public void onDrawerStateChanged(int newState) {  }
        });

        mAdapter.setOnItemClickLitener(new UNIMenuElementAdapter.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position, float pos_x, float pos_y) {

            }

            @Override
            public void onItemLongClick(View view, int position, float pos_x, float pos_y) {
                ClipData data =  ClipData.newPlainText("","");
                view.startDrag(data,new View.DragShadowBuilder(view),null,0);

                mMangeer.addToWaiting((UNIElementView)mAdapter.getItem(position));

                ROOT.closeDrawer(Gravity.START);
            }
        });



        bnt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        bnt_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setMenu(Context context)
    {
        UNIElementView ansU = new UNIElementView(context);
        ansU.setThumb(Graphicstools.imgtool.res2bitmap(context,R.drawable.ic_favorite));
        u.add(ansU);
        ansU = new UNIElementView(context);
        ansU.setThumb(Graphicstools.imgtool.res2bitmap(context,R.drawable.ic_favorite_border_grey_500_24dp));
        u.add(ansU);
        ansU = new UNIElementView(context);
        ansU.setThumb(Graphicstools.imgtool.res2bitmap(context,R.drawable.ic_border_color_grey_400_24dp));
        u.add(ansU);

        mAdapter = new UNIMenuElementAdapter<UNIElementView>(context,u);

        RecyclerView rv = MENUVIEW.findViewById(R.id.rv_editor_uni_item_menu);
        rv.setLayoutManager(new GridLayoutManager(context,4));
        rv.setAdapter(mAdapter);
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
        float scaleX = (float) Math.tanh(CAVANS.getScaleX()*scale2Translaform)*1.4f;
        float scaleY = (float) Math.tanh(CAVANS.getScaleY()*scale2Translaform)*1.4f;
        SCALE_FACTOR = scaleX;

        //设置放大缩小锚点
        CAVANS.setPivotX(detector.getFocusX());
        CAVANS.setPivotY(detector.getFocusY());
        if(!isDrawerOpen)
            CAVANS.setScaleX(scaleX < 0.4f? 0.4f:scaleX);
        CAVANS.setScaleY(scaleY < 0.4f? 0.4f:scaleY);

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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(List<Property> Props, List<Bitmap> Imgs)
    {
        if (mMangeer!=null)
        {
            mMangeer.updateCanvas(Props,Imgs);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        EventBus.getDefault().unregister(this);
    }
}
