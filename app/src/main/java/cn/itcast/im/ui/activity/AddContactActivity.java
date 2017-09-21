package cn.itcast.im.ui.activity;

import com.hyphenate.chat.EMClient;

import android.view.View;
import cn.itcast.im.R;
import cn.itcast.im.ui.view.EditLayout;

/**
 * 真正做项目时应该是先进行搜索，再添加，环信sdk不支持搜索用户
 */
public class AddContactActivity extends BaseActivity {
	
	private EditLayout mELUsername;
	
	@Override
	public int getLayoutRes() {
		return R.layout.activity_add_contact;
	}

	@Override
	public void initView() {
		setPageTitle("添加联系人");
		
		mELUsername = (EditLayout) findViewById(R.id.el_username);
		mELUsername.setHint("请输入要添加的用户名");
	}

	@Override
	public void initListener() {
	}

	@Override
	public void initData() {
	}

	@Override
	public void onClick(View v, int id) {
		if (id == R.id.btn_add_friend) {	// 添加朋友
			addUser();
			return;
		}
	}
	
	private void addUser() {
		showProgressDialog("正在发送请求...");
		final String toAddUsername = mELUsername.getText();
		
		new Thread(new Runnable() {
			public void run() {
				try {
					// 备注信息，实际应该让用户手动填入
					String reason = "我是王某某";
					// 请求添加为好友，等待对方确认
					EMClient.getInstance().contactManager().addContact(toAddUsername, reason);
					showToast("发送请求成功，等待对方确认");
				} catch (final Exception e) {
					showToast("请求添加好友失败：" + e.getMessage());
				} finally {
					dismissProgressDialog();
				}
			}
		}).start();
	}
}
