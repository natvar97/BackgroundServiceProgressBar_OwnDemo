package com.indialone.backgroundserviceprogressbar

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {

    private lateinit var mProgressViewModel: ProgressViewModel
    private var mProgressService: ProgressService? = null

    private lateinit var btn: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgress: TextView

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mProgressViewModel =
            ViewModelProvider(this, ViewModelFactory()).get(ProgressViewModel::class.java)

        btn = findViewById(R.id.toggle_updates)
        progressBar = findViewById(R.id.progresss_bar)
        tvProgress = findViewById(R.id.text_view)

        setObservers()

        btn.setOnClickListener {
            makeUpdates()
        }

    }

    private fun makeUpdates() {
        if (mProgressService != null) {
            if (mProgressService!!.getProgress() == mProgressService!!.getMaxValue()) {
                mProgressService!!.resetTask()
                btn.text = "Start"
            } else {
                if (mProgressService!!.getIsPaused()) {
                    mProgressService!!.unPausePretendingLongRunningTask()
                    mProgressViewModel.setIsUpdating(true)
                } else {
                    mProgressService!!.pausePretendingLongRunningTask()
                    mProgressViewModel.setIsUpdating(false)
                }
            }
        }
    }

    private fun setObservers() {
        mProgressViewModel.getProgressBinder().observe(this) { binder ->
            if (binder != null) {
                mProgressService = binder.getService()
                Log.e(TAG, "Connected To the Progress Service")
            } else {
                mProgressService = null
                Log.e(TAG, "Disconnected From the Progress Service")
            }
        }

        mProgressViewModel.getIsUpdating().observe(this) { boolean ->
            val handler = Handler(Looper.getMainLooper())
            val runnable = object : Runnable {
                override fun run() {
                    if (boolean) {
                        if (mProgressViewModel.getProgressBinder().value != null) {
                            if (mProgressService?.getProgress() == mProgressService?.getMaxValue()) {
                                mProgressViewModel.setIsUpdating(false)
                            }
                            progressBar.progress = mProgressService!!.getProgress()
                            progressBar.max = mProgressService!!.getMaxValue()

                            val progress =
                                100 * mProgressService!!.getProgress() / mProgressService!!.getMaxValue()

                            tvProgress.text = "$progress"
                            handler.postDelayed(this, 100)

                        }
                    } else {
                        handler.removeCallbacks(this)
                    }
                }
            }

            if (boolean) {
                btn.text = "Pause"
                handler.postDelayed(runnable, 100)
            } else {
                if (mProgressService!!.getProgress() == mProgressService!!.getMaxValue())
                    btn.text = "Restart"
                else
                    btn.text = "Start"
            }

        }
    }

    private fun startService() {
        val intent = Intent(this, ProgressService::class.java)
        startService(intent)
        bindService()
    }

    private fun bindService() {
        val intent = Intent(this, ProgressService::class.java)
        bindService(intent, mProgressViewModel.getServiceConnection(), Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()

        startService()
    }

    override fun onPause() {
        super.onPause()

        unbindService(mProgressViewModel.getServiceConnection())
    }

}