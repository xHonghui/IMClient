package cn.itcast.im.ui.activity;

import android.content.Intent;
import android.os.SystemClock;
import android.view.View;

import com.hyphenate.chat.EMClient;

import cn.itcast.im.R;

/**
 * 启动界面
 */
public class SplashActivity extends BaseActivity {

	private void init() {
		new Thread() {
			public void run() {

				// 如果已经登录，直接进入主界面
				if (EMClient.getInstance().isLoggedInBefore()) {
					// 预加载主界面会话数据
					EMClient.getInstance().chatManager().loadAllConversations();
					SystemClock.sleep(500);
					enterHomeActivity();
				} else {	// 进入登录界面
					SystemClock.sleep(1500);
					enterLoginActivity();
				}
			};
		}.start();
	}
	
	protected void enterLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	protected void enterHomeActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public int getLayoutRes() {
		return R.layout.activity_splash;
	}

	@Override
	public void initView() {
		init();
	}

	@Override
	public void initListener() {
	}

	@Override
	public void initData() {
	}

	@Override
	public void onClick(View v, int id) {
	}
}
