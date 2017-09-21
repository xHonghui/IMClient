package cn.itcast.im.ui.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import cn.itcast.im.ui.holder.BaseHolder;

/**
 * 适配器基类封装
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {

    private Context context;
    
    /** 列表显示的数据集合 */
    public List<T> listData;

    public MyBaseAdapter(Context context, List<T> listData) {
        this.context = context;
        this.listData = listData;
    }

    /**
     * 刷新数据显示
     * @param newData 要显示的新数据
     */
    public void setDatas(List<T> newData) {
        this.listData = newData;
        notifyDataSetChanged();
    }

	@SuppressWarnings("unchecked")
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder<T> holder = null;

        // 列表项对应的javabean
        T bean = (T) getItem(position);
        if (convertView == null) {
            // 创建holder对象
            holder = createViewHolder(context, parent, bean, position);
            // 初始化holder, 填充布局，查找子控件，settag等
            holder.init();
        } else {
            holder = (BaseHolder<T>) convertView.getTag();
        }

        // 刷新item子控件显示
        holder.refreshView(bean, position);
        // 返回item布局
        return holder.getItemView();
    }

    /**
     * 创建holder对象
     *
     * @param context
     * @param parent
     * @param bean 列表项对应的javabean
     * @param position 列表项位置
     * @return
     */
    public abstract BaseHolder<T> createViewHolder(
            Context context, ViewGroup parent, T bean, int position);

    @Override
    public int getCount() {
        return listData == null ? 0 : listData.size();
    }

    /**
     * 获取列表项对应的实体对象
     */
    @Override
    public T getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * 删除一个对象, 并刷新界面
     * @param bean
     */
    public void remove(T bean) {
        listData.remove(bean);
        notifyDataSetChanged();
    }
}
















