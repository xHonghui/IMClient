package cn.itcast.im.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import com.hyphenate.EMContactListener;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.db.UserDao;
import com.hyphenate.easeui.domain.EaseUser;

import java.util.ArrayList;
import java.util.List;

import cn.itcast.im.R;
import cn.itcast.im.base.Const;
import cn.itcast.im.base.Global;
import cn.itcast.im.ui.adapter.MainAdapter;
import cn.itcast.im.ui.fragment.MainFragment01;
import cn.itcast.im.ui.fragment.MainFragment02;
import cn.itcast.im.ui.fragment.MainFragment03;
import cn.itcast.im.ui.fragment.MainFragment04;
import cn.itcast.im.ui.view.GradientTab;
import de.greenrobot.event.EventBus;

/**
 * 主界面
 */
public class MainActivity extends BaseActivity {

	private int[] icons = new int[] {
			R.drawable.icon_tab_1,
			R.drawable.icon_tab_2,
			R.drawable.icon_tab_3,
			R.drawable.icon_tab_4,
	};

	private String[] titles = new String[] {
			"消息", "联系人", "发现", "我"};

	private GradientTab[] tabs = new GradientTab[4];

	/** 当前选中的选项卡 */
	private GradientTab mCurrentTab;


	private LinearLayout llTabLayout;
	private ViewPager viewPager;
	private MainFragment01 fragmentConversation;

	@Override
	public int getLayoutRes() {
		return R.layout.activity_main;
	}

	@Override
	public void initView() {
		super.hideBtnBack();		// 隐藏左边的按钮
		super.showRightBtn();		// 显示右边的按钮
		super.setPageTitle("即信");

		initTab();
		initViewPager();
	}

	private void initViewPager() {
		viewPager = findView(R.id.viewpager);

		List<Fragment> fragments = new ArrayList<>();
		fragmentConversation = new MainFragment01();
		fragments.add(fragmentConversation);
		fragments.add(new MainFragment02());
		fragments.add(new MainFragment03());
		fragments.add(new MainFragment04());

		PagerAdapter mainAdapter = new MainAdapter(
				getSupportFragmentManager(), fragments);
		viewPager.setAdapter(mainAdapter);
	}

	private void initTab() {
		llTabLayout = findView(R.id.ll_tabs);

		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// 点击的是第几个选项卡
				int position = (int) view.getTag();
				// 选项卡切换了
				// onTabSelected(position);
				// 禁用界面切换的动画
				viewPager.setCurrentItem(position, false);
			}
		};

		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				0, LinearLayout.LayoutParams.MATCH_PARENT);
		param.weight = 1;		// 通过代码指定权重

		int padding = Global.dp2px(5);		// 5dp
		for (int i = 0; i < tabs.length; i++) {
			GradientTab tab = new GradientTab(this);
			tab.setTag(i);		// 设置选面卡标识
			tabs[i] = tab;
			// 设置选项卡标题和图标
			tab.setTextAndIcon(titles[i], icons[i]);
			tab.setPadding(0, padding, 0, padding);
			tab.setOnClickListener(listener);
			// 添加选项卡
			llTabLayout.addView(tab, param);
		}

		// 默认选中第一个选项卡
		mCurrentTab = tabs[0];
		// 选中状态，显示为高亮效果
		mCurrentTab.setTabSelected(true);
	}

	/**
	 * 选项卡切换了
	 * @param position 点击的选项卡位置
	 */
	private void onTabSelected(int position) {
		// 上一次选中的取消高亮
		mCurrentTab.setTabSelected(false);
		mCurrentTab = tabs[position];
		// 当前选中的设为高亮
		mCurrentTab.setTabSelected(true);

//		// 演示选项卡自定义控件的使用
//		if (position == 0) {
//			tabs[0].setUnreadCount(5);
//			tabs[1].setRedDotVisible(true);		// 显示红点，表示有新消息
//		} else if (position == 3) {
//			tabs[0].setUnreadCount(0);
//			tabs[1].setRedDotVisible(false);	// 显示红点，表示有新消息
//		}
	}

	@Override
	public void initListener() {
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				onTabSelected(position);
			}

			// 滑动ViewPager时调用
			@Override
			public void onPageScrolled(int position, float percent, int
					positionOffsetPixels) {
				// 不是最后一个选项卡，否则造成越界
				if (position != tabs.length - 1) {
					GradientTab leftTab = tabs[position];
					GradientTab rightTab = tabs[position + 1];

					// 刷新选项卡的透明度
					leftTab.updateTabAlpha(1 - percent);
					rightTab.updateTabAlpha(percent);
				}

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	@Override
	public void initData() {

	}

	@Override
	public void onClick(View v, int id) {
		if (id == R.id.title_bar_btn_right) {
			enterAddContactActivity();
		}
	}

	private void enterAddContactActivity() {
		Intent intent = new Intent(this, AddContactActivity.class);
		startActivity(intent);
	}

	private UserDao userDao;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userDao = new UserDao(this);

		initGlobalListener();
	}


	EMContactListener mEMContactListener = new EMContactListener() {

		// 被删除时回调此方法
		@Override
		public void onContactDeleted(String username) {
			// 从数据库中删除好友
			userDao.deleteContact(username);

			// 发送eventbus事件，通知联系人Fragment列表刷新
			Message msg = Message.obtain();
			msg.what = Const.TYPE_DEL_CONTACT;
			msg.obj = username;
			EventBus.getDefault().post(msg);
		}

		// 增加了联系人时回调此方法
		@Override
		public void onContactAdded(String username) {
			// 把新增的好好友添加到数据库
			EaseUser user = new EaseUser(username);
			userDao.saveContact(user);

			// 发送eventbus事件，通知联系人Fragment列表刷新
			Message msg = Message.obtain();
			msg.what = Const.TYPE_ADD_CONTACT;
			msg.obj = username;
			EventBus.getDefault().post(msg);
		}

		@Override
		public void onContactAgreed(String username) {
			//好友请求被同意
		}

		@Override
		public void onContactRefused(String username) {
			//好友请求被拒绝
		}

		@Override
		public void onContactInvited(String username, String reason) {
			//收到好友邀请
		}
	};

	EMMessageListener mMessageListener = new EMMessageListener() {
		// 子线程回调
		@Override
		public void onMessageReceived(List<EMMessage> messages) {
			// 收到消息
			// 刷新列表和未读消息总条数
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					reloadConversationAndRefreshUnreadMsgCount();
				}
			});
		}

		@Override
		public void onCmdMessageReceived(List<EMMessage> messages) {
			//收到透传消息
		}

		@Override
		public void onMessageReadAckReceived(List<EMMessage> messages) {
			//收到已读回执
		}

		@Override
		public void onMessageDeliveryAckReceived(List<EMMessage> message) {
			//收到已送达回执
		}

		@Override
		public void onMessageChanged(EMMessage message, Object change) {
			//消息状态变动
		}
	};

	/** 刷新会话列表和未读消息总条数*/
	private void reloadConversationAndRefreshUnreadMsgCount() {
		// 刷新会话列表
		if (fragmentConversation != null)
			fragmentConversation.loadConversationDatas();

		// 刷新未读消息总条数
		updateUnreadMsgCount();
	}

	private void initGlobalListener() {
		// 监听联系人变化
		EMClient.getInstance().contactManager().setContactListener(mEMContactListener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 取消监听
		EMClient.getInstance().contactManager().removeContactListener(mEMContactListener);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 记得在不需要的时候移除listener，如在activity的onDestroy()时
		EMClient.getInstance().chatManager().removeMessageListener(mMessageListener);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 监听来了新消息
		EMClient.getInstance().chatManager().addMessageListener(mMessageListener);

		// 在选项卡上显示未读消息总条数
		updateUnreadMsgCount();

		// 刷新会话列表
		if (fragmentConversation != null)
			fragmentConversation.loadConversationDatas();
	}

	/** 在选项卡上显示未读消息总条数*/
	private void updateUnreadMsgCount() {
		int unreadMsgCount = EMClient.getInstance()
				.chatManager().getUnreadMsgsCount();
		tabs[0].setUnreadCount(unreadMsgCount);
	}
}























