package pub.tanzby.unieditor;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.RelativeLayout;

import com.uni.utils.CAN;
import com.uni.utils.Property;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import pub.tanzby.unieditor.UNIElementView;
/**
 * Created by tan on 2017/12/8.
 *
 * 用于管理uni element view
 * 处理高层级发过来的数据，进行UNIElementView的增删查改
 */

public class UNIElementViewManager {

    //用于维护时序的frameID
    private int frameID;
    private int cur_frameID_count; // 一直递增

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
        mCanvans = canvans;
        mContext  = context;

        current_added_element = new ArrayList<>();
        current_selected_element = new ArrayList<>();
        current_waiting_added_element = new ArrayList<>();
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

    public void batchMove(float deltaX, float deltaY)
    {
        for(UNIElementView u: current_selected_element)
        {
            u.setPositionByDelta((int)deltaX,(int)deltaY);
        }
    }

    public void addToWaiting(UNIElementView oriItem)
    {
        current_waiting_added_element.add(oriItem);
    }

    public void addToCavansFromMenu(int x, int y)
    {
        for (UNIElementView ori: current_waiting_added_element)
        {
            UNIElementView mUniViewItem = new UNIElementView(mContext);

            //TODO 一个 菜单元素 到 实例化到函数
            mUniViewItem.setPositionByCoor(x,y);
            mUniViewItem.setThumb(ori.mThumb);
            mUniViewItem.setID(cur_frameID_count);
            cur_frameID_count ++;


            // 绑定 拖拽 程序
            mUniViewItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    current_selected_element.clear();
                    current_selected_element.add((UNIElementView)v);
                    ClipData data =  ClipData.newPlainText("ID","");
                    v.startDrag(data,new View.DragShadowBuilder(v),null,0);
                    return false;
                }
            });
            current_added_element.add(mUniViewItem);

            mCanvans.addView(mUniViewItem);

            CAN.DataBus.addElement(frameID,mUniViewItem.setID(),mUniViewItem.mProperty);


        }

        current_waiting_added_element.clear();

    }


    /**
     * UNIEditor中用于接受管理器发来的属性，进行画布更新
     * @param Props 元素属性列表
     * @param Imgs  元素背景略缩图列表
     */
    public void updateCanvas(List<Property> Props, List<Bitmap> Imgs)
    {
        // 清空画板
        mCanvans.removeAllViews();

        for (int i = 0;i < Props.size(); i++)
        {
            UNIElementView u = new UNIElementView(mContext);
            u.setImageBitmap(Imgs.get(i));
            u.mProperty = Props.get(i);
        }
    }


    public boolean isWaitToPlace() {
        return current_waiting_added_element.size() > 0;
    }


    /**
     * 请求上一帧
     */
    public void updatePrevFrame()
    {
        //TODO 将当前所有元素的属性更新发送给服务
        // 更新是否有上上帧
        frameID++;

        if (true) // 没有下一帧了
        {
            // 置 按钮为灰色 并设置标志位
        }

        CAN.DataBus.requireUpdate(frameID);

        // TODO  cur_frameID_count 也需要更新

    }

    /**
     * 请求下一帧
     */
    public void updateNextFrame()
    {
        //TODO 将当前所有元素的属性更新发送给服务
        // 更新是否有下下帧
        frameID--;
        if (true) // 没有上一帧了
        {
            // 置 按钮为灰色 并设置标志位
        }

        CAN.DataBus.requireUpdate(frameID);
        // TODO       cur_frameID_count 也需要更新
    }

    /**
     * 删除当前帧
     */
    public void deleteCurrentFrame()
    {
        //TODO 将当前所有元素的属性更新发送给服务
        frameID--;
        if (frameID == 0) // 已经没有帧了
        {
            // 清空画板
            // 重置所有
        }

        CAN.DataBus.deleteElement(frameID);

        // TODO       cur_frameID_count 也需要更新
    }
}
