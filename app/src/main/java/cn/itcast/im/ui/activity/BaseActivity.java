package cn.itcast.im.ui.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import cn.itcast.im.R;
import cn.itcast.im.base.Global;
import cn.itcast.im.interfaces.IUIOperation;
import cn.itcast.im.util.Utils;

public abstract class BaseActivity extends FragmentActivity implements IUIOperation {

	/** 标题 */
	private TextView tvTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(getLayoutRes());
		
		// 系统的一个根布局，可以查找到activity布局的所有的子控件
		View root = findViewById(android.R.id.content);
		
		// 查找activity布局中所有的Button（ImageButton），并设置点击事件
		Utils.findButtonAndSetOnClickListener(root, this);
		
		tvTitle = findView(R.id.tv_title);
		
		initView();
		initListener();
		initData();
	}
	
	/**
	 * 设置界面标题
	 * @param title
	 */
	public void setPageTitle(String title) {
		if (tvTitle != null) {
			tvTitle.setText(title);
		}
	}

	/** 查找子控件，可以省略强转 */
	public <T> T findView(int id) {
		@SuppressWarnings("unchecked")
		T view = (T) findViewById(id);
		return view;
	}
	
	@Override
	public void onClick(View v) {
		// 如果标题栏左上角的返回按钮
		if (v.getId() == R.id.btn_back) {
			finish();   // 退出当前界面
			return;
		}
		onClick(v, v.getId());
	}
	
	public void showToast(String text) {
		Global.showToast(text);
	}

	
	private ProgressDialog mPDialog;

	/**
	 * 显示加载提示框(不能在子线程调用)
	 */
	public void showProgressDialog(String message) {
		mPDialog = new ProgressDialog(this);
		mPDialog.setMessage(message);
		// 点击外部时不销毁
		mPDialog.setCanceledOnTouchOutside(false);

		// activity如果正在销毁或已销毁，不能show Dialog，否则出错。
		if (!isFinishing())
			mPDialog.show();
	}

	/**
	 * 销毁加载提示框
	 */
	public void dismissProgressDialog() {
		if (mPDialog != null) {
			mPDialog.dismiss();
			mPDialog = null;
		}
	}
	
	/**
	 * 隐藏左上角的返回按钮
	 */
	protected void hideBtnBack() {
		View btnBack = findViewById(R.id.btn_back);
		if (btnBack != null)
			btnBack.setVisibility(View.GONE);
	}
	
	/**
	 * 显示标题栏右边的按钮
	 */
	protected void showRightBtn() {
		View btnRight = findViewById(R.id.title_bar_btn_right);
		if (btnRight != null)
			btnRight.setVisibility(View.VISIBLE);
	}
}



















