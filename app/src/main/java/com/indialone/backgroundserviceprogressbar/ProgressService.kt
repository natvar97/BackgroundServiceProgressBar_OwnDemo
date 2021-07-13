package com.indialone.backgroundserviceprogressbar

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log

class ProgressService : Service() {

    private var mBinder = ProgressBinder()
    private var mIsPaused: Boolean? = null
    private lateinit var mHandler: Handler
    private var mProgress: Int? = null
    private var mMaxValue: Int? = null
    private val TAG = "ProgressService"

    override fun onCreate() {
        super.onCreate()

        mIsPaused = true
        mHandler = Handler(Looper.getMainLooper())
        mProgress = 0
        mMaxValue = 5000

    }


    inner class ProgressBinder : Binder() {
        fun getService(): ProgressService {
            return this@ProgressService
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    private fun startPretendingLongRunningTask() {
        val runner = object : Runnable {
            override fun run() {
                if (mProgress!! >= mMaxValue!! || mIsPaused!!) {
                    Log.e(TAG, "removing task")
                    mHandler.removeCallbacks(this)
                    pausePretendingLongRunningTask()
                } else {
                    Log.e(TAG, "pause the task")
                    mProgress = mProgress!! + 100
                    Log.e(TAG , "$mProgress")
                    mHandler.postDelayed(this, 100)
                }
            }
        }

        mHandler.postDelayed(runner, 100)
    }

    fun pausePretendingLongRunningTask() {
        mIsPaused = true
    }

    fun getProgress(): Int {
        return mProgress!!
    }

    fun unPausePretendingLongRunningTask() {
        mIsPaused = false
        startPretendingLongRunningTask()
    }

    fun getIsPaused(): Boolean {
        return mIsPaused!!
    }

    fun getMaxValue(): Int {
        return mMaxValue!!
    }

    fun resetTask() {
        mProgress = 0
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

}