package cn.itcast.im.util;

import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

/**
 * @author wjq 
 */
public class Utils {

	/**
	 * 查找一个布局里的所有的按钮并设置点击事件
	 * 
	 * @param rootView
	 * @param listener
	 */
	public static void findButtonAndSetOnClickListener(View rootView,
			OnClickListener listener) {
		
		if (rootView instanceof ViewGroup) {
			ViewGroup parent = (ViewGroup) rootView;
			for (int i = 0; i < parent.getChildCount(); i++) {
				View child = parent.getChildAt(i); 
				// 如果是按钮设置点击事件
				if (child instanceof Button || child instanceof ImageButton) {
					child.setOnClickListener(listener); // 设置点击事件
				} if (child instanceof ViewGroup) {
					findButtonAndSetOnClickListener(child, listener);
				}
			}
		}
	}

	/**
	 * 格式化时间显示，如果是当天的短信，则显示时分，否则显示日期
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDate(long date) {
		if (DateUtils.isToday(date)) {
			return DateFormat.format("今天 hh时mm分", date).toString();
		} else {
			return DateFormat.format("yyyy年MM月dd日", date).toString();
		}
	}

}











