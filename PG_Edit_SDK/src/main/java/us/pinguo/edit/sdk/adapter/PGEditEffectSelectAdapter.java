package us.pinguo.edit.sdk.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import us.pinguo.edit.sdk.R;
import us.pinguo.edit.sdk.base.widget.BaseHoriScrollItemAdapter;
import us.pinguo.edit.sdk.base.widget.LinearHoriScrollView;
import us.pinguo.edit.sdk.core.model.PGEftDispInfo;
import us.pinguo.edit.sdk.core.utils.SystemUtils;
import us.pinguo.edit.sdk.widget.ImageLoaderView;

/**
 * Created by mr on 14-9-9.
 */
public class PGEditEffectSelectAdapter extends BaseHoriScrollItemAdapter {

    private LinearHoriScrollView mParentView;

    public PGEditEffectSelectAdapter(LinearHoriScrollView parentView, int selectPosition) {
        mParentView = parentView;
        setSelectPosition(selectPosition);
    }

    @Override
    public View initView(final LinearHoriScrollView parent, Context context, final int position) {
        final PGEftDispInfo effect = (PGEftDispInfo) getItem(position);
        View item;
        item = View.inflate(context, R.layout.layout_effect_select_item, null);
        ImageLoaderView icon = (ImageLoaderView) item.findViewById(R.id.effect_image);
        icon.setScaleType(ImageView.ScaleType.FIT_XY);
        icon.setImageUrl(effect.getIconFileUrl(context));
        TextView text = (TextView) item.findViewById(R.id.effect_text);
        text.setBackgroundColor(effect.getColor());
        String info = SystemUtils.getLocationInfo();
        text.setText(effect.getName(info.replace("-", "_")));
        ImageView ivMask = (ImageView) item.findViewById(R.id.effect_mask);
        ivMask.setBackgroundColor(effect.getColor() & 0xb3ffffff);
        View effectStateView = item.findViewById(R.id.effect_state_parent);
        View view = item.findViewById(R.id.id_effect_click_state);
        view.setBackgroundDrawable(
                createStateListDrawable(0x00000000, effect.getColor() & 0xb3ffffff));
        if (position == getSelectPosition()) {
            effectStateView.setVisibility(View.VISIBLE);
            view.setVisibility(View.INVISIBLE);
        } else {
            effectStateView.setVisibility(View.INVISIBLE);
            view.setVisibility(View.VISIBLE);
        }
        View imageClick = item.findViewById(R.id.effect_image_container);
        EffectItemClickListener effectItemClickListener = new EffectItemClickListener(position);
        imageClick.setOnClickListener(effectItemClickListener);
        return item;
    }

    public void changeSelectPosition(int position) {

        int lastSelected = getSelectPosition();
        View lastStateView = findEffectStateView(lastSelected, R.id.effect_state_parent);
        View lastClickStateView = findEffectStateView(lastSelected, R.id.id_effect_click_state);

        if (lastStateView != null) {
            lastStateView.setVisibility(View.INVISIBLE);
        }
        if (lastClickStateView != null) {
            lastClickStateView.setVisibility(View.VISIBLE);
        }

        setSelectPosition(position);
        View stateView = findEffectStateView(position, R.id.effect_state_parent);
        View clickStateView = findEffectStateView(position, R.id.id_effect_click_state);

        if (stateView != null) {
            stateView.setVisibility(View.VISIBLE);
        }
        if (clickStateView != null) {
            clickStateView.setVisibility(View.INVISIBLE);
        }
    }

    protected View findEffectStateView(int position, int viewId) {
        int count = mParentView.getLinearContainer().getChildCount();
        for (int i = 0; i < count; i++) {
        }
        if (position >= 0 && position < mParentView.getLinearContainer().getChildCount()) {
            View item = mParentView.getLinearContainer().getChildAt(position);
            if (item != null) {
                View mask = item.findViewById(viewId);
                if (mask != null) {
                    return mask;
                }
            }
        }
        return null;
    }

    protected class EffectItemClickListener implements View.OnClickListener {
        private int mPosition;

        public EffectItemClickListener(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View v) {
            changeSelectPosition(mPosition);
            mParentView.smoothScrollItemToCenter(mPosition);
        }
    }
}
