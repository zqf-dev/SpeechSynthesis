package com.zqf.speechsynthesis.jzvideo

import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import cn.jzvd.JZUtils
import cn.jzvd.Jzvd
import com.zqf.speechsynthesis.MainActivity.Companion.mainActivity
import com.zqf.speechsynthesis.databinding.ActivityDetailBinding

class DetailVideoActivity : AppCompatActivity() {

    //动画时常
    private val mDURATION: Long = 250
    //列表页传过来的ViewAttr
    private var attr: ViewAttr? = null
    //此页的ViewAttr
    private var currentAttr: ViewAttr? = null
    lateinit var detailBinding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        JZUtils.hideSystemUI(this)
        JZUtils.hideStatusBar(this)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(detailBinding.root)
        //拿到view
        attr = intent.getParcelableExtra("attr")
        /**
         * 监听视图树的观察者
         * 目的：在跳转到此页面时，页面试图构建中做操作，达到无缝效果
         */
        detailBinding.surfaceContainer.viewTreeObserver
            .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                //当视图树将要绘制时，所要调用的回调函数的接口类
                override fun onPreDraw(): Boolean {
                    //防止空
                    detailBinding.surfaceContainer.viewTreeObserver.removeOnPreDrawListener(this)
                    //拿到父类
                    val parent = JzVdStdRv.CURRENT_JZVD.parent
                    if (parent != null) {
                        (parent as ViewGroup).removeView(JzVdStdRv.CURRENT_JZVD)
                    }
                    //添加JzVdStdRv实例到容器
                    detailBinding.surfaceContainer.addView(
                        JzVdStdRv.CURRENT_JZVD, FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )
                    //构造
                    currentAttr = ViewAttr()
                    //实例化一个Int的数组
                    val location = IntArray(2)
                    //拿到容器FrameLayout的数据
                    detailBinding.surfaceContainer.getLocationInWindow(location)
                    //赋值具体的位置
                    currentAttr!!.x = location[0]
                    currentAttr!!.y = location[1]
                    currentAttr!!.width = detailBinding.surfaceContainer.measuredWidth
                    currentAttr!!.height = detailBinding.surfaceContainer.measuredHeight
                    /**
                     * 执行动画方法
                     * 相当于将attr的数据换到详情页的容器位置上
                     * 动画时常mDURATION
                     */
                    ViewMoveHelper(
                        detailBinding.surfaceContainer,
                        attr,
                        currentAttr,
                        mDURATION
                    ).startAnim()
                    //增加一个渐变的动画形式
                    val animation = AlphaAnimation(0f, 1f)
                    animation.duration = mDURATION
                    animation.start()
                    return true
                }
            })
    }

    //finish退出时的动画复原
    private fun backAnimation() {
        ViewMoveHelper(detailBinding.surfaceContainer, currentAttr, attr, mDURATION).startAnim()
        detailBinding.surfaceContainer.postDelayed({
            mainActivity!!.animateFinish()
            finish()
            overridePendingTransition(0, 0)
        }, mDURATION)
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) return
        backAnimation()
    }
}
