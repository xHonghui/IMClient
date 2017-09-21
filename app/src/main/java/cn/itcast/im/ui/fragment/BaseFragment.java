package cn.itcast.im.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.itcast.im.R;
import cn.itcast.im.base.Global;
import cn.itcast.im.interfaces.IUIOperation;
import cn.itcast.im.ui.activity.BaseActivity;
import cn.itcast.im.util.Utils;

public abstract class BaseFragment extends Fragment implements IUIOperation {
	
	/** 管理Fragment的Activity */
	public BaseActivity mActivity;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		mActivity = (BaseActivity)getActivity();
	}
	
	/** Fragment显示的布局 */
	public View mRoot;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		if (mRoot == null) {
			mRoot = Global.inflate(getLayoutRes(), container);
			
			// 查找布局中的所有的button(ImageButton),并设置点击事件
			Utils.findButtonAndSetOnClickListener(mRoot, this);
			
			initView();
			initListener();
			initData();
			
		} else {
			// 解除mRoot与其父控件的关系
			unbindWidthParent(mRoot);
		}
		
		return mRoot;
	}

	/**
	 * 解除父子控件关系
	 * 
	 * @param view
	 */
	public void unbindWidthParent(View view) {
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
	}

	/** 查找子控件，可以省略强转 */
	public <T> T findView(int id) {
		@SuppressWarnings("unchecked")
		T view = (T) mRoot.findViewById(id);
		return view;
	}

	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		
		if (id == R.id.btn_back) {// 点击了标题栏左上角的返回按钮
			mActivity.finish();
			return ;
		}
		
		onClick(v, id);
	}
	
	public void showToast(String text) {
		Global.showToast(text);
	}
}

