package pub.tanzby.unieditor;


import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.uni.utils.Brief;
import com.uni.utils.CAN;
import com.uni.utils.FrameProperty;
import com.uni.utils.GraphicsTools;
import com.uni.utils.Playable;
import com.uni.utils.Property;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tan on 2017/12/8.
 *
 * 用于管理uni element view
 * 处理高层级发过来的数据，进行UNIElementView的增删查改
 */

public class UNIElementViewManager {

    //region 构造器
    /**
     * 用于维护时序的frameID
     * frameID 将从 0 开始计数
     */

    private int frameID;

    private Boolean hasNext;

    private boolean is_playing = false;


    public Button bnt_ctrl_play;
    public Button bnt_ctrl_next;
    public Button bnt_ctrl_prev;
    public Button bnt_ctrl_save;
    public Button bnt_ctrl_dele;
    public Button bnt_ctrl_inse;


    // 当前被选中的元素，通常用于批量移动
    private List<UNIElementView> current_selected_element;

    // 当前添加到画板中的元素
    private List<UNIElementView> current_added_element;

    private List<UNIElementView> current_waiting_added_element;

    // View 初始化时所要的上下文
    private Context mContext;

    // View 初始化时需要的addTo的画布
    private RelativeLayout mCanvans;



    UNIElementViewManager(Context context,RelativeLayout canvans)
    {
        frameID = 0;
        hasNext = false;
        is_playing = false;

        mCanvans = canvans;
        mContext  = context;

        current_added_element = new ArrayList<>();
        current_selected_element = new ArrayList<>();
        current_waiting_added_element = new ArrayList<>();

        // 注册EventBus
        EventBus.getDefault().register(this);

        // 绑定Canvans更新

    }

    @Override
    protected void finalize() throws Throwable {
        //注销EventBus
        EventBus.getDefault().unregister(this);
        super.finalize();
    }
    //endregion

    //region Getter
    /**
     * 获取当前Uni frame 的ID
     * @return 返回当前的帧的ID数值
     */
    public Integer getFrameID()
    {
        return frameID;
    }


    /**
     * 获取是否还有下一帧
     * @return <tt>true</tt> 还有下一帧
     */
    public Boolean hasNextFrame()
    {
        return hasNext;
    }

    /**
     * 是否正在Canvas中进行UNI帧的播放
     * @return <tt>true</tt> 正在播放
     */
    public Boolean isPlaying(){return is_playing;}
    //endregion

    //region 捆绑事件处理
    /**
     * 设置控制 Manager 的外部按钮，并在此处进行按钮的事件绑定<br>
     * 并在 forbidAllBotton 处进行按钮的禁用设置<br>
     * 在 updateAllBotton 处进行按钮的更新设置
     *
     * @param play 播放/暂停 按钮
     * @param next 请求下一帧按钮设置
     * @param prev 请求上一帧按钮设置
     * @param save 请求保存按钮设置
     * @param dele 请求删除按钮设置
     * @param inse 请求插入或添加按钮设置
     */
    public void setCtrlButtonGroup(@NonNull Button play,
                                   @NonNull Button next,
                                   @NonNull Button prev,
                                   @NonNull Button save,
                                   @NonNull Button dele,
                                   @NonNull Button inse)
    {

       bnt_ctrl_play=play;
       bnt_ctrl_prev=prev;
       bnt_ctrl_next=next;
       bnt_ctrl_save=save;
       bnt_ctrl_dele=dele;
       bnt_ctrl_inse=inse;

       forbidAllBotton();  // 禁用所有按钮

       bnt_ctrl_next.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Toast.makeText(mContext,"next",Toast.LENGTH_SHORT).show();
               requestNextFrame();
           }
       });
        bnt_ctrl_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            Toast.makeText(mContext,"play",Toast.LENGTH_SHORT).show();
            if (!is_playing) //查询服务器中是否已经有这一个 uni frame
            {
                resetCanvas();
                CAN.Control.requirePlayer();
                bnt_ctrl_play.setBackgroundResource(R.drawable.ic_pause_red_a400_24dp);
                is_playing = true;
            }
            else
            {
                CAN.DataBus.requireUpdate(frameID);
                is_playing=false;
            }
            }
        });

        bnt_ctrl_prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"previous",Toast.LENGTH_SHORT).show();
                requestPrevFrame();
            }
        });

        bnt_ctrl_inse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"insert",Toast.LENGTH_SHORT).show();
                requestNewFrame();
            }
        });

        bnt_ctrl_dele.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"delete",Toast.LENGTH_SHORT).show();
                deleteCurrentFrame();
            }
        });

        bnt_ctrl_save.setOnClickListener(new View.OnClickListener() {
            private EditText edit = new EditText(mContext);
            private AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setView(edit)
                    .setTitle("正在保存UNI，请确认")
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (!edit.getText().toString().isEmpty()){
                                saveCanvas(); // 保存全部
                                Brief b = new Brief();
                                b.title = edit.getText().toString();
                                b.thumb = GraphicsTools.getShotCut(mCanvans);
                                CAN.DataBus.commit(b,CAN.Package.EditorCommit.State.Save);
                                ((UniEditActivity)mContext).finish();
                            }
                            else {
                                edit.setError("不能为空");
                            }
                        }
                    }).create();

            @Override
            public void onClick(View v) {
                edit.setHint("UNI的名字");
                dialog.show();
            }
        });
    }

    /**
     * 当前是否有元素被选到
     * @return <tt>true</tt> 没有元素被选中
     */
    public boolean isNoSelectElement()
    {
        return current_selected_element.size() == 0;
    }

    /**
     * 将当前某一个UNI元素调添加到被选中的列表中
     * @param u 需要被添加到已选中列表中的元素
     */
    public void addSelectView(UNIElementView u)
    {
        if (current_added_element.contains(u))
        {
            if (!current_selected_element.contains(u))
            {
                current_selected_element.add(u);
            }
        }
        else
        {
            //TODO
        }
    }

    /**
     * 从被选中列表中移除指定的元素
     * @param u 需要被移除的元素
     * @return <tt>true</tt> 被选中的元素存在 被选中项 中
     */
    public boolean removeSelectView(UNIElementView u)
    {
       return  current_selected_element.remove(u);
    }

    /**
     * 对被选中列表中元素进行 (deltaX,deltaY) 幅度的移动，坐标系尺度基于canvas
     * @param deltaX
     * @param deltaY
     */
    public void batchMove(float deltaX, float deltaY)
    {
        for(UNIElementView u: current_selected_element)
        {
            u.moveBy((int)deltaX,(int)deltaY);
        }
    }

    /**
     * 批量设置透明度
     * @param alpha 透明度
     */
    public void batchSetAlpha(float alpha)
    {
        for(UNIElementView u: current_selected_element)
        {
            u.setAlpha(alpha);
        }
    }

    /**
     * 批量设置旋转量
     * @param rotation 旋转量
     */
    public void batchSetRotation(float rotation)
    {
        for(UNIElementView u: current_selected_element)
        {
            u.setRotation(rotation);
        }
    }
    /**
     * 批量设置尺度
     * @param scale 尺度
     */
    public void batchSetScale(float scale)
    {
        for(UNIElementView u: current_selected_element)
        {
            u.SetScale(scale);
        }
    }

    public void addToWaiting(UNIElementView oriItem)
    {
        current_waiting_added_element.add(oriItem);
    }
    public void clearWaiting(){current_waiting_added_element.clear();}
    public boolean isWaitToPlace() {
        return current_waiting_added_element.size() > 0;
    }
    public UNIElementView getTopWaitingItem(){return current_waiting_added_element.get(0);}
    //endregion

    //region 画布管理
    /**
     * 实例化添加到 "等待添加列表" 的元素
     * @param x 最终位置的x值 但对batch add 来说应该时质点 x
     * @param y 最终位置的y值 但对batch add 来说应该时质点 y
     */
    public void addToCanvasFromMenu(int x, int y)
    {
        final float localScale = UniEditActivity.LocalScale;
        final int halfLength = Property.ElementLength / 2 * GraphicsTools.dipScale();
        x = (int)(x / localScale - halfLength);
        y = (int)(y / localScale - halfLength);

        for (UNIElementView ori: current_waiting_added_element)
        {
            UNIElementView mUniViewItem = ori.clone(mContext);
            addElementView(mUniViewItem, new Property(ori.mProperty.width,ori.mProperty.height, x, y));
            current_added_element.add(mUniViewItem);
            CAN.DataBus.addElement(frameID,mUniViewItem.mProperty,mUniViewItem.mThumb, mUniViewItem.mUrl);
        }
        current_waiting_added_element.clear();
    }

    private void addElementView(UNIElementView view, Property p)
    {
        view.setProperty(p);

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                current_selected_element.clear();
                current_selected_element.add((UNIElementView)v);
                ClipData data =  ClipData.newPlainText("ID","");
                v.startDrag(data,UNIDragShadowBuilder.fromUNI(mContext,(UNIElementView)v),null,0);
                return false;
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            current_selected_element.clear();
            current_selected_element.add((UNIElementView)v);
            mOnUniItemClickLitener.onUniItemClick((UNIElementView)v);
            }
        });

        mCanvans.addView(view);
        this.updateAllBotton();
    }

    /**
     * 保存Canvas上所有的元素
     */
    private void saveCanvas() {
        for (UNIElementView uni: current_added_element) {
            CAN.DataBus.updateElement(frameID,uni.mProperty);
        }
    }


    /**
     * 重置画板
     */
    private void resetCanvas()
    {

        // 恢复缩放界面
        mCanvans.removeAllViews();

        mCanvans.setScaleX(1);
        mCanvans.setScaleY(1);

        ((RelativeLayout)mCanvans.getParent()).scrollTo(0, 0);

        // 清除本地数据
        current_added_element.clear();
        current_waiting_added_element.clear();
        current_selected_element.clear();
    }
    //endregion

    //region 帧按钮管理
    /**
     * 请求上一帧
     */
    public void requestPrevFrame()
    {
        if (frameID > 0)
        {
            forbidAllBotton();
            saveCanvas();
            CAN.DataBus.requireUpdate(--frameID);
        }
    }

    /**
     * 请求下一帧
     */
    public void requestNextFrame()
    {
        if (hasNext) {
            forbidAllBotton();
            saveCanvas();
            CAN.DataBus.requireUpdate(++frameID);
        }
    }

    /**
     * 在当前帧的位置插入一帧，如 1,2,3,4 当前在 2 时 * 将会插入到 1 2 * 3 4
     * 添加了之后直接跳到这一个帧
     */
    public void requestNewFrame()
    {
        forbidAllBotton();
        // 通知服务
        saveCanvas();
        CAN.DataBus.addFrame(++frameID);
    }

    /**
     * 删除当前帧
     */
    public void deleteCurrentFrame()
    {
        forbidAllBotton();
        CAN.DataBus.deleteFrame(frameID);
        if(!hasNext && frameID != 0)
            frameID--;
    }
    //endregion

    //region 按钮事件
    /**
     * Canvas 中每一个 item 的点击接口
     */

    public interface OnUniItemClickLitener
    {
        void onUniItemClick(UNIElementView view);
    }
    private OnUniItemClickLitener mOnUniItemClickLitener;

    public void setOnUniItemClickLitener(OnUniItemClickLitener mOnUniItemClickLitener)
    {
        this.mOnUniItemClickLitener = mOnUniItemClickLitener;
    }

    /**
     * 禁用所有按钮，启动之后按钮全部被置为不可点击状态，并相应替换背景图片
     */
    private void forbidAllBotton()
    {
        bnt_ctrl_prev.setEnabled(false);
        bnt_ctrl_play.setEnabled(false);
        bnt_ctrl_next.setEnabled(false);
        bnt_ctrl_dele.setEnabled(false);
        bnt_ctrl_inse.setEnabled(false);
        bnt_ctrl_save.setEnabled(false);

        bnt_ctrl_prev.setBackgroundResource(R.drawable.ic_chevron_left_grey_400_24dp);
        bnt_ctrl_play.setBackgroundResource(R.drawable.ic_play_arrow_grey_400_24dp);
        bnt_ctrl_next.setBackgroundResource(R.drawable.ic_chevron_right_grey_400_24dp);
        bnt_ctrl_dele.setBackgroundResource(R.drawable.ic_delete_forever_grey_400_24dp);
        bnt_ctrl_inse.setBackgroundResource(R.drawable.ic_add_location_grey_400_24dp);
        bnt_ctrl_save.setBackgroundResource(R.drawable.ic_save_grey_400_24dp);
    }

    /**
     * 根据一定条件更改所有才做按钮的状态
     */
    private void updateAllBotton()
    {
        if (bnt_ctrl_prev!=null)
        {
            if (frameID!= 0) {
                bnt_ctrl_prev.setBackgroundResource(R.drawable.ic_chevron_left_grey_900_24dp);
                bnt_ctrl_prev.setEnabled(true);
            }
        }
        if (bnt_ctrl_play!=null)
        {
            if (true) // TODO: 能播放的条件
            {
                bnt_ctrl_play.setBackgroundResource(R.drawable.ic_play_arrow_red_a400_24dp);
                bnt_ctrl_play.setEnabled(true);
            }
        }
        if (bnt_ctrl_next!=null)
        {
            if (hasNext)
            {
                bnt_ctrl_next.setBackgroundResource(R.drawable.ic_chevron_right_grey_900_24dp);
                bnt_ctrl_next.setEnabled(true);
            }
        }
        if (bnt_ctrl_save!=null)
        {
            if (mCanvans.getChildCount()!=0) // TODO: 能保存的条件
            {
                bnt_ctrl_save.setEnabled(true);
                bnt_ctrl_save.setBackgroundResource(R.drawable.ic_save_grey_900_24dp);
            }
        }
        if (bnt_ctrl_inse!=null) {
            if (true) // TODO: 能插入的条件
            {
                bnt_ctrl_inse.setEnabled(true);
                bnt_ctrl_inse.setBackgroundResource(R.drawable.ic_add_location_grey_900_24dp);
            }
        }
        if (bnt_ctrl_dele!=null)
        {
            if (frameID!=0) // TODO: 能删除的条件
            {
                bnt_ctrl_dele.setEnabled(true);
                bnt_ctrl_dele.setBackgroundResource(R.drawable.ic_delete_grey_900_24dp);
            }
        }
    }
    //endregion

    //region 订阅者
    /**
     * UNIEditor中用于接接收管理器发来的属性，进行画布更新
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateCanvas(CAN.Package.EditorUpdate pkg) {
        FrameProperty frameProperty = pkg.frameProperty;

        // TODO: 加上帧的另外两个属性的设置：与上一帧的距离，当前帧的停留时间。

        hasNext = frameProperty.hasNext;
        //更新按钮
        updateAllBotton();
        // 清空画板
        resetCanvas();
        // 更新帧号
        ((TextView) ((RelativeLayout) mCanvans.getParent()).findViewById(R.id.tv_editor_frameID)).setText(frameID + "");

        for (int i = 0; i < pkg.props.size(); i++) {
            UNIElementView u = new UNIElementView(mContext);
            u.setImageBitmap(pkg.images.valueAt(i));
            u.mProperty = pkg.props.valueAt(i).clone();
            addElementView(u, u.mProperty);
            current_added_element.add(u);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updatePlayer(CAN.Package.Player.Reply pkg)
    {
        Playable player = pkg.player;
        View view = (View)player;
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(lp);
        view.setBackgroundColor(Color.GRAY);
        mCanvans.addView(view);
        player.play();
    }
    //endregion
}
