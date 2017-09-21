package cn.itcast.im.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.db.UserDao;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import cn.itcast.im.R;
import cn.itcast.im.base.Const;
import cn.itcast.im.ui.activity.ChatActivity;
import cn.itcast.im.ui.adapter.ContactAdapter;
import de.greenrobot.event.EventBus;

/**
 * @author WJQ
 */

public class MainFragment02 extends BaseFragment {

    // 操作好友的dao
    private UserDao userDao;

    private ListView listView;

    /** 列表显示的所有的联系人 */
    private ArrayList<EaseUser> listData;
    private ContactAdapter contactAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.main_fragment_02;
    }

    @Override
    public void initView() {
        listView = findView(R.id.list_view);
        contactAdapter = new ContactAdapter(mActivity, null);
        listView.setAdapter(contactAdapter);
    }

    @Override
    public void initListener() {
        // （1）显示上下文菜单
        listView.setOnCreateContextMenuListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 点击的列表项javabean
                EaseUser bean = contactAdapter.getItem(position);
                String username = bean.getUsername();

                Intent intent = new Intent(mActivity, ChatActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        userDao = new UserDao(mActivity);

        loadContact();
    }

    // 加载好友数据
    private void loadContact() {
        new Thread() {
            @Override
            public void run() {
                try {
                    // 获取服务器好友
                    List<String> usernames = EMClient.getInstance()
                            .contactManager().getAllContactsFromServer();

                    // 把好友数据保存到本地数据库
                    ArrayList<EaseUser> users = new ArrayList<EaseUser>();
                    for (String username : usernames) {
                        EaseUser user = new EaseUser(username);
                        users.add(user);
                    }
                    // 保存到数据库
                    userDao.saveContactList(users);
                    listData = users;

                } catch (Exception e) {
                    e.printStackTrace();

                    // 从服务器获取好友失败，读取本地数据库缓存数据
                    Map<String, EaseUser> map = userDao.getContactList();
                    listData = new ArrayList<EaseUser>(map.values());
                }

                // 排序操作: 用户名升序
                sortContacts();
                showToast("好友个数：" + listData.size());

                // 刷新列表数据显示
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        contactAdapter.setDatas(listData);
                    }
                });
            }
        }.start();
    }

    // 排序操作: 用户名升序
    private void sortContacts() {
        Collections.sort(listData, new Comparator<EaseUser>() {
            @Override
            public int compare(EaseUser left, EaseUser right) {
                return left.getUsername().compareTo(right.getUsername());
            }
        });
    }

    @Override
    public void onClick(View v, int id) {
    }

    //==============上下文菜单=(begin)================
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        // 创建上下文菜单项
        getActivity().getMenuInflater().inflate(R.menu.contact_list, menu);
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // 点击删除联系人菜单
        if (R.id.delete_contact == item.getItemId()) {
            AdapterView.AdapterContextMenuInfo info =
                    (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // 长按的列表项的位置
            int position = info.position;
            // 要删除的好友对象
            EaseUser easeUser = listData.get(position);
            // 删除好友
            deleteContact(easeUser);

        }
        return super.onOptionsItemSelected(item);
    }

    /** 删除好友 */
    private void deleteContact(final EaseUser user) {
        final String username = user.getUsername();
        new Thread() {
            @Override
            public void run() {
                try {
                    // 1. 从服务器中删除
                    EMClient.getInstance().contactManager().deleteContact(username);

                    // 2. 从数据库删除
                    userDao.deleteContact(username);

                    // 3. 从列表中删除
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            contactAdapter.remove(user);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //==============上下文菜单=(end)================

    //==============监听联系人变化（begin）===================


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    // 接收EventBus事件的方法
    public void onEventMainThread(Message msg) {
        if (msg.what == Const.TYPE_ADD_CONTACT)  {
            String username = (String) msg.obj;
            showToast("添加了好友：" + username);
            reloadContactFromDB();
            return;
        }

        if (msg.what == Const.TYPE_DEL_CONTACT)  {
            String username = (String) msg.obj;
            showToast("删除了好友：" + username);
            reloadContactFromDB();
            return;
        }
    }

    private void reloadContactFromDB() {
        // 读取本地数据库缓存数据
        Map<String, EaseUser> map = userDao.getContactList();
        listData = new ArrayList<EaseUser>(map.values());
        // 排序操作: 用户名升序
        sortContacts();
        // 刷新列表数据显示
        contactAdapter.setDatas(listData);
    }

    //==============监听联系人变化（end）===================
}
