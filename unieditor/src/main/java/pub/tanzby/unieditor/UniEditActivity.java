package pub.tanzby.unieditor;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.TextView;

import com.uni.utils.CAN;
import com.uni.utils.GraphicsTools;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.feeeei.circleseekbar.CircleSeekBar;


public class UniEditActivity extends AppCompatActivity
    implements
        View.OnTouchListener,
        ScaleGestureDetector.OnScaleGestureListener

{
    /*
     * 视图元素
     */
    ConstraintLayout LEFTMENU;      // 左侧滑动菜单
    ConstraintLayout RIGHTMENU;     // 右侧滑动菜单
    RelativeLayout   CAVANS;        // 主屏画板
    RelativeLayout   MAINContent;   // 主屏
    DrawerLayout     ROOT;          // 根

    Button bnt_play;
    Button bnt_next;
    Button bnt_prev;

    CircleSeekBar csb_rotation;
    CircleSeekBar csb_alpha;
    CircleSeekBar csb_scale;
    TextView tv_rotation;
    TextView tv_alpha;
    TextView tv_frameID;
    TextView tv_scale;


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
        CAN.DataBus.requireMenu(10, 0);

        mainEvenBinding();
    }

    /**
     * 元素初始化绑定
     */
    private void Init()
    {
        LEFTMENU = findViewById(R.id.uniEditor_layout_left);
        RIGHTMENU= findViewById(R.id.uniEditor_layout_right);
        CAVANS   = findViewById(R.id.uniEditor_layout_cavans);
        MAINContent = findViewById(R.id.uniEditor_layout_main);
        ROOT     = findViewById(R.id.uniEditor_layout_root);

        bnt_play = findViewById(R.id.bnt_editor_play);
        bnt_next = findViewById(R.id.bnt_editor_next);
        bnt_prev = findViewById(R.id.bnt_editor_prev);

        csb_rotation = findViewById(R.id.CSB_item_setting_rotation);
        csb_alpha    = findViewById(R.id.CSB_item_setting_alpha);
        csb_scale    = findViewById(R.id.CSB_item_setting_scale);

        tv_alpha     = findViewById(R.id.tv_item_setting_alpha);
        tv_rotation  = findViewById(R.id.tv_item_setting_rotation);
        tv_scale     = findViewById(R.id.tv_item_setting_scale);
        tv_frameID   = findViewById(R.id.tv_editor_frameID);

        mMangeer = new UNIElementViewManager(this,CAVANS);
        mScaleGestureDetector = new ScaleGestureDetector(this, this);

        ROOT.setScrimColor(Color.TRANSPARENT);
    }


    /**
     * 比较重要的事件绑定
     */
    private void mainEvenBinding()
    {
        mMangeer.setCtrlButtonGroup(bnt_play,bnt_next,bnt_prev);

        ROOT.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,Gravity.RIGHT);

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
                //new AlertDialog.Builder
            }

            @Override
            public void onItemLongClick(View view, int position, float pos_x, float pos_y) {
                ClipData data =  ClipData.newPlainText("","");
                view.startDrag(data,new View.DragShadowBuilder(view),null,0);

                mMangeer.addToWaiting((UNIElementView)mAdapter.getItem(position));

                ROOT.closeDrawer(Gravity.START);
            }
        });


        /*
         * 调节某一个item的旋转 角度（degree）
         */
        csb_rotation.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    tv_rotation.setAlpha(0.3f);
                }
            };
            @Override
            public void onChanged(CircleSeekBar circleSeekBar, int i) {
                tv_rotation.setAlpha(1);
                tv_rotation.setText(i+"˚");
                mMangeer.batchSetRotation(i);
                handler.removeCallbacks(r);
                handler.postDelayed(r,1000);
            }
        });
        /*
         * 调节某一个item的透明度 [0,100]
         */
        csb_alpha.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    tv_alpha.setAlpha(0.3f);
                }
            };

            @Override
            public void onChanged(CircleSeekBar circleSeekBar, int i) {
                tv_alpha.setAlpha(1);
                tv_alpha.setText(""+i);
                mMangeer.batchSetAlpha((100-i)/100.0f);
                handler.removeCallbacks(r);
                handler.postDelayed(r,1000);
            }
        });

        csb_scale.setOnSeekBarChangeListener(new CircleSeekBar.OnSeekBarChangeListener() {
            Handler handler = new Handler();
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    tv_scale.setAlpha(0.3f);
                }
            };
            @Override
            public void onChanged(CircleSeekBar circleSeekBar, int i) {
                tv_scale.setAlpha(1);
                float cur_scale = i*i/2500.f;
                tv_scale.setText( (int)(cur_scale*100)+"%");
                mMangeer.batchSetScale(cur_scale);
                handler.removeCallbacks(r);
                handler.postDelayed(r,1000);
            }
        });

        /*
         * 为画布上的元素添加点击事件
         */
        mMangeer.setOnUniItemClickLitener(new UNIElementViewManager.OnUniItemClickLitener() {
            @Override
            public void onUniItemClick(UNIElementView view) {
                ROOT.openDrawer(Gravity.RIGHT);
                int cur_alpha = (int) (100-view.getAlpha()*100);        // 透明度 [0, 100]
                int cur_rotation = (int)view.getRotation();             // 旋转角度[0,360]
                int cur_scale = (int) Math.sqrt(view.getScaleX()*2500);
                csb_alpha.setCurProcess(cur_alpha);
                tv_alpha.setText(cur_alpha+"");
                csb_rotation.setCurProcess(cur_rotation);
                tv_rotation.setText(cur_rotation+"˚");
                csb_scale.setCurProcess(cur_scale);
                tv_scale.setText((int)(view.getScaleX()*100)+"%");

                // 将物体放置到左侧中心

            }
        });
    }

    // TODO: 修改成由通知来设置menu
    private void setMenu(Context context)
    {
        UNIElementView ansU;

        ansU = new UNIElementView(context);
        ansU.setImageBitmap(GraphicsTools.imgtool.res2bitmap(context,R.drawable.people));
        u.add(ansU);

        ansU = new UNIElementView(context);
        ansU.setImageBitmap(GraphicsTools.imgtool.res2bitmap(context,R.drawable.dialog));
        u.add(ansU);

        ansU = new UNIElementView(context);
        ansU.setImageBitmap(GraphicsTools.imgtool.res2bitmap(context,R.drawable.low));
        u.add(ansU);

        ansU = new UNIElementView(context);
        ansU.setImageBitmap(GraphicsTools.imgtool.res2bitmap(context,R.drawable.clound));
        u.add(ansU);

        ansU = new UNIElementView(context);
        ansU.setImageBitmap(GraphicsTools.imgtool.res2bitmap(context,R.drawable.dialog2));
        u.add(ansU);

        ansU = new UNIElementView(context);
        ansU.setImageBitmap(GraphicsTools.imgtool.res2bitmap(context,R.drawable.high));
        u.add(ansU);


        ansU = new UNIElementView(context);
        ansU.setImageBitmap(GraphicsTools.imgtool.res2bitmap(context,R.drawable.dialog3));
        u.add(ansU);

        ansU = new UNIElementView(context);
        ansU.setImageBitmap(GraphicsTools.imgtool.res2bitmap(context,R.drawable.dialog4));
        u.add(ansU);




        mAdapter = new UNIMenuElementAdapter<>(context,u);

        RecyclerView rv = LEFTMENU.findViewById(R.id.rv_editor_uni_item_menu);
        rv.setLayoutManager(new GridLayoutManager(context,3));
        rv.setAdapter(mAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateMenu(CAN.Package.Menu.Reply pkg)
    {
        mAdapter.update(pkg.items);
    }

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


    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale2Translaform = detector.getScaleFactor();
        float scaleX = (float) Math.tanh(CAVANS.getScaleX()*scale2Translaform)+0.6f;
        float scaleY = (float) Math.tanh(CAVANS.getScaleY()*scale2Translaform)+0.6f;
        SCALE_FACTOR = scaleX;

        if(!isDrawerOpen)

            CAVANS.setScaleX(scaleX);
            CAVANS.setScaleY(scaleY);

        return false;
    }
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {  }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
