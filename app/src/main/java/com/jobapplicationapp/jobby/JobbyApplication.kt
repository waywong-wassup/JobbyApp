package com.jobapplicationapp.jobby

import com.jobapplicationapp.jobby.data.AppContainer
import com.jobapplicationapp.jobby.data.AppDataContainer
import android.app.Application

class JobbyApplication : Application() {
    // for holding app container
    lateinit var container: AppContainer

    //override the default onCreate
    override fun onCreate() {
        super.onCreate()
        //where we put appDataContainer in AppContainer.kt so that app has all data to start
        container = AppDataContainer(this)
    }
}