package cn.itcast.im.ui.activity;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import android.text.TextUtils;
import android.view.View;
import cn.itcast.im.R;
import cn.itcast.im.ui.view.EditLayout;

/**
 * 注册界面
 * 
 * @author WJQ
 */
public class RegisterActivity extends BaseActivity {

	private EditLayout elusername;
	private EditLayout elpassword;
	private EditLayout elpassword2;

	/**
	 * 点击了注册按钮
	 */
	private void onRegister() {
		String username = elusername.getText();
		String password = elpassword.getText();
		String password2 = elpassword2.getText();

		if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)
				|| TextUtils.isEmpty(password2)) {
			showToast("用户名或密码不能为空");
			return;
		}

		if (!password.equals(password2)) {
			showToast("两次输入的密码不一致");
			return;
		}

		register(username, password);
	}

	/**
	 * 请求接口进行注册
	 * 
	 * @param username
	 * @param password
	 */
	private void register(final String username, final String password) {
		showProgressDialog("正在注册...");

		new Thread() {
			
			@Override
			public void run() {
				try {
					// 注册：创建新账号
					EMClient.getInstance().createAccount(username, password);

					showToast("注册成功");

					// 模拟按下返回键，退出当前界面
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							onBackPressed();
						}
					});

				} catch (HyphenateException e) {
					e.printStackTrace();
					
					int errorCode = e.getErrorCode();
					if (errorCode == EMError.NETWORK_ERROR) {
						showToast("网络异常，请检查网络！");
					} else if (errorCode == EMError.USER_ALREADY_EXIST) {
						showToast("用户已存在！");
					} else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
						showToast("注册失败，无权限！");
					} else {
						showToast("注册失败: " + e.getMessage());
					}
					
				} finally {
					dismissProgressDialog();
				}
			}
		}.start();
	}


	/**
	 * 按下返回键退出当前界面
	 */
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		animWhenExit();
	}

	// 动画切换返回到登录界面
	private void animWhenExit() {
		overridePendingTransition(R.anim.alpha_unchanged,
				R.anim.push_bottom_out);
	}

	@Override
	public int getLayoutRes() {
		return R.layout.activity_register;
	}

	@Override
	public void initView() {
		setPageTitle("注册");

		this.elpassword2 = (EditLayout) findViewById(R.id.el_password2);
		this.elpassword = (EditLayout) findViewById(R.id.el_password);
		this.elusername = (EditLayout) findViewById(R.id.el_username);

		this.elusername.setHint("请输入用户名");
		this.elpassword.setHint("请输入密码");
		this.elpassword2.setHint("请再次输入密码");
		this.elpassword.setPasswordStyle();
		this.elpassword2.setPasswordStyle();
	}

	@Override
	public void initListener() {
	}

	@Override
	public void initData() {
	}

	@Override
	public void onClick(View v, int id) {
		if (id == R.id.btn_register) {
			onRegister();
			return;
		}
	}
}
