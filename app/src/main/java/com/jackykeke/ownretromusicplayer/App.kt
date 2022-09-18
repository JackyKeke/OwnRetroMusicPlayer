package com.jackykeke.ownretromusicplayer

import android.app.Application
import code.name.monkey.retromusic.billing.BillingManager

/**
 * @author keyuliang on 2022/9/14.
 * @version 9999.0.0
 * @descrption 描述 ：
 * @copy 版权当然属于 keyuliang
 */
class App : Application(){

    lateinit var billingManager: BillingManager

    companion object {
        private var instance: App? = null

        fun getContext(): App {
            return instance!!
        }

        fun isProVersion(): Boolean {
            return BuildConfig.DEBUG || instance?.billingManager!!.isProVersion
        }
    }

    override fun onCreate() {
        super.onCreate()
    }

}