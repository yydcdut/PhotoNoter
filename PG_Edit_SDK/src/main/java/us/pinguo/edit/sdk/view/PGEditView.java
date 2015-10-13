package us.pinguo.edit.sdk.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import us.pinguo.androidsdk.PGGLListener;
import us.pinguo.androidsdk.PGGLSurfaceView;
import us.pinguo.edit.sdk.R;
import us.pinguo.edit.sdk.adapter.PGEditBaseHoriScrollItemAdapter;
import us.pinguo.edit.sdk.adapter.PGEditEffectHoriScrollItemAdapter;
import us.pinguo.edit.sdk.adapter.PGEditEffectTypeHoriScrollItemAdapter;
import us.pinguo.edit.sdk.adapter.PGEditHSLHoriScrollItemAdapter;
import us.pinguo.edit.sdk.base.PGEditConstants;
import us.pinguo.edit.sdk.base.PGEditTools;
import us.pinguo.edit.sdk.base.view.EffectTypeMaskView;
import us.pinguo.edit.sdk.base.view.IMenuItemView;
import us.pinguo.edit.sdk.base.view.IPGEditCompareGLSurfaceView;
import us.pinguo.edit.sdk.base.view.IPGEditRandomSeekBarView;
import us.pinguo.edit.sdk.base.view.IPGEditSeekBarView;
import us.pinguo.edit.sdk.base.view.IPGEditThreeSeekBarView;
import us.pinguo.edit.sdk.base.view.IPGEditTiltShiftSeekBarView;
import us.pinguo.edit.sdk.base.view.IPGEditView;
import us.pinguo.edit.sdk.base.view.IPGEditViewListener;
import us.pinguo.edit.sdk.base.view.IPGEditViewMenuListener;
import us.pinguo.edit.sdk.base.view.PGEditAutoHideTextView;
import us.pinguo.edit.sdk.base.widget.AnimationAdapter;
import us.pinguo.edit.sdk.base.widget.LinearHoriScrollView;

public class PGEditView implements IPGEditView {

    private static final long ANIMATION_TIME = 250l;
    protected IPGEditCompareGLSurfaceView mCompareGLSurfaceView;
    protected LinearHoriScrollView mFirstHorizontalLayout;
    protected LinearHoriScrollView mSecondHorizontalLayout;
    protected View mSecondMenusLayout;
    protected LinearHoriScrollView mThirdHorizontalLayout;
    protected View mFirstTopView;
    protected ViewGroup mCenterLayout;
    protected View mSaveEffectView;
    protected View mBackMainView;
    protected View mQuitView;
    protected View mSavePhotoView;
    protected View mProgressDialogView;
    protected View mLastStepView;
    protected View mNextStepView;
    protected View mBannerView;

    protected TextView mSecondBottomName;
    protected View mCenterLayoutParent;
    protected View mStepLayout;
    protected View mEffectBackView;
    protected PGEditAutoHideTextView mNameAutoHideTextView;
    protected PGEditAutoHideTextView mValueAutoHideTextView;
    private IPGEditViewListener mListener;
    private IPGEditViewMenuListener mMenuListener;
    private Activity mActivity;

    public void initView(Activity activity) {
        activity.setContentView(R.layout.pg_sdk_edit_main);
        mCompareGLSurfaceView = (IPGEditCompareGLSurfaceView) activity.findViewById(R.id.compare_view);
        mCompareGLSurfaceView.getPGGLSurfaceView().setListener(mPGGLListener);

        mBannerView = activity.findViewById(R.id.pg_sdk_edit_banner);
        mBannerView.setOnClickListener(mOnClickListener);

        mFirstHorizontalLayout = (LinearHoriScrollView) activity.findViewById(R.id.first_menus);
        mSecondHorizontalLayout = (LinearHoriScrollView) activity.findViewById(R.id.second_menus);
        mSecondMenusLayout = activity.findViewById(R.id.second_menus_layout);

        mThirdHorizontalLayout = (LinearHoriScrollView) activity.findViewById(R.id.third_menus);

        mFirstTopView = activity.findViewById(R.id.first_top);
        mStepLayout = activity.findViewById(R.id.step_layout);
        mSaveEffectView = activity.findViewById(R.id.save_effect);
        mSaveEffectView.setOnClickListener(mOnClickListener);

        mBackMainView = activity.findViewById(R.id.back_main);
        mBackMainView.setOnClickListener(mOnClickListener);

        mQuitView = activity.findViewById(R.id.quit);
        mQuitView.setOnClickListener(mOnClickListener);

        mSavePhotoView = activity.findViewById(R.id.save_photo);
        mSavePhotoView.setOnClickListener(mOnClickListener);

        mCenterLayout = (ViewGroup) activity.findViewById(R.id.center_layout);
        mCenterLayoutParent = activity.findViewById(R.id.center_layout_parent);

        mProgressDialogView = activity.findViewById(R.id.progress_dialog);

        mLastStepView = activity.findViewById(R.id.last_step);
        mLastStepView.setOnClickListener(mOnClickListener);
        mLastStepView.setEnabled(false);

        mNextStepView = activity.findViewById(R.id.next_step);
        mNextStepView.setOnClickListener(mOnClickListener);
        mNextStepView.setEnabled(false);

        mNameAutoHideTextView = (PGEditAutoHideTextView) activity.findViewById(R.id.name_auto_hide_textview);
        mValueAutoHideTextView = (PGEditAutoHideTextView) activity.findViewById(R.id.value_auto_hide_textview);
        mSecondBottomName = (TextView) activity.findViewById(R.id.second_bottom_name);

        mEffectBackView = activity.findViewById(R.id.effect_back);
        mActivity = activity;
    }

    public void showProgress() {
        mProgressDialogView.setVisibility(View.VISIBLE);
    }

    public void hideProgress() {
        mProgressDialogView.setVisibility(View.GONE);
    }

    public boolean isInProgressing() {
        return mProgressDialogView.getVisibility() == View.VISIBLE;
    }

    public void setListener(IPGEditViewListener listener) {
        mListener = listener;
    }

    public void toastFailForSdCard(Context context, boolean hasSD) {
        if (hasSD) {
            Toast.makeText(context, R.string.pg_sdk_edit_no_free_space, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, R.string.pg_sdk_edit_no_storage, Toast.LENGTH_LONG).show();
        }
    }

    public void toastFirstFail(Context context) {
        Toast toast = Toast.makeText(context, R.string.pg_sdk_edit_first_show_fail, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void setImageViewPhoto(Bitmap bitmap) {
        mCompareGLSurfaceView.setImageViewPhoto(bitmap);
    }

    public void setImageViewLayoutParam(int photoShowWidth, int photoShowHeight) {
        mCompareGLSurfaceView.setImageViewLayoutParam(photoShowWidth, photoShowHeight);
    }

    public PGGLSurfaceView getPGGLSurfaceView() {
        return mCompareGLSurfaceView.getPGGLSurfaceView();
    }

    public LinearHoriScrollView getFirstHorizontalLayout() {
        return mFirstHorizontalLayout;
    }

    public LinearHoriScrollView getSecondHorizontalLayout() {
        return mSecondHorizontalLayout;
    }

    public LinearHoriScrollView getThirdHorizontalLayout() {
        return mThirdHorizontalLayout;
    }

    public View getFirstTopView() {
        return mFirstTopView;
    }

    public ViewGroup getCenterLayout() {
        return mCenterLayout;
    }

    public View getCenterLayoutParent() {
        return mCenterLayoutParent;
    }

    public IPGEditCompareGLSurfaceView getCompareGLSurfaceView() {
        return mCompareGLSurfaceView;
    }

    public View getProgressDialogView() {
        return mProgressDialogView;
    }

    public PGEditAutoHideTextView getNameAutoHideTextView() {
        return mNameAutoHideTextView;
    }

    public PGEditAutoHideTextView getValueAutoHideTextView() {
        return mValueAutoHideTextView;
    }

    public View getSecondMenusLayout() {
        return mSecondMenusLayout;
    }

    public TextView getSecondBottomName() {
        return mSecondBottomName;
    }

    public View getStepLayout() {
        return mStepLayout;
    }

    public View getEffectBackView() {
        return mEffectBackView;
    }

    public View getBackMainView() {
        return mBackMainView;
    }

    public View getSaveEffectView() {
        return mSaveEffectView;
    }

    public void enableNextAndLast(boolean nextStep, boolean lastStep) {
        mLastStepView.setEnabled(lastStep);
        mNextStepView.setEnabled(nextStep);
    }

    public void toastMakePhotoFail(Context context) {
        Toast.makeText(context, R.string.pg_sdk_edit_make_photo_fail, Toast.LENGTH_LONG).show();
    }

    public void showQuitDialog(Activity activity) {

        SaveTipsDialog dialog = new SaveTipsDialog(activity);
        dialog.show();
    }

    public void moveTopAndCenterToUpWithAnimation() {
        if (Build.VERSION.SDK_INT >= 11) {
            float centerMoveHeight = mActivity.getResources().getDimension(
                    R.dimen.pg_sdk_edit_center_move_top_height);
            mStepLayout.setVisibility(View.INVISIBLE);
            ObjectAnimator centerAnimator = ObjectAnimator.ofFloat(mCenterLayoutParent, "y", 0f, -centerMoveHeight);
            centerAnimator.setDuration(ANIMATION_TIME);
            centerAnimator.start();

            float topHeight = mActivity.getResources().getDimension(R.dimen.pg_sdk_edit_top_height);
            ObjectAnimator topAnimator = ObjectAnimator.ofFloat(mFirstTopView, "y", 0f, -topHeight);
            topAnimator.setDuration(ANIMATION_TIME);
            topAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != mMenuListener) {
                                mMenuListener.onShowSecondAnimationEnd();
                            }
                        }
                    });
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            topAnimator.start();

        } else {
            mStepLayout.setVisibility(View.INVISIBLE);
            mFirstTopView.setVisibility(View.GONE);
            int centerMoveHeight = Math.round(mActivity.getResources().getDimension(R.dimen.pg_sdk_edit_center_move_top_height));
            mCenterLayoutParent.setPadding(mCenterLayoutParent.getPaddingLeft(),
                    mCenterLayoutParent.getPaddingTop() - centerMoveHeight,
                    mCenterLayoutParent.getPaddingRight(),
                    centerMoveHeight + mCenterLayoutParent.getPaddingBottom());
            if (null != mMenuListener) {
                mMenuListener.onShowSecondAnimationEnd();
            }
        }
    }

    public void showBottomSecondMenuWithAnimation() {
        mSecondMenusLayout.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 11) {
            float secondBottomHeight = mActivity.getResources().getDimension(R.dimen.pg_sdk_edit_second_bottom_height);
            TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, secondBottomHeight, 0f);
            translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            translateAnimation.setDuration(ANIMATION_TIME);
            mSecondMenusLayout.startAnimation(translateAnimation);

            float bottomHeight = mActivity.getResources().getDimension(R.dimen.pg_sdk_edit_bottom_height);
            TranslateAnimation firstTranslateAnimation = new TranslateAnimation(0f, 0f, 0f, bottomHeight);
            firstTranslateAnimation.setDuration(ANIMATION_TIME);
            firstTranslateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            AlphaAnimation firstAlphaAnimation = new AlphaAnimation(1f, 0f);
            firstAlphaAnimation.setDuration(ANIMATION_TIME);
            firstAlphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(firstTranslateAnimation);
            animationSet.addAnimation(firstAlphaAnimation);

            animationSet.setAnimationListener(new AnimationAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mFirstHorizontalLayout.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            });
            mFirstHorizontalLayout.startAnimation(animationSet);

        } else {
            mFirstHorizontalLayout.setVisibility(View.INVISIBLE);

        }
    }

    public void setMenuListener(IPGEditViewMenuListener menuListener) {
        mMenuListener = menuListener;
    }

    public void backTopAndCenterWithAnimation() {
        mStepLayout.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 11) {
            float centerHeight = mActivity.getResources().getDimension(R.dimen.pg_sdk_edit_center_move_top_height);
            ObjectAnimator centerAnimator = ObjectAnimator.ofFloat(mCenterLayoutParent, "y", -centerHeight, 0f);
            centerAnimator.setDuration(ANIMATION_TIME);
            centerAnimator.start();

            float topHeight = mActivity.getResources().getDimension(R.dimen.pg_sdk_edit_top_height);
            ObjectAnimator topAnimator = ObjectAnimator.ofFloat(mFirstTopView, "y", -topHeight, 0f);
            topAnimator.setDuration(ANIMATION_TIME);
            topAnimator.start();

        } else {
            mFirstTopView.setVisibility(View.VISIBLE);

            float topHeight = mActivity.getResources().getDimension(R.dimen.pg_sdk_edit_top_height);
            float bottomHeight = mActivity.getResources().getDimension(R.dimen.pg_sdk_edit_bottom_height);
            mCenterLayoutParent.setPadding(mCenterLayoutParent.getPaddingLeft(),
                    Math.round(topHeight), mCenterLayoutParent.getPaddingRight(),
                    Math.round(bottomHeight));

        }
    }

    public void hideBottomSecondMenuWithAnimation() {
        if (Build.VERSION.SDK_INT >= 11) {
            float secondBottomHeight = mActivity.getResources().getDimension(R.dimen.pg_sdk_edit_second_bottom_height);
            TranslateAnimation translateAnimation = new TranslateAnimation(0f, 0f, 0f, secondBottomHeight);
            translateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            translateAnimation.setDuration(ANIMATION_TIME);
            translateAnimation.setAnimationListener(new AnimationAdapter() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (null != mMenuListener) {
                                mMenuListener.onHideBottomSecondMenuWithAnimationFinish();
                            }

                        }
                    });
                }
            });
            mSecondMenusLayout.startAnimation(translateAnimation);

            mFirstHorizontalLayout.setVisibility(View.VISIBLE);
            float bottomHeight = mActivity.getResources().getDimension(R.dimen.pg_sdk_edit_bottom_height);
            TranslateAnimation firstTranslateAnimation = new TranslateAnimation(0f, 0f, bottomHeight, 0f);
            firstTranslateAnimation.setDuration(ANIMATION_TIME);
            firstTranslateAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            AlphaAnimation firstAlphaAnimation = new AlphaAnimation(0f, 1f);
            firstAlphaAnimation.setDuration(ANIMATION_TIME);
            firstAlphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(firstTranslateAnimation);
            animationSet.addAnimation(firstAlphaAnimation);
            mFirstHorizontalLayout.startAnimation(animationSet);

        } else {
            if (null != mMenuListener) {
                mMenuListener.onHideBottomSecondMenuWithAnimationFinish();
            }

            mFirstHorizontalLayout.setVisibility(View.VISIBLE);

        }
    }

    public View getEffectTypeItem(View view) {
        return view.getRootView().findViewById(R.id.effect_type_item_root);
    }

    public EffectTypeMaskView getEffectTypeMask(View view) {
        return (EffectTypeMaskView) view.findViewById(R.id.effect_type_mask);
    }

    public void quitMenu() {
        backTopAndCenterWithAnimation();
        hideBottomSecondMenuWithAnimation();
        mThirdHorizontalLayout.setVisibility(View.GONE);
        mEffectBackView.setVisibility(View.GONE);
        mBackMainView.setVisibility(View.VISIBLE);
        mSecondHorizontalLayout.setVisibility(View.VISIBLE);
    }

    public void showEffectBackWithAnimation() {

        mEffectBackView.setOnClickListener(mOnClickListener);
        mEffectBackView.setVisibility(View.VISIBLE);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(PGEditConstants.EFFECT_ANIMATION_TIME);
        mEffectBackView.startAnimation(alphaAnimation);
    }

    public void hideEffectBackWithAnimation() {

        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(PGEditConstants.EFFECT_ANIMATION_TIME);
        alphaAnimation.setAnimationListener(new AnimationAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mEffectBackView.setVisibility(View.GONE);
                    }
                });
            }
        });
        mEffectBackView.startAnimation(alphaAnimation);
        mEffectBackView.setOnClickListener(null);
    }

    public void backSecondMenuWithAnimation() {
        int scrollOffset = mThirdHorizontalLayout.getLastScrollX() - mThirdHorizontalLayout.getScrollX();
        mThirdHorizontalLayout.startCollapseAnim(mThirdHorizontalLayout.getLastExpandPosition() - scrollOffset);
        mSecondHorizontalLayout.show(true);
    }

    public void resetSecondBottomLayoutWithAnimation() {

        mBackMainView.setVisibility(View.VISIBLE);
        AlphaAnimation backAlphaAnimation = new AlphaAnimation(0f, 1f);
        backAlphaAnimation.setDuration(PGEditConstants.EFFECT_ANIMATION_TIME);
        mBackMainView.startAnimation(backAlphaAnimation);

        AlphaAnimation nameAlphaAnimation = new AlphaAnimation(1f, 0f);
        nameAlphaAnimation.setDuration(PGEditConstants.EFFECT_BOTTOM_ANIMATION_TIME);
        nameAlphaAnimation.setAnimationListener(new AnimationAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mMenuListener) {
                            mMenuListener.onResetSecondBottomLayoutWithAnimationFinish();
                        }

                        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                        alphaAnimation.setDuration(PGEditConstants.EFFECT_BOTTOM_ANIMATION_TIME);
                        mSecondBottomName.startAnimation(alphaAnimation);
                    }
                });
            }
        });
        mSecondBottomName.startAnimation(nameAlphaAnimation);

        AlphaAnimation savePhotoAlphaAnimation = new AlphaAnimation(1f, 0f);
        savePhotoAlphaAnimation.setDuration(PGEditConstants.EFFECT_BOTTOM_ANIMATION_TIME);
        savePhotoAlphaAnimation.setAnimationListener(new AnimationAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                        alphaAnimation.setDuration(PGEditConstants.EFFECT_BOTTOM_ANIMATION_TIME);
                        mSaveEffectView.startAnimation(alphaAnimation);

                    }
                });
            }
        });
        mSaveEffectView.startAnimation(savePhotoAlphaAnimation);

    }

    public void changeSecondBottomLayoutWithAnimation() {
        AlphaAnimation backAlphaAnimation = new AlphaAnimation(1f, 0f);
        backAlphaAnimation.setDuration(PGEditConstants.EFFECT_BOTTOM_ANIMATION_TIME);
        backAlphaAnimation.setAnimationListener(new AnimationAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBackMainView.setVisibility(View.GONE);
                    }
                });
            }
        });
        mBackMainView.startAnimation(backAlphaAnimation);

        AlphaAnimation nameAlphaAnimation = new AlphaAnimation(1f, 0f);
        nameAlphaAnimation.setDuration(PGEditConstants.EFFECT_BOTTOM_ANIMATION_TIME);
        nameAlphaAnimation.setAnimationListener(new AnimationAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mMenuListener) {
                            mMenuListener.onChangeSecondBottomLayoutWithAnimationFinish();
                        }

                        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                        alphaAnimation.setDuration(PGEditConstants.EFFECT_BOTTOM_ANIMATION_TIME);
                        mSecondBottomName.startAnimation(alphaAnimation);
                    }
                });
            }
        });
        mSecondBottomName.startAnimation(nameAlphaAnimation);

        AlphaAnimation savePhotoAlphaAnimation = new AlphaAnimation(1f, 0f);
        savePhotoAlphaAnimation.setDuration(PGEditConstants.EFFECT_BOTTOM_ANIMATION_TIME);
        savePhotoAlphaAnimation.setAnimationListener(new AnimationAdapter() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                        alphaAnimation.setDuration(PGEditConstants.EFFECT_BOTTOM_ANIMATION_TIME);
                        mSaveEffectView.startAnimation(alphaAnimation);

                    }
                });
            }
        });
        mSaveEffectView.startAnimation(savePhotoAlphaAnimation);

    }

    @Override
    public void addSecondChildViews(List childList, float showCount, View.OnClickListener onClickListener) {

        PGEditBaseHoriScrollItemAdapter baseHoriScrollItemAdapter = new PGEditBaseHoriScrollItemAdapter();
        baseHoriScrollItemAdapter.setContext(mActivity);
        baseHoriScrollItemAdapter.setData(childList);
        mSecondHorizontalLayout.setItemCountOnScreen(showCount);
        baseHoriScrollItemAdapter.setOnItemViewClickListener(onClickListener);

        mSecondHorizontalLayout.setAdapter(baseHoriScrollItemAdapter);
    }

    @Override
    public void addSecondEffectChildViews(List list, float showCount,
                                          View.OnClickListener onThirdClickListener,
                                          View.OnClickListener onScrollClickListener,
                                          String maskIcon, View lastSelectedView) {

        PGEditEffectHoriScrollItemAdapter baseHoriScrollItemAdapter
                = new PGEditEffectHoriScrollItemAdapter(mSecondHorizontalLayout, -1);
        baseHoriScrollItemAdapter.setOnItemViewClickListener(onThirdClickListener);
        baseHoriScrollItemAdapter.setOnScrollClickListener(onScrollClickListener);
        baseHoriScrollItemAdapter.hideFirstPosition();
        baseHoriScrollItemAdapter.setMaskResource(maskIcon);
        baseHoriScrollItemAdapter.setData(list);
        baseHoriScrollItemAdapter.setContext(mActivity);
        baseHoriScrollItemAdapter.setLastSelectedView(lastSelectedView);

        mSecondHorizontalLayout.setItemCountOnScreen(showCount);
        mSecondHorizontalLayout.setAdapter(baseHoriScrollItemAdapter);
    }

    @Override
    public void addSecondEffectTypeChildViews(List childList, float showCount, View.OnClickListener onClickListener) {
        PGEditEffectTypeHoriScrollItemAdapter baseHoriScrollItemAdapter = new PGEditEffectTypeHoriScrollItemAdapter();
        baseHoriScrollItemAdapter.setContext(mActivity);
        mSecondHorizontalLayout.setItemCountOnScreen(0f);
        baseHoriScrollItemAdapter.setData(childList);
        baseHoriScrollItemAdapter.setOnItemViewClickListener(onClickListener);

        mSecondHorizontalLayout.setAdapter(baseHoriScrollItemAdapter);
    }

    @Override
    public void showFirstImageViewPhoto(List mPGEditMenusBeanList, float showCount, View.OnClickListener onClickListener) {

        mActivity.findViewById(R.id.step_layout).setVisibility(View.VISIBLE);
        mActivity.findViewById(R.id.first_top).setVisibility(View.VISIBLE);

        PGEditBaseHoriScrollItemAdapter baseHoriScrollItemAdapter = new PGEditBaseHoriScrollItemAdapter();
        baseHoriScrollItemAdapter.setContext(mActivity);
        baseHoriScrollItemAdapter.setData(mPGEditMenusBeanList);
        mFirstHorizontalLayout.setItemCountOnScreen(showCount);
        baseHoriScrollItemAdapter.setOnItemViewClickListener(onClickListener);

        mFirstHorizontalLayout.setAdapter(baseHoriScrollItemAdapter);
    }

    @Override
    public IPGEditSeekBarView createEditSeekBarView() {
        return new PGEditSeekBarView();
    }

    @Override
    public void addThirdEffectChildViews(List list, float showCount,
                                         View.OnClickListener onThirdClickListener,
                                         View.OnClickListener onScrollClickListener,
                                         String maskIcon, View view, View lastSelectedView) {

        PGEditEffectHoriScrollItemAdapter baseHoriScrollItemAdapter
                = new PGEditEffectHoriScrollItemAdapter(mThirdHorizontalLayout, -1);
        baseHoriScrollItemAdapter.setOnItemViewClickListener(onThirdClickListener);
        baseHoriScrollItemAdapter.setOnScrollClickListener(onScrollClickListener);
        baseHoriScrollItemAdapter.setMaskResource(maskIcon);
        baseHoriScrollItemAdapter.setData(list);
        baseHoriScrollItemAdapter.setContext(mActivity);
        baseHoriScrollItemAdapter.setLastSelectedView(lastSelectedView);

        mThirdHorizontalLayout.setItemCountOnScreen(showCount);
        mThirdHorizontalLayout.setAdapter(baseHoriScrollItemAdapter);
        mThirdHorizontalLayout.setVisibility(View.VISIBLE);

        mSecondHorizontalLayout.hide(true);

        final int position = mSecondHorizontalLayout.getLinearContainer().indexOfChild(view);
        final int itemWidth = getEffectTypeItem(view).getWidth();
        mThirdHorizontalLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int scroll = mSecondHorizontalLayout.getScrollX() - mThirdHorizontalLayout.getScrollX();
                mThirdHorizontalLayout.startExpandAnim(position * itemWidth - scroll);
                mThirdHorizontalLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    public void addSecondFrameChildViews(List effectList, float v, View.OnClickListener mThirdClickListener, String pg_sdk_edit_effect_check, int color) {
        PGEditEffectHoriScrollItemAdapter baseHoriScrollItemAdapter
                = new PGEditEffectHoriScrollItemAdapter(mSecondHorizontalLayout, -1);
        baseHoriScrollItemAdapter.setContext(mActivity);
        baseHoriScrollItemAdapter.setMaskResource(pg_sdk_edit_effect_check);
        baseHoriScrollItemAdapter.hideFirstPosition();
        mSecondHorizontalLayout.setItemCountOnScreen(v);
        baseHoriScrollItemAdapter.setData(effectList);
        baseHoriScrollItemAdapter.setOnItemViewClickListener(mThirdClickListener);

        mSecondHorizontalLayout.setAdapter(baseHoriScrollItemAdapter);
    }

    @Override
    public IMenuItemView createEditEffectMenuItemView() {
        return new PGEditMenuItemWithValueView(mActivity);
    }

    @Override
    public IPGEditThreeSeekBarView createEditThreeSeekBarView() {
        return new PGEditThreeSeekBarView();
    }

    @Override
    public IPGEditThreeSeekBarView createEditHSLSeekBarView() {
        return new PGEditHSLSeekBarView();
    }

    @Override
    public IPGEditThreeSeekBarView createEditTintSeekBarView() {
        return new PGEditTintSeekBarView();
    }

    @Override
    public void addSecondHSLChildViews(List childList, float showCount, View.OnClickListener onClickListener) {
        PGEditBaseHoriScrollItemAdapter baseHoriScrollItemAdapter = new PGEditHSLHoriScrollItemAdapter();
        baseHoriScrollItemAdapter.setContext(mActivity);
        baseHoriScrollItemAdapter.setData(childList);
        mSecondHorizontalLayout.setItemCountOnScreen(showCount);
        baseHoriScrollItemAdapter.setOnItemViewClickListener(onClickListener);

        mSecondHorizontalLayout.setAdapter(baseHoriScrollItemAdapter);
    }

    @Override
    public IPGEditRandomSeekBarView createEditRandomSeekBarView() {
        return new PGEditRandomSeekBarView();
    }

    @Override
    public void addSecondLightingChildViews(List effectList, float v, View.OnClickListener onClickListener, View.OnClickListener mOnScrollClickListener, String pg_sdk_edit_effect_scroll) {
        PGEditEffectHoriScrollItemAdapter baseHoriScrollItemAdapter
                = new PGEditEffectHoriScrollItemAdapter(mSecondHorizontalLayout, -1);
        baseHoriScrollItemAdapter.setOnItemViewClickListener(onClickListener);
        baseHoriScrollItemAdapter.setOnScrollClickListener(mOnScrollClickListener);
        baseHoriScrollItemAdapter.setContext(mActivity);
        baseHoriScrollItemAdapter.setMaskResource(pg_sdk_edit_effect_scroll);
        baseHoriScrollItemAdapter.hideFirstPosition();
        mSecondHorizontalLayout.setItemCountOnScreen(v);
        baseHoriScrollItemAdapter.setData(effectList);

        mSecondHorizontalLayout.setAdapter(baseHoriScrollItemAdapter);
    }

    @Override
    public IPGEditTiltShiftSeekBarView createEditTiltShiftSeekBarView() {
        return new PGEditTiltShiftSeekBarView();
    }

    protected PGGLListener mPGGLListener = new PGGLListener() {

        @Override
        public void glCreated(GL10 gl) {
            if (null != mListener) {
                mListener.onGLCreated(gl);
            }
        }

        @Override
        public void glDestroyed() {

        }
    };

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (null == mListener) {
                return;
            }

            if (v == mSaveEffectView) {
                mListener.onSaveEffectClick();
                return;
            }

            if (v == mBackMainView) {
                mListener.onBackClick();
                return;
            }

            if (v == mQuitView) {
                mListener.onQuitClick();
                return;
            }

            if (v == mSavePhotoView) {
                mListener.onSavePhotoClick();
                return;
            }

            if (v == mLastStepView) {
                mListener.onLastStepClick();
                return;
            }

            if (v == mNextStepView) {
                mListener.onNextStepClick();
                return;
            }

            if (v == mBannerView) {
                mListener.onBannerClick();
                return;
            }

            if (v == mEffectBackView) {
                if (null != mMenuListener) {
                    mMenuListener.onEffectBackClick();
                }
            }
        }
    };

    private class SaveTipsDialog extends Dialog {

        public SaveTipsDialog(Context context) {
            super(mActivity, PGEditTools.getStyleByName(mActivity, "SDKThemeDialog"));
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            RelativeLayout root = new RelativeLayout(mActivity);
            RelativeLayout.LayoutParams rootParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            root.setPadding(30, 0, 30, 0);
            root.setLayoutParams(rootParams);

            LinearLayout container = new LinearLayout(mActivity);
            container.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams containerParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            containerParams.setMargins(30, 0, 30, 0);
            container.setLayoutParams(containerParams);
            container.setPadding(30, 20, 30, 20);

            TextView descView = new TextView(mActivity);
            LinearLayout.LayoutParams descViewParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            descViewParams.setMargins(0, 20, 0, 20);
            descView.setLayoutParams(descViewParams);
            descView.setGravity(Gravity.CENTER);
            descView.setTextColor(Color.parseColor("#ffffff"));
            descView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            descView.setText(mActivity.getString(R.string.pg_sdk_edit_quit_edit));
            container.addView(descView);

            ImageView vDivider = new ImageView(mActivity);
            LinearLayout.LayoutParams vDividerParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, 1);
            vDividerParams.setMargins(0, 20, 0, 0);
            vDivider.setLayoutParams(vDividerParams);
            container.addView(vDivider);

            RelativeLayout buttonContainer = new RelativeLayout(mActivity);
            RelativeLayout.LayoutParams buttonContainerParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            buttonContainerParams.setMargins(0, 20, 0, 0);
            buttonContainer.setLayoutParams(buttonContainerParams);

            float density = mActivity.getResources().getDisplayMetrics().density;
            int width = mActivity.getResources().getDisplayMetrics().widthPixels;
            ImageView hDivider = new ImageView(mActivity);
            RelativeLayout.LayoutParams hDividerParams
                    = new RelativeLayout.LayoutParams(1, Math.round(48 * density));
            hDividerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            hDivider.setLayoutParams(hDividerParams);
            buttonContainer.addView(hDivider);

            float[] outerR = new float[]{10, 10, 10, 10, 10, 10, 10, 10};

            RoundRectShape roundRectShape = new RoundRectShape(outerR, null, null);
            ShapeDrawable drawable = new ShapeDrawable(roundRectShape);
            drawable.getPaint().setColor(Color.parseColor("#404040"));
            drawable.getPaint().setStyle(Paint.Style.FILL);

            Button confirmButton = new Button(mActivity);
            RelativeLayout.LayoutParams confirmButtonParams = new RelativeLayout.LayoutParams(
                    (width - Math.round(80 * density)) / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
            confirmButtonParams.addRule(RelativeLayout.LEFT_OF, hDivider.getId());
            confirmButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            confirmButton.setLayoutParams(confirmButtonParams);
            confirmButton.setGravity(Gravity.CENTER);
            confirmButton.setTextColor(Color.parseColor("#FFFFFF"));
            confirmButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            confirmButton.setText(mActivity.getString(R.string.pg_sdk_edit_ok));
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onQuitDialogConfirm();
                    }
                }
            });
            confirmButton.setBackgroundDrawable(drawable);
            buttonContainer.addView(confirmButton);

            Button closeButton = new Button(mActivity);
            RelativeLayout.LayoutParams closeButtonParams = new RelativeLayout.LayoutParams(
                    (width - Math.round(80 * density)) / 2, ViewGroup.LayoutParams.WRAP_CONTENT);
            closeButtonParams.addRule(RelativeLayout.RIGHT_OF, hDivider.getId());
            closeButtonParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            closeButton.setLayoutParams(closeButtonParams);
            closeButton.setGravity(Gravity.CENTER);
            closeButton.setTextColor(Color.parseColor("#9f9f9f"));
            closeButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            closeButton.setText(mActivity.getString(R.string.pg_sdk_edit_quit));
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            closeButton.setBackgroundDrawable(drawable);
            buttonContainer.addView(closeButton);

            container.addView(buttonContainer);
            root.addView(container);

            outerR = new float[]{20, 20, 20, 20, 20, 20, 20, 20};

            roundRectShape = new RoundRectShape(outerR, null, null);
            drawable = new ShapeDrawable(roundRectShape);
            drawable.getPaint().setColor(Color.parseColor("#343434"));
            drawable.getPaint().setStyle(Paint.Style.FILL);
            container.setBackgroundDrawable(drawable);

            setContentView(root);

            getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);

            setCanceledOnTouchOutside(false);
        }
    }
}
