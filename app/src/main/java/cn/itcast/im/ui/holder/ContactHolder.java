package cn.itcast.im.ui.holder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.domain.EaseUser;

import cn.itcast.im.R;
import cn.itcast.im.base.Global;
import cn.itcast.im.ui.adapter.MyBaseAdapter;

/**
 * @author WJQ
 */
public class ContactHolder extends BaseHolder<EaseUser> {

    private TextView tvCatalog;
    private ImageView ivIcon;
    private TextView tvName;
    private ImageView ivRedDot;

    public ContactHolder(Context context, ViewGroup parent,
                         MyBaseAdapter<EaseUser> adapter,
                         int position, EaseUser bean) {
        super(context, parent, adapter, position, bean);
    }

    @Override
    public View onCreateView(Context context, ViewGroup parent) {
        View item = Global.inflate(R.layout.item_contact, parent);
        initView(item);
        return item;
    }

    @Override
    protected void onRefreshView(EaseUser bean, int position) {
        // 显示列表项子控件
        tvCatalog.setVisibility(View.GONE);
        tvName.setText(bean.getUsername());
    }

    private void initView(View item) {
        tvCatalog = (TextView) item.findViewById(R.id.tv_catalog);
        ivIcon = (ImageView) item.findViewById(R.id.iv_icon);
        tvName = (TextView) item.findViewById(R.id.tv_name);
        ivRedDot = (ImageView) item.findViewById(R.id.iv_red_dot);
    }
}
