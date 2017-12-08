package pub.tanzby.unieditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.RelativeLayout;

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

    // 当前被选中的元素，通常用于批量移动
    private List<UNIElementView> current_selected_element;

    // 当前添加到画板中的元素
    private List<UNIElementView> current_added_element;

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

        //注册EventBus
        EventBus.getDefault().register(this);


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

    public void batchMove(Integer deltaX, Integer deltaY)
    {
        for(UNIElementView u: current_selected_element)
        {
            u.setPostionByDelta(deltaX,deltaY);
        }
    }


    /**
     * UNIEditor中用于接受管理器发来的属性，进行画布更新
     * @param Props 元素属性列表
     * @param Imgs  元素背景略缩图列表
     */
    @Subscribe(threadMode = ThreadMode.ASYNC)
    private void updateCanvas(List<Property> Props, List<Bitmap> Imgs)
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



}
