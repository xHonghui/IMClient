package cn.itcast.im.ui.activity;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.manager.PreferenceManager;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import cn.itcast.im.R;
import cn.itcast.im.ui.view.EditLayout;

/**
 * 登录界面
 */
public class LoginActivity extends BaseActivity {

	private EditLayout etaccount;
	private EditLayout etpassword;

	@Override
	public int getLayoutRes() {
		return R.layout.activity_login;
	}

	@Override
	public void initView() {
		super.hideBtnBack();
		super.setPageTitle("即信");
		
		this.etpassword = (EditLayout) findViewById(R.id.et_password);
		this.etaccount = (EditLayout) findViewById(R.id.et_account);

		this.etaccount.setHint("请输入用户名");
		this.etpassword.setHint("请输入密码");
		
		// 密码显示成*
		this.etpassword.setPasswordStyle();

		// 回显之前登录的账号
		String currentUsername = PreferenceManager.getInstance()
				.getCurrentUsername();
		
		this.etaccount.setText(currentUsername);
		// 隐藏清除按钮
		this.etaccount.hideClearBtn();
		// 获取光标
		this.etpassword.requestFocus();
		// 光标不显示
		this.etpassword.getEditText().setCursorVisible(false);
	}

	@Override
	public void initListener() {
	}

	@Override
	public void initData() {
	}

	@Override
	public void onClick(View v, int id) {
		if (id == R.id.ib_login) {
			onLogin();
			return;
		}

		if (id == R.id.btn_register) {
			onRegister();
			return;
		}
	}

	private void onLogin() {
		String username = etaccount.getText();
		String password = etpassword.getText();

		if (TextUtils.isEmpty(username)) {
			showToast("用户名不能为空");
			return;
		}

		if (TextUtils.isEmpty(password)) {
			showToast("密码不能为空");
			return;
		}

		login(username, password);
	}

	/**
	 * 请求接口进行登录
	 */
	private void login(final String username, String password) {
		showProgressDialog("正在登录");

		EMClient.getInstance().login(username,password,new EMCallBack() {//回调

			@Override
			public void onSuccess() {
				dismissProgressDialog();

				// 预加载主界面会话数据
				EMClient.getInstance().chatManager().loadAllConversations();
				// 使用SP保存登录成功的账号
				PreferenceManager.getInstance().setCurrentUserName(username);
				// 进入主界面
				enterHomeActivity();
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String message) {
				showToast("登录失败：" + message);
				dismissProgressDialog();
			}
		});
	}

	private void enterHomeActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 点击注册按钮进入注册界面
	 */
	private void onRegister() {
		Intent intent = new Intent(this, RegisterActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.push_bottom_in, R.anim.alpha_unchanged);
	}

}















