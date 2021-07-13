package com.indialone.backgroundserviceprogressbar

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProgressViewModel : ViewModel() {

    private val TAG = "ProgressViewModel"

    private var mIsUpdatingProgress = MutableLiveData<Boolean>()
    private var mBinder = MutableLiveData<ProgressService.ProgressBinder>()

    private var serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.e(TAG, "Connected to The Service")
            val binder = service as ProgressService.ProgressBinder
            mBinder.postValue(binder)
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Log.e(TAG, "Disconnected to The Service")
            mBinder.postValue(null)
        }
    }

    fun getServiceConnection(): ServiceConnection {
        return serviceConnection
    }

    fun getProgressBinder(): LiveData<ProgressService.ProgressBinder> {
        return mBinder
    }

    fun getIsUpdating(): LiveData<Boolean> {
        return mIsUpdatingProgress
    }

    fun setIsUpdating(isUpdating: Boolean) {
        mIsUpdatingProgress.postValue(isUpdating)
    }


}