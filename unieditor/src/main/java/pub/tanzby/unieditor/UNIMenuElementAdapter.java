package pub.tanzby.unieditor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uni.utils.Brief;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by tanzb on 2017/10/20 0020.
 */

public class UNIMenuElementAdapter<E> extends RecyclerView.Adapter<UNIMenuElementAdapter.VH> {

    private Context mContext;
    private List<E> itemList;
    public void removeData(int position) {
        itemList.remove(position);
        notifyItemRemoved(position);
    }
    public E getItem(int position)
    {
        return itemList == null?null:itemList.get(position);
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType)
    {
        VH h = new VH(LayoutInflater
                .from(mContext)
                .inflate(R.layout.uni_editor_menu_item, parent,false));
        return h;
    }

    public void update(List<Brief> items)
    {
        int size = items.size();
        itemList = new ArrayList<>(size);

        for(int i = 0; i < size; i++)
        {
            UNIElementView item = (UNIElementView) itemList.get(i);
            Brief brief = items.get(i);
            item.setURL(brief.url);
            item.setImageBitmap(brief.thumb);
        }

        notify();
    }

    @Override
    public void onBindViewHolder(final VH holder, int position) {
        UNIElementView ansView = (UNIElementView) itemList.get(position);
//        holder.Description_name.setText("A");
        holder.ThumbNail.setImageBitmap(ansView.mThumb);

        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    int x = (int)holder.itemView.getX();
                    int y = (int)holder.itemView.getY();
                    mOnItemClickLitener.onItemClick(holder.itemView, pos,x,y);
                }});

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = holder.getLayoutPosition();
                    int x = (int)holder.itemView.getX();
                    int y = (int)holder.itemView.getY();

                    mOnItemClickLitener.onItemLongClick(holder.itemView, pos,x,y );
                    return true;
                }});
        }
    }
    @Override
    public int getItemCount() {
        return itemList == null?0:itemList.size();
    }

    public static class VH extends RecyclerView.ViewHolder {

        ImageView ThumbNail;
        TextView  Description_name;

        public VH(View itemView) {
            super(itemView);
            ThumbNail= (ImageView) itemView.findViewById(R.id.uni_item_single_style_img);
            Description_name = (TextView) itemView.findViewById(R.id.uni_item_single_style_tv);
        }
    }

    public UNIMenuElementAdapter(Context context, List<E> itemList_) {
        itemList = itemList_;
        mContext = context;
    }

    /*创建Item的点击接口*/
    public interface OnItemClickLitener
    {
        void onItemClick(View view, int position, float pos_x, float pos_y);
        void onItemLongClick(View view, int position, float pos_x, float pos_y);
    }
    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
}