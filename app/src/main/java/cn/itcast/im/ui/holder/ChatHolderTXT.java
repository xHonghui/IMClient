package cn.itcast.im.ui.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;

import cn.itcast.im.R;
import cn.itcast.im.base.Global;
import cn.itcast.im.ui.adapter.MyBaseAdapter;
import cn.itcast.im.util.Utils;

/**
 * @author WJQ
 */
public class ChatHolderTXT extends BaseHolder<EMMessage> {

    // 接收消息列表项中的子控件
//    private TextView timestamp;
//    private ImageView ivUserhead;
//    private RelativeLayout bubble;
//    private TextView tvChatcontent;
    private TextView tvUserid;

    // 发送消息列表项中的子控件
    private TextView timestamp;
    private ImageView ivUserhead;
    private RelativeLayout bubble;
    private TextView tvChatcontent;
    private ImageView ivFail;
    private TextView tvAck;
    private TextView tvDelivered;
    private ProgressBar progressBar;


    public ChatHolderTXT(Context context, ViewGroup parent,
                         MyBaseAdapter<EMMessage> adapter,
                         int position, EMMessage bean) {
        super(context, parent, adapter, position, bean);
    }

    // 创建列表item布局, 并查找子控件
    @Override
    public View onCreateView(Context context, ViewGroup parent) {
        int layoutRes = 0;
        if (super.bean.direct() == EMMessage.Direct.RECEIVE) {
            // 接收到的消息
            layoutRes = R.layout.chat_row_received_message;
        } else {
            // 发送出去的消息
            layoutRes = R.layout.chat_row_sent_message;
        }

        View item = Global.inflate(layoutRes, parent);
        initView(item);
        return item;
    }

    private void initView(View item) {
        // 接收消息列表项中的子控件
//        timestamp = (TextView) item.findViewById(R.id.timestamp);
//        ivUserhead = (ImageView) item.findViewById(R.id.iv_userhead);
//        bubble = (RelativeLayout) item.findViewById(R.id.bubble);
//        tvChatcontent = (TextView) item.findViewById(R.id.tv_chatcontent);
        tvUserid = (TextView) item.findViewById(R.id.tv_userid);

        // 发送消息列表项中的子控件
        timestamp = (TextView) item.findViewById(R.id.timestamp);
        ivUserhead = (ImageView) item.findViewById(R.id.iv_userhead);
        bubble = (RelativeLayout) item.findViewById(R.id.bubble);
        tvChatcontent = (TextView) item.findViewById(R.id.tv_chatcontent);
        ivFail = (ImageView) item.findViewById(R.id.msg_status);
        tvAck = (TextView) item.findViewById(R.id.tv_ack);
        tvDelivered = (TextView) item.findViewById(R.id.tv_delivered);
        progressBar = (ProgressBar) item.findViewById(R.id.progress_bar);

        // 发送失败图标的点击事件
        if (ivFail != null) {   // 发送的消息才需要点击事件
            ivFail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 点击图标重新发送消息

                    // 修改消息的状态为刚刚创建
                    bean.setStatus(EMMessage.Status.CREATE);
                    // 重新发送消息
                    EMClient.getInstance().chatManager().sendMessage(bean);
                    // 重新刷新列表显示
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    // 刷新列表项子控件显示
    @Override
    protected void onRefreshView(EMMessage bean, int position) {
        // 显示时间
        timestamp.setText(Utils.formatDate(bean.getMsgTime()));

        // 显示消息内容
        EMTextMessageBody body = (EMTextMessageBody) bean.getBody();
        tvChatcontent.setText(body.getMessage());

        if (bean.direct() == EMMessage.Direct.SEND) {   // 发送的消息

            // 显示或隐藏送达回执
            showDeliveryAckView(bean);
            // 显示或隐藏已读回执
            showReadAckView(bean);
            // 监听消息发送状态(正在发送，发成成功，失败..)
            bean.setMessageStatusCallback(mEMCallback);
            // 更新消息的发送状态
            updateMessageStatus(bean);

        } else {    // 接收到的消息
            // 通知服务器某一条消息已经读取
            if (!bean.isAcked()) {  // 未读取才需要通知服务器
                // to	接收方的用户名
                // messageId	消息的ID
                try {
                    EMClient.getInstance().chatManager()
                            .ackMessageRead(bean.getTo(), bean.getMsgId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /** 更新消息的发送状态*/
    private void updateMessageStatus(EMMessage bean) {
        switch (bean.status()) {
            case CREATE:            // 消息刚创建
                progressBar.setVisibility(View.VISIBLE);
                ivFail.setVisibility(View.GONE);
                break;
            case INPROGRESS:        // 消息正在发送
                progressBar.setVisibility(View.VISIBLE);
                ivFail.setVisibility(View.GONE);
                break;
            case SUCCESS:           // 消息发送成功
                progressBar.setVisibility(View.GONE);
                ivFail.setVisibility(View.GONE);
                break;
            case FAIL:              // 消息发送失败
                progressBar.setVisibility(View.GONE);
                ivFail.setVisibility(View.VISIBLE);
                break;
        }
    }

    // 监听消息发送状态(正在发送，发成成功，失败..)
    EMCallBack mEMCallback = new EMCallBack() {

        @Override  // 消息发送成功， 子线程调用
        public void onSuccess() {
            Global.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    // 重新刷新列表显示
                    updateListView();
                }
            });
        }

        @Override  // 消息发送失败
        public void onError(int i, String s) {
            Global.getMainHandler().post(new Runnable() {
                @Override
                public void run() {
                    // 重新刷新列表显示
                    updateListView();
                }
            });
        }

        @Override
        public void onProgress(int progress, String s) {

        }
    };

    // 重新刷新列表显示
    private void updateListView() {
        // 消息发送失败
        if (super.bean.status() == EMMessage.Status.FAIL) {
            Global.showToast("消息发送失败");
        }
        super.adapter.notifyDataSetChanged();
    }

    /** 显示或隐藏已读回执*/
    private void showReadAckView(EMMessage bean) {
        if (bean.isAcked()) {   // 已读
            tvAck.setVisibility(View.VISIBLE);
            tvDelivered.setVisibility(View.GONE);
        } else {
            tvAck.setVisibility(View.GONE);
        }
    }

    /** 显示或隐藏送达回执 */
    private void showDeliveryAckView(EMMessage bean) {
        if (bean.isDelivered()) {   // 送达
            tvDelivered.setVisibility(View.VISIBLE);
        } else {
            tvDelivered.setVisibility(View.GONE);
        }
    }
}













