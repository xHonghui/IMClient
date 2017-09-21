package cn.itcast.im.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import cn.itcast.im.R;
import cn.itcast.im.ui.activity.ChatActivity;
import cn.itcast.im.ui.adapter.ConversationAdapter;

/**
 * @author WJQ
 */
public class MainFragment01 extends BaseFragment {

    private ListView listView;
    private ConversationAdapter mAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.main_fragment_01;
    }

    @Override
    public void initView() {
        listView = findView(R.id.lv_conversation);
        mAdapter = new ConversationAdapter(mActivity, null);
        listView.setAdapter(mAdapter);
    }

    @Override
    public void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击的列表项javabean
                EMConversation bean = mAdapter.getItem(position);
                String username = bean.getUserName();

                Intent intent = new Intent(mActivity, ChatActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        loadConversationDatas();
    }

    /** 加载所有的会话 */
    public void loadConversationDatas() {
        Map<String, EMConversation> conversations =
                EMClient.getInstance().chatManager().getAllConversations();

        // 列表显示的数据集合
        ArrayList<EMConversation> listData = new ArrayList<>();

        // 过滤没有消息的会话
        Collection<EMConversation> values = conversations.values();
        Iterator<EMConversation> iterator = values.iterator();
        while (iterator.hasNext()) {
            // 取出集合中的一个EMConversation对象
            EMConversation bean = iterator.next();
            // 会话中至少有一条消息，才显示在列表上
            if (bean.getAllMsgCount() > 0) {
                listData.add(bean);
            }
        }

        // 按会话最后一条消息发送时间的降序排列
        Collections.sort(listData, new Comparator<EMConversation>() {
            @Override
            public int compare(EMConversation o1, EMConversation o2) {
                Long left = o1.getLastMessage().getMsgTime();
                Long right = o2.getLastMessage().getMsgTime();
                return -left.compareTo(right);
            }
        });
        // showToast("会话个数：" + listData.size());

        if (mAdapter == null)
            mAdapter = new ConversationAdapter(mActivity, null);
        // 刷新列表会话数据显示
        mAdapter.setDatas(listData);
    }

    @Override
    public void onClick(View v, int id) {

    }
}
