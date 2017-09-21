package cn.itcast.im.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.manager.PreferenceManager;

import cn.itcast.im.R;
import cn.itcast.im.ui.activity.LoginActivity;


/**
 * @author WJQ
 */

public class MainFragment04 extends BaseFragment {
    private ImageView iv_avatar;
    private TextView tv_nick_name;
    private TextView tv_username;
    private Button btn_logout;

    @Override
    public int getLayoutRes() {
        return R.layout.main_fragment_04;
    }

    @Override
    public void initView() {
        iv_avatar = (ImageView) mRoot.findViewById(R.id.iv_avatar);
        tv_nick_name = (TextView) mRoot.findViewById(R.id.tv_nick_name);
        tv_username = (TextView) mRoot.findViewById(R.id.tv_username);
        btn_logout = (Button) mRoot.findViewById(R.id.btn_logout);

        // 获取当前登录用户
        String username = PreferenceManager.getInstance().getCurrentUsername();
        tv_nick_name.setText(username);
        tv_username.setText(username);
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v, int id) {
        if (id == R.id.btn_logout) {
            logout();
            return;
        }
    }

    /**
     * 退出当前登录账号
     */
    private void logout() {
        mActivity.showProgressDialog("正在注销...");

        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                // 销毁对话框提示
                mActivity.dismissProgressDialog();

                enterLoginActivity();
            }

            @Override
            public void onProgress(int arg0, String arg1) {
            }

            @Override
            public void onError(int arg0, String error) {
                // 销毁对话框提示
                mActivity.dismissProgressDialog();
                showToast("注销失败:" + error);
            }
        });
    }


    protected void enterLoginActivity() {
        Intent intent = new Intent(mActivity, LoginActivity.class);
        mActivity.startActivity(intent);
        mActivity.finish(); // 退出主界面
    }
}
