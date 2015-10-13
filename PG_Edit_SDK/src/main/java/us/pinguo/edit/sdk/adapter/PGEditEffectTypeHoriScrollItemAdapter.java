package us.pinguo.edit.sdk.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import us.pinguo.edit.sdk.R;
import us.pinguo.edit.sdk.base.view.EffectTypeMaskView;
import us.pinguo.edit.sdk.base.widget.BaseHoriScrollItemAdapter;
import us.pinguo.edit.sdk.base.widget.LinearHoriScrollView;
import us.pinguo.edit.sdk.core.model.PGEftPkgDispInfo;
import us.pinguo.edit.sdk.core.utils.SystemUtils;
import us.pinguo.edit.sdk.widget.ImageLoaderView;

public class PGEditEffectTypeHoriScrollItemAdapter extends BaseHoriScrollItemAdapter {

    private Context mContext;
    private View.OnClickListener mOnItemViewClickListener;

    public void setContext(Context context) {
        mContext = context;
    }

    @Override
    public View initView(LinearHoriScrollView parent, Context context, int position) {

        // FIXME 封装接口
        PGEftPkgDispInfo dispInfo = (PGEftPkgDispInfo) mList.get(position);
        View itemView = View.inflate(mContext, R.layout.layout_effect_type_item, null);
        ImageLoaderView icon = (ImageLoaderView) itemView.findViewById(R.id.effect_type_image);
        TextView text = (TextView) itemView.findViewById(R.id.effect_type_text);
        View colorBar = itemView.findViewById(R.id.effect_type_color_bar);
        icon.setScaleType(ImageView.ScaleType.FIT_XY);
        itemView.setTag(dispInfo);
        itemView.setOnClickListener(mOnItemViewClickListener);

        EffectTypeMaskView effectTypeMaskView = (EffectTypeMaskView) itemView.findViewById(R.id.effect_type_mask);
        effectTypeMaskView.setEffectTypeBackgroundColor(dispInfo.getColor() & 0xb3ffffff);
        effectTypeMaskView.setNormalMaskBackgroudDrawable(
                createStateListDrawable(0x33000000, dispInfo.getColor() & 0xb3ffffff));
        effectTypeMaskView.showEffectTypeMask(false);

        colorBar.setBackgroundColor(dispInfo.getColor());
        icon.setImageUrl(dispInfo.getIconFileUrl(mContext));
        String info = SystemUtils.getLocationInfo();
        text.setText(dispInfo.getName(info.replace("-", "_")));

        return itemView;
    }

    public void setOnItemViewClickListener(View.OnClickListener onItemViewClickListener) {
        mOnItemViewClickListener = onItemViewClickListener;
    }
}