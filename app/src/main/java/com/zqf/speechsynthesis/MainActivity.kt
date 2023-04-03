package com.zqf.speechsynthesis

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jzvd.Jzvd
import com.zqf.speechsynthesis.databinding.ActivityMainBinding
import com.zqf.speechsynthesis.jzvideo.DataSource
import com.zqf.speechsynthesis.jzvideo.DetailVideoActivity
import com.zqf.speechsynthesis.jzvideo.RecycleViewAdapter
import com.zqf.speechsynthesis.jzvideo.ViewAttr
import com.zqf.speechsynthesis.server.CheckService
import com.zqf.speechsynthesis.socket.AppSocket

/**
 * 过于简单的就不加注释了
 */
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

    /**
     * 初始化recycleview和数据
     */
    private fun initView() {
        Jzvd.TOOL_BAR_EXIST = false
        binding.recycler.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recycler.adapter = mRecycleAdapter
        //Adapter点击方法
        mRecycleAdapter.setOnVideoClick(object : RecycleViewAdapter.OnVideoClick {
            override fun videoClick(focusView: ViewGroup?, viewAttr: ViewAttr?, position: Int) {
                //点击画面跳转详情页面
                routerAct(viewAttr)
            }
        })
        mRecycleAdapter.setList(DataSource.getVideoList())
        startService(Intent(this, CheckService::class.java))
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
        DataSource.positionInList = -1
        Jzvd.releaseAllVideos()
        AppSocket.unconnect()
    }

    override fun onResume() {
        Jzvd.goOnPlayOnResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.goOnPlayOnPause()
    }
}