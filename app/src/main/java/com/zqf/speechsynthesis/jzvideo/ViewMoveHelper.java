package com.zqf.speechsynthesis.jzvideo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

/**
 * 视图平移动画工具类
 */
public class ViewMoveHelper {
    private final ViewGroup targetView;
    private final ViewAttr fromViewInfo;
    private final ViewAttr toViewInfo;
    private final long duration;

    /**
     * @param targetView   目标布局
     * @param fromViewInfo 起始view坐标信息
     * @param toViewInfo   目标view坐标信息
     * @param duration     动画时长
     */
    public ViewMoveHelper(ViewGroup targetView, ViewAttr fromViewInfo, ViewAttr toViewInfo, long duration) {
        this.targetView = targetView;
        this.fromViewInfo = fromViewInfo;
        this.toViewInfo = toViewInfo;
        this.duration = duration;
    }

    public void startAnim() {
        ObjectAnimator xAnim = ObjectAnimator.ofFloat(targetView, "x", fromViewInfo.getX(), toViewInfo.getX());
        ObjectAnimator yAnim = ObjectAnimator.ofFloat(targetView, "y", fromViewInfo.getY(), toViewInfo.getY());
        ValueAnimator widthAnim = ValueAnimator.ofInt(fromViewInfo.getWidth(), toViewInfo.getWidth());
        ValueAnimator heightAnim = ValueAnimator.ofInt(fromViewInfo.getHeight(), toViewInfo.getHeight());
        widthAnim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams param = targetView.getLayoutParams();
            param.width = (int) valueAnimator.getAnimatedValue();
            targetView.setLayoutParams(param);
        });
        heightAnim.addUpdateListener(valueAnimator -> {
            ViewGroup.LayoutParams param = targetView.getLayoutParams();
            param.height = (int) valueAnimator.getAnimatedValue();
            targetView.setLayoutParams(param);
        });

        AnimatorSet animation = new AnimatorSet();
        animation.playTogether(xAnim, yAnim, widthAnim, heightAnim);
        animation.setDuration(duration);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
    }
}
