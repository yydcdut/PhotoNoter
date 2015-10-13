package us.pinguo.edit.sdk.adapter;

import android.content.Context;
import android.view.View;

import us.pinguo.edit.sdk.base.bean.PGEditMenuBean;
import us.pinguo.edit.sdk.base.widget.BaseHoriScrollItemAdapter;
import us.pinguo.edit.sdk.base.widget.LinearHoriScrollView;
import us.pinguo.edit.sdk.view.PGEditMenuItemWithValueView;

public class PGEditBaseHoriScrollItemAdapter extends BaseHoriScrollItemAdapter {

    private Context mContext;
    private View.OnClickListener mOnItemViewClickListener;

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public View initView(LinearHoriScrollView parent, Context context, int position) {
        PGEditMenuBean menusBean = (PGEditMenuBean) mList.get(position);
        PGEditMenuItemWithValueView itemView = new PGEditMenuItemWithValueView(mContext);
        itemView.setIcon(menusBean.getIcon());
        if (menusBean.getName() != null && !menusBean.getName().equals("")) {
            itemView.setNameText(menusBean.getName());
        } else {
            itemView.hideName();
        }
        itemView.enableDivider(true);
        itemView.setTag(menusBean.clone());
        itemView.setOnClickListener(mOnItemViewClickListener);

        return itemView;
    }

    public void setOnItemViewClickListener(View.OnClickListener onItemViewClickListener) {
        mOnItemViewClickListener = onItemViewClickListener;
    }
}
