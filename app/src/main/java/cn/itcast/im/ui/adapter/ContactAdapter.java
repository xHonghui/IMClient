package cn.itcast.im.ui.adapter;

import android.content.Context;
import android.view.ViewGroup;

import com.hyphenate.easeui.domain.EaseUser;

import java.util.List;

import cn.itcast.im.ui.holder.BaseHolder;
import cn.itcast.im.ui.holder.ContactHolder;

/**
 * @author WJQ
 */
public class ContactAdapter extends MyBaseAdapter<EaseUser> {

    public ContactAdapter(Context context, List<EaseUser> listData) {
        super(context, listData);
    }

    @Override
    public BaseHolder<EaseUser> createViewHolder(Context context, ViewGroup parent,
                                                 EaseUser bean, int position) {
        return new ContactHolder(context, parent, this, position, bean);
    }
}
