package com.zqf.speechsynthesis.jzvideo

import java.util.*

class AutoPlay {

    companion object {
        //记录当前播放列表位置
        var positionInList = -1

        /**
         * 初始化数据（此可换成接口数据）
         */
        @JvmStatic
        fun getVideoList(): MutableList<String> {
            val videoUrl1 = "https://v-cdn.zjol.com.cn/280443.mp4"
            val videoUrl2 = "https://v-cdn.zjol.com.cn/280444.mp4"
            val list: MutableList<String> = ArrayList()
            list.add(videoUrl1)
            list.add(videoUrl2)
            return list
        }
    }
}