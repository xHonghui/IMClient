package cn.itcast.im.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.hyphenate.chat.EMConversation;

import java.util.List;

import cn.itcast.im.ui.holder.BaseHolder;
import cn.itcast.im.ui.holder.ConversationHolder;

/**
 * @author WJQ
 */
public class ConversationAdapter extends MyBaseAdapter<EMConversation> {

    public ConversationAdapter(Context context, List<EMConversation> listData) {
        super(context, listData);
    }

    @Override
    public BaseHolder createViewHolder(
            Context context, ViewGroup parent,
            EMConversation bean,int position) {
        return new ConversationHolder(context, parent, this, position, bean);
    }
}
