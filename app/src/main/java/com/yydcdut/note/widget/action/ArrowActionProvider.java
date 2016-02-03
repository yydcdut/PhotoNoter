package com.yydcdut.note.widget.action;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.yydcdut.note.R;
import com.yydcdut.note.utils.Const;

/**
 * Created by yuyidong on 16/1/12.
 */
public class ArrowActionProvider extends AbsActionProvider implements View.OnClickListener {
    private View mView;
    private boolean isOpened = true;

    /**
     * Creates a new instance.
     *
     * @param context Context for accessing resources.
     */
    public ArrowActionProvider(Context context) {
        super(context);
    }

    @Override
    public View onCreateActionView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.action_arrow, null);
        mView = view.findViewById(R.id.img_menu_arrow);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (mOnActionProviderClickListener != null) {
            mOnActionProviderClickListener.onActionClick(this);
        }
        if (isOpened) {
            close();
            isOpened = false;
        } else {
            open();
            isOpened = true;
        }
    }

    private void open() {
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(Const.DURATION);
        animation.playTogether(
                ObjectAnimator.ofFloat(mView, "rotationX", 180f, 0f),
                ObjectAnimator.ofFloat(mView, "rotationY", 180f, 0f)
        );
        animation.start();
    }

    private void close() {
        AnimatorSet animation = new AnimatorSet();
        animation.setDuration(Const.DURATION);
        animation.playTogether(
                ObjectAnimator.ofFloat(mView, "rotationX", 0f, 180f),
                ObjectAnimator.ofFloat(mView, "rotationY", 0f, 180f)
        );
        animation.start();

    }

}
