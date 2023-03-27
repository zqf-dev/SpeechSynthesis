package com.zqf.speechsynthesis

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jzvd.JZUtils
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.zqf.speechsynthesis.databinding.ActivityMainBinding
import com.zqf.speechsynthesis.jzvideo.AutoPlay
import com.zqf.speechsynthesis.jzvideo.DetailVideoActivity
import com.zqf.speechsynthesis.jzvideo.RecycleViewAdapter
import com.zqf.speechsynthesis.jzvideo.ViewAttr

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    companion object {
        @JvmStatic
        var mainActivity: MainActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainActivity = this
        initView()
    }

    private val mRecycleAdapter by lazy {
        RecycleViewAdapter(R.layout.item_layout)
    }

    private fun initView() {
        JZUtils.hideSystemUI(this)
        JZUtils.hideStatusBar(this)
        Jzvd.TOOL_BAR_EXIST = false
        binding.recycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycler.adapter = mRecycleAdapter
        mRecycleAdapter.setOnVideoClick(object : RecycleViewAdapter.OnVideoClick {
            override fun videoClick(focusView: ViewGroup?, viewAttr: ViewAttr?, position: Int) {
                routerAct(viewAttr)
            }
        })
        mRecycleAdapter.setList(AutoPlay.getVideoList())
    }

    private fun routerAct(viewAttr: ViewAttr?) {
        intent = Intent(this, DetailVideoActivity::class.java)
        intent.putExtra("attr", viewAttr)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    fun animateFinish() {
        mRecycleAdapter.notifyDataSetChanged()
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) return
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        Jzvd.TOOL_BAR_EXIST = true
        mainActivity = null
        AutoPlay.positionInList = -1
        Jzvd.releaseAllVideos()
    }
}