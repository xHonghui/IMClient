package cn.itcast.im.base;

import android.content.Context;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.manager.EaseNotifier;
import com.hyphenate.easeui.manager.PreferenceManager;

/**
 * 环信初始化辅助类
 */
public class ImHelper {

    private static ImHelper instance = new ImHelper();

    private ImHelper() {
    }

    public static ImHelper getInstance() {
        return instance;
    }

    private Context context = Global.mContext;

    /**
     * 初始化环信sdk
     */
    public void init() {
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的。如果需要验证可设置成false
        // options.setAcceptInvitationAlways(false);
        // 设置是否需要已读回执   Ack: 回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(true);
        // 默认加载多少条消息
        // options.setNumberOfMessagesLoaded(1);

        // 初始化环信sdk
        EMClient.getInstance().init(context, options);
        // 打开调试模式,在混淆打包时要设置为false，否则程序无法运行
        EMClient.getInstance().setDebugMode(true);

        // 初始化管理类对象
        EaseNotifier.init(context);
        PreferenceManager.init(context);
    }
    // ...
}