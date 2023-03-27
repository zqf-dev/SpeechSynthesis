package com.zqf.speechsynthesis.jzvideo

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator

class ViewMoveHelper(
    private val targetView: ViewGroup,
    private val fromViewInfo: ViewAttr?,
    private val toViewInfo: ViewAttr?,
    private val duration: Long
) {

    fun startAnim() {
        val xAnim: ObjectAnimator = ObjectAnimator.ofFloat(
            targetView,
            "x",
            fromViewInfo!!.x.toFloat(),
            toViewInfo!!.x.toFloat()
        )
        val yAnim: ObjectAnimator = ObjectAnimator.ofFloat(
            targetView,
            "y",
            fromViewInfo.y.toFloat(),
            toViewInfo.y.toFloat()
        )
        val widthAnim = ValueAnimator.ofInt(fromViewInfo.width, toViewInfo.width)
        val heightAnim = ValueAnimator.ofInt(fromViewInfo.height, toViewInfo.height)
        widthAnim.addUpdateListener { valueAnimator: ValueAnimator ->
            val param: ViewGroup.LayoutParams = targetView.layoutParams
            param.width = valueAnimator.animatedValue as Int
            targetView.layoutParams = param
        }
        heightAnim.addUpdateListener { valueAnimator: ValueAnimator ->
            val param: ViewGroup.LayoutParams = targetView.layoutParams
            param.height = valueAnimator.animatedValue as Int
            targetView.layoutParams = param
        }
        val animation = AnimatorSet()
        animation.playTogether(xAnim, yAnim, widthAnim, heightAnim)
        animation.duration = duration
        animation.interpolator = DecelerateInterpolator()
        animation.start()
    }
}