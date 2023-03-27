package com.zqf.speechsynthesis.jzvideo

import android.view.ViewGroup
import android.widget.FrameLayout
import cn.jzvd.Jzvd
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zqf.speechsynthesis.R
import com.zqf.speechsynthesis.jzvideo.JzVdStdRv.ClickUi


class RecycleViewAdapter(layoutResId: Int) :
    BaseQuickAdapter<String, BaseViewHolder>(layoutResId) {
    private var onVideoClick: OnVideoClick? = null
    private val videoTitle = "测试无缝续播"
    override fun convert(holder: BaseViewHolder, item: String) {
        val jzVdStdRv: JzVdStdRv
        val position = holder.layoutPosition
        val container = holder.getView<FrameLayout>(R.id.surface_container)
        if (JzVdStdRv.CURRENT_JZVD != null && AutoPlay.positionInList == position) {
            val parent = JzVdStdRv.CURRENT_JZVD.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(JzVdStdRv.CURRENT_JZVD)
            }
            container.removeAllViews()
            container.addView(
                JzVdStdRv.CURRENT_JZVD, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            jzVdStdRv = JzVdStdRv.CURRENT_JZVD as JzVdStdRv
        } else {
            if (container.childCount == 0) {
                jzVdStdRv = JzVdStdRv(container.context)
                container.addView(
                    jzVdStdRv,
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
            } else {
                jzVdStdRv = container.getChildAt(0) as JzVdStdRv
            }
            jzVdStdRv.setUp(
                AutoPlay.getVideoList()[position],
                videoTitle,
                Jzvd.SCREEN_NORMAL
            )
        }
        jzVdStdRv.id = R.id.jzvdplayer
        jzVdStdRv.isAtList = true
        jzVdStdRv.setClickUi(object : ClickUi {
            override fun onClickUiToggle() {
                AutoPlay.positionInList = position
                jzVdStdRv.isAtList = false
                val attr = ViewAttr()
                val location = IntArray(2)
                container.getLocationInWindow(location)
                attr.x = location[0]
                attr.y = location[1]
                attr.width = container.measuredWidth
                attr.height = container.measuredHeight
                onVideoClick?.videoClick(container, attr, position)
                jzVdStdRv.setClickUi(null)
            }

            override fun onClickStart() {
                AutoPlay.positionInList = position
            }
        })
    }

    fun setOnVideoClick(onVideoClick: OnVideoClick?) {
        this.onVideoClick = onVideoClick
    }

    interface OnVideoClick {
        fun videoClick(focusView: ViewGroup?, viewAttr: ViewAttr?, position: Int)
    }
}