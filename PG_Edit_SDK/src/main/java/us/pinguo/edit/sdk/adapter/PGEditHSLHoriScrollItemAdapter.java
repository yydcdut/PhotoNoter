package us.pinguo.edit.sdk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import us.pinguo.edit.sdk.base.bean.PGEditHSLMenuBean;
import us.pinguo.edit.sdk.base.widget.LinearHoriScrollView;
import us.pinguo.edit.sdk.view.PGEditMenuItemWithValueView;

public class PGEditHSLHoriScrollItemAdapter extends PGEditBaseHoriScrollItemAdapter {

    @Override
    public View initView(LinearHoriScrollView parent, Context context, int position) {
        PGEditMenuItemWithValueView itemView = (PGEditMenuItemWithValueView) super.initView(parent, context, position);
        PGEditHSLMenuBean menusBean = (PGEditHSLMenuBean) mList.get(position);
        itemView.setItemBg(Color.parseColor(menusBean.getBackgroundColor()));

        return itemView;
    }
}
