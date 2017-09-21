package cn.itcast.im.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;

import java.util.List;

import cn.itcast.im.R;
import cn.itcast.im.base.Global;
import cn.itcast.im.ui.adapter.ChatAdapter;

import static com.hyphenate.easeui.base.EaseConstant.CHATTYPE_GROUP;

/**
 * 聊天界面
 *
 * @author WJQ
 */
public class ChatActivity extends BaseActivity {

    /** 聊天对象 */
    private String toUsername;
    private ListView listView;
    private ChatAdapter mAdapter;
    private List<EMMessage> listData;
    private EMConversation conversation;
    private EditText etInput;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public int getLayoutRes() {
        return R.layout.activity_chat;
    }

    @Override
    public void initView() {
        etInput = findView(R.id.et_chatting_input);
        listView = findView(R.id.lv_chatting);
        swipeRefreshLayout = findView(R.id.chat_swipe_layout);
        mAdapter = new ChatAdapter(this, null);
        listView.setAdapter(mAdapter);
    }

    /** 是否正在下拉刷新 */
    private boolean isRefreshing = false;

    @Override
    public void initListener() {
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isRefreshing) {
                    isRefreshing = true;
                    // 下拉刷新监听, 下拉时加载更多的消息
                    // 列表中有消息，则获取第一条消息的id,
                    // 以它为参考，往上加载更多的10条消息
                    if (listData != null && listData.size() > 0) {
                        EMMessage firstMsg = listData.get(0);
                        // 加载更多的消息，从数据库加载10条消息到内存中
                        List<EMMessage> list = conversation.loadMoreMsgFromDB
                                (firstMsg.getMsgId(), 10);

                        // 还有更多消息,重新显示列表
                        if (list != null && list.size() > 0) {
                            // 列表显示的数据
                            listData = conversation.getAllMessages();
                            // 刷新列表显示
                            mAdapter.setDatas(listData);
                            // 隐藏下拉刷新控件
                            swipeRefreshLayout.setRefreshing(false);
                            isRefreshing = false;

                        } else {
                            showToast("没有更多消息了");
                            // 隐藏下拉刷新控件
                            swipeRefreshLayout.setRefreshing(false);
                            isRefreshing = false;
                        }
                    }
                }
            }
        });
    }

    @Override
    public void initData() {
        getIntentData();
        loadConversationMsgs();
    }

    /** 加载会话中所有的消息 */
    private void loadConversationMsgs() {
        // 单聊
        // true表示如果EMConversation对象不存在，会自动创建
        conversation = EMClient.getInstance()
                .chatManager().getConversation(toUsername,
                        EMConversation.EMConversationType.Chat,   // 单聊
                        true);

        // 获取此会话的所有消息
        listData = conversation.getAllMessages();
        // 有加载到消息
        if (listData != null && listData.size() > 0) {
            EMMessage firstMsg = listData.get(0);
            // 从数据库中，再加载9条消息到内存中
            conversation.loadMoreMsgFromDB(firstMsg.getMsgId(), 9);
        }
        listData = conversation.getAllMessages();
        // 把会话中所有的消息置为已读状态
        conversation.markAllMessagesAsRead();

        // showToast("消息条数：" + ((messages == null) ? 0 : messages.size()));
        // 显示列表数据
        mAdapter.setDatas(listData);
        // 列表自动滚动到底部
        listView.setSelection(mAdapter.getCount() - 1);
    }

    /** 重新加载会话消息，并刷新列表显示 */
    private void reloadConversationMsgs() {
        // 获取此会话的所有消息
        listData = conversation.getAllMessages();
        // 把会话中所有的消息置为已读状态
        conversation.markAllMessagesAsRead();
        // 显示列表数据
        mAdapter.setDatas(listData);
        // 列表自动滚动到底部
        scrollToBottom();
    }

    /** 列表自动滚动到底部 */
    private void scrollToBottom() {
        Global.getMainHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                listView.setSelection(mAdapter.getCount() - 1);
            }
        }, 100);
    }

    private void getIntentData() {
        toUsername = getIntent().getStringExtra("username");
        super.setPageTitle(toUsername);
    }

    @Override
    public void onClick(View v, int id) {
        if (id == R.id.btn_send) {
            // 发送文本消息
            sendMsgTXT();
        }
    }

    /** 发送文本消息*/
    private void sendMsgTXT() {
        String content = etInput.getText().toString().trim();
        //创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, toUsername);

        //如果是群聊，设置chattype，默认是单聊
        //if (chatType == CHATTYPE_GROUP)
        //    message.setChatType(ChatType.GroupChat);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
        // 清空编辑框
        etInput.setText("");
        // 重新加载列表数据
        reloadConversationMsgs();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 监听来了新消息
        EMClient.getInstance().chatManager().addMessageListener(mMessageListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 记得在不需要的时候移除listener，如在activity的onDestroy()时
        EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
    }

    EMMessageListener mMessageListener = new EMMessageListener() {
        // 收到消息
        // 子线程回调
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            // 刷新列表和未读消息总条数
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    reloadConversationMsgs();
                }
            });
        }

        @Override
        public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            // 收到已送达回执
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 刷新列表显示 -> getView() -> mHolder.onRefreshView();
                    // -> 在onRefreshView方法去显示或隐藏送达回执
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onMessageReadAckReceived(List<EMMessage> messages) {
            // 收到已读回执
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 刷新列表显示 -> getView() -> mHolder.onRefreshView();
                    // -> 在onRefreshView方法去显示或隐藏已读回执
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {
            //收到透传消息
        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {
            //消息状态变动
        }
    };
}
