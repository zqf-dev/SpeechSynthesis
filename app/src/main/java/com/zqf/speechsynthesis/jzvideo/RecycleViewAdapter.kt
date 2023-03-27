package com.zqf.speechsynthesis.jzvideo

import android.view.ViewGroup
import android.widget.FrameLayout
import cn.jzvd.Jzvd
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zqf.speechsynthesis.R
import com.zqf.speechsynthesis.jzvideo.JzVdStdRv.ClickUi

/**
 * 列表adapter
 */
class RecycleViewAdapter(layoutResId: Int) :
    BaseQuickAdapter<String, BaseViewHolder>(layoutResId) {
    private var onVideoClick: OnVideoClick? = null
    private val videoTitle = "测试无缝续播"
    override fun convert(holder: BaseViewHolder, item: String) {
        //动态需要添加的播放器
        val jzVdStdRv: JzVdStdRv
        //位置
        val position = holder.layoutPosition
        //承装的容器
        val container = holder.getView<FrameLayout>(R.id.surface_container)
        //判断播放器状态
        if (JzVdStdRv.CURRENT_JZVD != null && AutoPlay.positionInList == position) {
            //拿到复用实例
            val parent = JzVdStdRv.CURRENT_JZVD.parent
            if (parent != null) {
                (parent as ViewGroup).removeView(JzVdStdRv.CURRENT_JZVD)
            }
            //移除掉容器的view
            container.removeAllViews()
            //添加b播放器实例
            container.addView(
                JzVdStdRv.CURRENT_JZVD, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            jzVdStdRv = JzVdStdRv.CURRENT_JZVD as JzVdStdRv
        } else {
            //相当于构造一个播放器实例
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
                //复用
                jzVdStdRv = container.getChildAt(0) as JzVdStdRv
            }
            //赋视频数据给播放器【可根据接口数据对应替换】
            jzVdStdRv.setUp(
                AutoPlay.getVideoList()[position],
                videoTitle,
                Jzvd.SCREEN_NORMAL
            )
        }
        //添加tag
        jzVdStdRv.id = R.id.jzvdplayer
        //添加
        jzVdStdRv.isAtList = true
        //增加点击事件
        jzVdStdRv.setClickUi(object : ClickUi {
            //切换时候
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

            //开始点击
            override fun onClickStart() {
                AutoPlay.positionInList = position
            }
        })
    }

    //外部调用方法
    fun setOnVideoClick(onVideoClick: OnVideoClick?) {
        this.onVideoClick = onVideoClick
    }

    //接口
    interface OnVideoClick {
        fun videoClick(focusView: ViewGroup?, viewAttr: ViewAttr?, position: Int)
    }
}