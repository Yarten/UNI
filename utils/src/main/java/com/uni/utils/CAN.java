package com.uni.utils;

import android.graphics.Bitmap;
import android.util.SparseArray;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * 模块局域网<br>
 * Created by yarten on 2017/12/7.<br>
 * 对EventBus作了封装，用于各个模块间的数据交换和相互调用。<br>
 * 涉及到的模块应该遵守CAN的接口约定，以让完整的程序work起来。<br>
 * 其中一方修改给另外一方的数据接口时，请联系对方沟通清楚。<br>
 *
 * 其中包含类有：<br>
 * 1. {@link com.uni.utils.CAN.DataBus}，用于UNICache和UNIEditor之间中传递数据<br>
 * 2. {@link com.uni.utils.CAN.UISwitcher}, 用于子UI模块通知Manager切换UI<br>
 * 3. {@link com.uni.utils.CAN.Package}, 定义了所有通信通道会用到的包的集合体<br>
 *
 * 建议：大量计算的工作请让订阅者在异步线程完成。
 */

public class CAN
{
    /**
     * 用于各个通道传递的消息。<br>
     * 通道双方使用哪一种包需要事先沟通好。<br>
     */
    public static class Package
    {
        /**
         * UNICache主动更新UNIEditor用到的包
         */
        public static class EditorUpdate
        {
            public SparseArray<Property> props;
            public SparseArray<Bitmap> images;
            public boolean hasNext;
        }

        /**
         * UNIEditor的简单请求，仅携带一个ID值。<br>
         * 请求包括：<br>
         * 1. 更新某一帧<br>
         * 2. 删除某一帧<br>
         * 3. 删除某一元素
         */
        public static class FrameRequest
        {
            public enum Type
            {
                Update, Add, Delete
            }

            public Type what;
            public Integer which;
        }

        /**
         * UNIEditor通知UNICache添加一个元素到某一帧，<br>
         * 该元素的ID和属性一并通知。<br>
         * 注意：Element不局限于UNIElement，也可以是UNIElement的Atom.
         */
        public static class ElementRequest
        {
            public enum Type
            {
                Update, Add, Delete
            }

            public Type what;
            public Integer where;
            public Integer which;
            public Property how;
            public Bitmap image;
        }
    }

    /**
     * 用于UNICache和UNIEditor之间中传递数据
     */
    public static class DataBus
    {
        /**
         * UNICache主动更新UNIEditor，只更新当前帧。<br>
         * 携带了帧中元素的数据，按ID索引<br>
         *
         * 用到的包：{@link Package.EditorUpdate}<br>
         * TODO: 目前还没有考虑帧本身的数据（间隔、持续时间、作者、哪些元素不可修改等）
         * @param props 帧中各个元素的属性
         * @param images 帧中各个元素的截图
         */
        public static void updateEditor(SparseArray<Property> props, SparseArray<Bitmap> images, boolean hasNext)
        {
            Package.EditorUpdate pkg = new Package.EditorUpdate();
            pkg.props = props;
            pkg.images = images;
            pkg.hasNext = hasNext;
            EventBus.getDefault().post(pkg);
        }

        /**
         * UNIEditor要求UNICache返回指定帧的全部数据<br>
         *
         * 用到的包：{@link Package.FrameRequest}
         * @param frameID
         */
        public static void requireUpdate(Integer frameID)
        {
            Package.FrameRequest pkg = new Package.FrameRequest();
            pkg.what = Package.FrameRequest.Type.Update;
            pkg.which = frameID;
            EventBus.getDefault().post(pkg);
        }

        /**
         * 为当前帧序列中添加或插入帧<br>
         *
         * 用到的包：{@link Package.FrameRequest}<br>
         * @param where 插入的位置，位置从0开始（该位置的帧将会往后挪，如：1,2,3, 在第1位插入4：1,4,2,3）
         */
        public static void addFrame(Integer where)
        {
            Package.FrameRequest pkg = new Package.FrameRequest();
            pkg.what = Package.FrameRequest.Type.Add;
            pkg.which = where;
            EventBus.getDefault().post(pkg);
        }

        /**
         * UNIEditor要求UNICache删除指定帧。<br>
         * 建议：如果删除的是当前帧，UNICache自动调用{@link DataBus#updateEditor(SparseArray, SparseArray, boolean)}，<br>
         * 或者UNIEditor调用{@link DataBus#requireUpdate(Integer)}。<br>
         *
         * 用到的包：{@link Package.FrameRequest}
         * @param frameID
         */
        public static void deleteFrame(Integer frameID)
        {
            Package.FrameRequest pkg = new Package.FrameRequest();
            pkg.what = Package.FrameRequest.Type.Delete;
            pkg.which = frameID;
            EventBus.getDefault().post(pkg);
        }

        /**
         * UNIEditor要求UNICache删除指定元素<br>
         *
         * 用到的包：{@link Package.ElementRequest}
         * @param frameID
         */
        public static void deleteElement(Integer frameID, Integer elementID)
        {
            Package.ElementRequest pkg = new Package.ElementRequest();
            pkg.what = Package.ElementRequest.Type.Delete;
            pkg.where = frameID;
            pkg.which = elementID;
            EventBus.getDefault().post(pkg);
        }


        /**
         * UNIEditor通知UNICache添加元素<br>
         *
         * 用到的包：{@link Package.ElementRequest}
         * @param frameID 元素所在的帧的ID（请不要超过当前帧的数量，或者注意及时更新！）
         * @param prop 元素在该帧的属性
         * @param image 元素的缩略图
         */
        public static void addElement(Integer frameID, Property prop, Bitmap image)
        {
            Package.ElementRequest pkg = new Package.ElementRequest();
            pkg.what = Package.ElementRequest.Type.Add;
            pkg.where = frameID;
            pkg.which = prop.ID;
            pkg.how = prop;
            pkg.image = image;
            EventBus.getDefault().post(pkg);
        }


        public static void updateElement(Integer frameID, Property prop)
        {
            Package.ElementRequest pkg = new Package.ElementRequest();
            pkg.what = Package.ElementRequest.Type.Update;
            pkg.where = frameID;
            pkg.which = prop.ID;
            pkg.how = prop;
            EventBus.getDefault().post(pkg);
        }
    }

    public static class Control
    {

    }

    /**
     *
     */
    public static class UISwitcher
    {

    }

    public static void login(Object who)
    {
        EventBus.getDefault().register(who);
    }

    public static void logout(Object who)
    {
        EventBus.getDefault().unregister(who);
    }
}
