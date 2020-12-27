package com.example.dishu_handler

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.lang.ref.WeakReference
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener, OnTouchListener {
    private var mResultTextView: TextView? = null
    private var mTianYiImageView: ImageView? = null
    private var mStartButton: Button? = null
    var mPosition = arrayOf(intArrayOf(342, 180), intArrayOf(432, 880), intArrayOf(521, 256), intArrayOf(429, 780), intArrayOf(456, 976), intArrayOf(145, 665), intArrayOf(123, 678), intArrayOf(564, 567))
    private var mTotalCount = 0  //刷新个数
    private var mSuccessCount = 0 //成功击打个数
    companion object {
        const val MSG_SIGN = 123 //message标识符
        const val MAX_COUNT = 10 //总个数
    }
    private val mHandler = DiglettHandler(this) //实例化handler对象
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        title = "打地鼠"
    }

    private fun initView() { //绑定视图
        mResultTextView = findViewById<View>(R.id.textview) as TextView
        mTianYiImageView = findViewById<View>(R.id.imageView) as ImageView
        mStartButton = findViewById<View>(R.id.btn_start) as Button
        mStartButton!!.setOnClickListener(this)
        mTianYiImageView!!.setOnTouchListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_start -> start()
        }
    }

    private fun start() {
        mResultTextView!!.text = "开始啦"
        mStartButton!!.text = "游戏中……"
        mStartButton!!.isEnabled = false
        next(0)
    }

    private fun next(delayTime: Int) { //设置刷新时间间隔，出现位置
        val position = Random().nextInt(mPosition.size)
        val message = Message.obtain()
        message.what = MSG_SIGN
        message.arg1 = position
        mHandler.sendMessageDelayed(message, delayTime.toLong()) //指定多少毫秒后发送
        mTotalCount++
    }

    private fun clear() {  //重新开始
        mTotalCount = 0
        mSuccessCount = 0
        mTianYiImageView!!.visibility = View.GONE
        mStartButton!!.text = "开始"
        mStartButton!!.isEnabled = true
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean { //计分函数
        v.visibility = View.GONE //隐藏控件
        mSuccessCount++
        mResultTextView!!.text = "命中：" + mSuccessCount + "共：" + MAX_COUNT
        return false
    }

    class DiglettHandler(activity: MainActivity) : Handler() { //继承handler类
        private val mWeakReference: WeakReference<MainActivity>
        init {                                                   //弱引用，防止内存泄漏
            mWeakReference = WeakReference(activity)
        }
        companion object { //伴随对象，相当于Java的静态属性
            private const val RANDOM_NUM = 500
        }
        override fun handleMessage(msg: Message) { //重写handelmessage
            super.handleMessage(msg)
            val activity = mWeakReference.get()
            when (msg.what) {
                MSG_SIGN -> {
                    if (activity!!.mTotalCount > MAX_COUNT) {
                        activity.clear()
                        Toast.makeText(activity, "游戏结束", Toast.LENGTH_SHORT).show()
                        return
                    }
                    val postion = msg.arg1 //二维数组行数
                    activity.mTianYiImageView!!.x = activity.mPosition[postion][0].toFloat()//设置控件位置
                    activity.mTianYiImageView!!.y = activity.mPosition[postion][1].toFloat()
                    activity.mTianYiImageView!!.visibility = View.VISIBLE //设置控件可见
                    val randomTime = Random().nextInt(RANDOM_NUM) + RANDOM_NUM
                    activity.next(randomTime) //刷新间隔时间
                }
            }
        }
    }
}