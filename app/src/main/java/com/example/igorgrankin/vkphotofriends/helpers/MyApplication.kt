package com.example.igorgrankin.vkphotofriends.helpers

import android.app.Application
import com.vk.sdk.VKSdk

class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        VKSdk.initialize(applicationContext)
    }
}
