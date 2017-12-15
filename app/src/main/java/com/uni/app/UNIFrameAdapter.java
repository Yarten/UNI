package com.uni.app;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.uni.uniplayer.UNIFrame;
import com.uni.uniplayer.UNIView;
import com.uni.utils.Brief;
import com.uni.utils.CAN;
import com.uni.utils.FileUtils;
import com.yarten.unimanager.UNIManager;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

/**
 * Created by tan on 2017/12/8.
 *
 * 用于首页显示有你帧
 */

public class UNIFrameAdapter <E> extends RecyclerView.Adapter<UNIFrameAdapter.VH>{

    List<E> mList;
    Context mContext;
    UpdateHandler mHandler;

    public UNIFrameAdapter(Context context){
        this(context, null);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        CAN.logout(this);
    }

    public UNIFrameAdapter(Context context, List<E> list_){
        mList = list_;
        mContext = context;
        mHandler = new UpdateHandler();
        CAN.login(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void updateMainMenu(CAN.Package.Menu.Reply pkg)
    {
        if(pkg.what != CAN.Package.Menu.Type.MainMenu) return;
        List<Brief> items = pkg.items;

        // TODO: 临时的操作：直接在这里加载UNIFrame
        mList.clear();
        for(Brief item : items)
        {
            File dir = FileUtils.instance.getCacheDir(item.title);
            UNIFrame uniFrame = UNIManager.instance.getUNIFrame(dir, item.title);
            mList.add((E)(uniFrame.clone()));
        }

        mHandler.sendEmptyMessage(0);
    }

    class UpdateHandler extends Handler
    {
        @Override
        public void handleMessage(Message msg) {
            notifyDataSetChanged();
        }
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        VH h = new VH(LayoutInflater
                .from(mContext)
                .inflate(R.layout.items_uni_mainactivity, parent,false));
        return h;
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        holder.title.setText(mList.get(position).toString());
        UNIFrame uniFrame = (UNIFrame) mList.get(position);
        holder.uniView.setUNIFrame(uniFrame);


        if (mOnItemClickLitener != null) {
            final  int pos = holder.getLayoutPosition();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onFrameClick(holder.uniView, pos);
                }});

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickLitener.onFrameLongClick(holder.title, pos);
                    return true;
                }});
            holder.bnt_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickLitener.onEditButtomClick(v,pos);
                }
            });
            holder.bnt_favor.setOnClickListener(new View.OnClickListener() {
                Boolean isfavour = false;
                @Override
                public void onClick(View v) {

                    mOnItemClickLitener.onFavourButtomClick(v,pos, isfavour);
                    if (isfavour)
                    {
                        holder.bnt_favor.setBackgroundResource(R.drawable.ic_favorite);
                    }
                    else
                    {
                        holder.bnt_favor.setBackgroundResource(R.drawable.ic_favorite_border_grey_500_24dp);
                    }
                    isfavour = !isfavour;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    public E getItem(Integer position)
    {
        return mList == null? null : mList.get(position);
    }

    public static class VH extends RecyclerView.ViewHolder {

        TextView title;
        ImageButton bnt_favor;
        ImageButton bnt_edit;
        UNIView uniView;


        public VH(View itemView) {
            super(itemView);
            title= (TextView) itemView.findViewById(R.id.tv_uni_mianactivity_title);
            bnt_favor = (ImageButton) itemView.findViewById(R.id.imbnt_uni_mianactivity_favor);
            bnt_edit  = (ImageButton) itemView.findViewById(R.id.imbnt_uni_mianactivity_edit);
            uniView = itemView.findViewById(R.id.UNIView);
        }
    }


    /**
     * 创建Item的点击接口
     */
    public interface OnItemClickLitener
    {
        void onFrameClick(View view, int position);
        void onFrameLongClick(View view , int position);
        void onFavourButtomClick(View view, int position,Boolean isfavour);
        void onEditButtomClick(View view, int position);
    }
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}
