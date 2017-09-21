package cn.itcast.im.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.hyphenate.chat.EMMessage;

import java.util.List;

import cn.itcast.im.ui.holder.BaseHolder;
import cn.itcast.im.ui.holder.ChatHolderTXT;

/**
 * @author WJQ
 */
public class ChatAdapter extends MyBaseAdapter<EMMessage> {

    /** 文本消息：接收 */
    private static final int TYPE_TXT_RECV = 0;
    /** 文本消息：发送 */
    private static final int TYPE_TXT_SEND = 1;
    /** 图片消息：接收 */
    private static final int TYPE_IMG_RECV = 2;
    /** 图片消息：发送 */
    private static final int TYPE_IMG_SEND = 3;

    public ChatAdapter(Context context, List<EMMessage> listData) {
        super(context, listData);
    }

    // 返回不同类型的holder对象
    @Override
    public BaseHolder<EMMessage> createViewHolder(
            Context context, ViewGroup parent,
            EMMessage bean, int position) {

        int type = getItemViewType(position);
        if (type == TYPE_TXT_RECV || type == TYPE_TXT_SEND) {   // 文本消息
            return new ChatHolderTXT(context, parent, this, position, bean);
        }

//        if (type == TYPE_IMG_RECV || type == TYPE_IMG_SEND) {   // 图片消息
//            return new ChatHolderIMG(context, parent, this, position, bean);
//        }

        return null;
    }


    // 根据列表位置返回对应的item类型
    @Override
    public int getItemViewType(int position) {
        EMMessage bean = getItem(position);
        if (bean.getType() ==  EMMessage.Type.TXT) {    // 文本消息
            if (bean.direct() == EMMessage.Direct.RECEIVE) {
                return 0;       // 接收到的消息
            } else {
                return 1;       // 发送的消息
            }
        } else if (bean.getType() ==  EMMessage.Type.IMAGE) {   // 图片消息
            if (bean.direct() == EMMessage.Direct.RECEIVE) {
                return 2;       // 接收到的消息
            } else {
                return 3;       // 发送的消息
            }
        }
        return super.getItemViewType(position);
    }

    // 有多少种不同类型的列表项布局
    @Override
    public int getViewTypeCount() {
        return 4;
    }
}


















