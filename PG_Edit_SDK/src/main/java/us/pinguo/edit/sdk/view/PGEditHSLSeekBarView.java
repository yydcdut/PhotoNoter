package us.pinguo.edit.sdk.view;

import android.content.Context;

import us.pinguo.edit.sdk.R;

public class PGEditHSLSeekBarView extends PGEditThreeSeekBarView {

    @Override
    public String[] getTextNameArray(Context context) {

        String textNameHue = context.getResources().getString(R.string.pg_sdk_edit_color_hue);
        String textNameSat = context.getResources().getString(R.string.pg_sdk_edit_lightzone_saturation);
        String textNameLight = context.getResources().getString(R.string.pg_sdk_edit_color_light);
        return new String[]{textNameHue, textNameSat, textNameLight};
    }
}
