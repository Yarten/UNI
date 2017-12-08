package com.uni.utils;

import android.graphics.Bitmap;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.Map;

/**
 * Created by yarten on 2017/12/7.
 * 模块局域网
 * 对EventBus作了封装，用于各个模块间的数据交换和相互调用。
 * 涉及到的模块应该遵守CAN的接口约定，以让完整的程序work起来。
 * 其中一方修改给另外一方的数据接口时，请联系对方沟通清楚。
 *
 * 其中包含类有：
 * 1. {@link com.uni.utils.CAN.DataBus}，用于UNICache和UNIEditor之间中传递数据
 * 2. {@link com.uni.utils.CAN.UISwitcher}, 用于子UI模块通知Manager切换UI
 * 3. {@link com.uni.utils.CAN.Package}, 定义了所有通信通道会用到的包的集合体
 *
 * 建议：大量计算的工作请让订阅者在异步线程完成。
 */

public class CAN
{
    /**
     * 用于各个通道传递的消息。
     * 通道双方使用哪一种包需要事先沟通好。
     */
    public static class Package
    {
        /**
         * UNICache主动更新UNIEditor用到的包
         */
        public static class EditorUpdate
        {
            public Map<Integer, Property> props;
            public Map<Integer, Bitmap> images;
        }

        /**
         * UNIEditor的简单请求，仅携带一个ID值。
         * 请求包括：
         * 1. 更新某一帧
         * 2. 删除某一帧
         * 3. 删除某一元素
         */
        public static class EditorRequire
        {
            public enum Type
            {
                Update, DeleteFrame, DeleteElement, AddFrame
            }

            public Type what;
            public Integer which;
        }

        /**
         * UNIEditor通知UNICache添加一个元素到某一帧，
         * 该元素的ID和属性一并通知。
         * 注意：Element不局限于UNIElement，也可以是UNIElement的Atom.
         */
        public static class AddElement
        {
            public Integer where;
            public Integer ID;
            public Property prop;
        }
    }

    /**
     * 用于UNICache和UNIEditor之间中传递数据
     */
    public static class DataBus
    {
        //region UNICache和UNIEditor

        /**
         * UNICache主动更新UNIEditor，只更新当前帧
         * 携带了帧中元素的数据，按ID索引
         * @param props 帧中各个元素的属性
         * @param images 帧中各个元素的截图
         * TODO: 目前还没有考虑帧本身的数据（间隔、持续时间、作者、哪些元素不可修改等）
         *
         * 用到的包：{@link Package.EditorUpdate}
         */
        public static void updateEditor(Map<Integer, Property> props, Map<Integer, Bitmap> images)
        {
            Package.EditorUpdate pkg = new Package.EditorUpdate();
            pkg.props = props;
            pkg.images = images;
            EventBus.getDefault().post(pkg);
        }

        /**
         * UNIEditor要求UNICache返回指定帧的全部数据
         * @param frameID
         *
         * 用到的包：{@link Package.EditorRequire}
         */
        public static void requireUpdate(Integer frameID)
        {
            Package.EditorRequire pkg = new Package.EditorRequire();
            pkg.what = Package.EditorRequire.Type.Update;
            pkg.which = frameID;
            EventBus.getDefault().post(pkg);
        }

        /**
         * UNIEditor要求UNICache删除指定帧。
         * 建议：如果删除的是当前帧，UNICache自动调用{@link DataBus#updateEditor(Map, Map)}，
         * 或者UNIEditor调用{@link DataBus#requireUpdate(Integer)}。
         * @param frameID
         *
         * 用到的包：{@link Package.EditorRequire}
         */
        public static void deleteFrame(Integer frameID)
        {
            Package.EditorRequire pkg = new Package.EditorRequire();
            pkg.what = Package.EditorRequire.Type.DeleteFrame;
            pkg.which = frameID;
            EventBus.getDefault().post(pkg);
        }

        /**
         * UNIEditor要求UNICache删除指定元素
         * @param frameID
         */
        public static void deleteElement(Integer frameID)
        {
            Package.EditorRequire pkg = new Package.EditorRequire();
            pkg.what = Package.EditorRequire.Type.DeleteElement;
            pkg.which = frameID;
            EventBus.getDefault().post(pkg);
        }

        /**
         * 为当前帧序列中添加或插入帧
         * @param where 插入的位置，位置从0开始（该位置的帧将会往后挪，如：1,2,3, 在第1位插入4：1,4,2,3）
         */
        public static void addFrame(Integer where)
        {
            Package.EditorRequire pkg = new Package.EditorRequire();
            pkg.what = Package.EditorRequire.Type.AddFrame;
            pkg.which = where;
            EventBus.getDefault().post(pkg);
        }

        /**
         * UNIEditor通知UNICache添加元素
         * @param frameID 元素所在的帧的ID（请不要超过当前帧的数量，或者注意及时更新！）
         * @param elementID 元素本身的索引ID
         * @param prop 元素在该帧的属性
         */
        public static void addElement(Integer frameID, Integer elementID, Property prop)
        {
            Package.AddElement pkg = new Package.AddElement();
            pkg.where = frameID;
            pkg.ID = elementID;
            pkg.prop = prop;
            EventBus.getDefault().post(pkg);
        }



        //endregion
    }

    /**
     *
     */
    public static class UISwitcher
    {

    }
}
