package com.zqf.speechsynthesis.jzvideo

import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import cn.jzvd.JZUtils
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.zqf.speechsynthesis.MainActivity.Companion.mainActivity
import com.zqf.speechsynthesis.databinding.ActivityDetailBinding

class DetailVideoActivity : AppCompatActivity() {

    private val mDURATION: Long = 250
    private var attr: ViewAttr? = null
    private var currentAttr: ViewAttr? = null
    lateinit var detailBinding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailBinding = ActivityDetailBinding.inflate(layoutInflater)
        JZUtils.hideSystemUI(this)
        JZUtils.hideStatusBar(this)
        setContentView(detailBinding.root)
        attr = intent.getParcelableExtra("attr")
        detailBinding.surfaceContainer.viewTreeObserver
            .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    detailBinding.surfaceContainer.viewTreeObserver.removeOnPreDrawListener(this)
                    val parent = JzVdStdRv.CURRENT_JZVD.parent
                    if (parent != null) {
                        (parent as ViewGroup).removeView(JzVdStdRv.CURRENT_JZVD)
                    }
                    detailBinding.surfaceContainer.addView(
                        JzVdStdRv.CURRENT_JZVD, FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )
                    currentAttr = ViewAttr()
                    val location = IntArray(2)
                    detailBinding.surfaceContainer.getLocationInWindow(location)
                    currentAttr!!.x = location[0]
                    currentAttr!!.y = location[1]
                    currentAttr!!.width = detailBinding.surfaceContainer.measuredWidth
                    currentAttr!!.height = detailBinding.surfaceContainer.measuredHeight
                    ViewMoveHelper(
                        detailBinding.surfaceContainer,
                        attr,
                        currentAttr,
                        mDURATION
                    ).startAnim()
                    val animation = AlphaAnimation(0f, 1f)
                    animation.duration = mDURATION
                    animation.start()
                    return true
                }
            })
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) return
        backAnimation()
    }

    private fun backAnimation() {
        ViewMoveHelper(detailBinding.surfaceContainer, currentAttr, attr, mDURATION).startAnim()
        detailBinding.surfaceContainer.postDelayed({
            mainActivity!!.animateFinish()
            finish()
            overridePendingTransition(0, 0)
        }, mDURATION)
    }
}