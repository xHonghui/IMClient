package cn.itcast.im.ui.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.util.EaseCommonUtils;

import cn.itcast.im.R;
import cn.itcast.im.base.Global;
import cn.itcast.im.ui.adapter.MyBaseAdapter;
import cn.itcast.im.util.Utils;

/**
 * @author WJQ
 */
public class ConversationHolder extends BaseHolder<EMConversation> {

    private LinearLayout listItlayout;
    private RelativeLayout avatarContainer;
    private ImageView ivAvatar;
    private TextView tvUnreadMsgNumber;
    private TextView tvUsername;
    private TextView tvMessage;
    private TextView tvTime;

    public ConversationHolder(Context context, ViewGroup parent,
                              MyBaseAdapter<EMConversation> adapter,
                              int position, EMConversation bean) {
        super(context, parent, adapter, position, bean);
    }

    @Override
    public View onCreateView(Context context, ViewGroup parent) {
        View item = Global.inflate(R.layout.item_conversation, parent);
        initView(item);
        return item;
    }

    // 列表项子控件显示 (只要getView都会回调此方法)
    @Override
    protected void onRefreshView(EMConversation bean, int position) {
        // 显示用户名
        tvUsername.setText(bean.getUserName());
        // 显示时间
        String timeStr = Utils.formatDate(bean.getLastMessage().getMsgTime());
        tvTime.setText(timeStr);

        // 显示消息内容
        // 显示会话中最近的一条消息
        tvMessage.setText(EaseCommonUtils.getMessageDigest(bean.getLastMessage(), context));

        // 显示未读消息总条数
        if (bean.getUnreadMsgCount() > 0) {
            tvUnreadMsgNumber.setVisibility(View.VISIBLE);
            tvUnreadMsgNumber.setText(bean.getUnreadMsgCount() + "");
        } else {
            tvUnreadMsgNumber.setVisibility(View.GONE);
        }
    }

    private void initView(View item) {
        listItlayout = (LinearLayout) item.findViewById(R.id.list_itlayout);
        avatarContainer = (RelativeLayout) item.findViewById(R.id.avatar_container);
        ivAvatar = (ImageView) item.findViewById(R.id.iv_avatar);
        tvUnreadMsgNumber = (TextView) item.findViewById(R.id.tv_unread_msg_number);
        tvUsername = (TextView) item.findViewById(R.id.tv_username);
        tvMessage = (TextView) item.findViewById(R.id.tv_message);
        tvTime = (TextView) item.findViewById(R.id.tv_time);
    }
}
