package io.github.diov.sample

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import androidx.core.os.postDelayed
import io.github.diov.syncpreferance.getSyncPreferences

/**
 * Created by Dio_V on 2019-05-27.
 * Copyright © 2019 diov.github.io. All rights reserved.
 */

class ForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val syncPreferences = getSyncPreferences()
        syncPreferences.registerOnSharedPreferenceChangeListener { sp, key ->
            println("$key: ${sp.all[key]}")
            println("null: ${sp.getBoolean("null", false)}")
        }
        Handler().postDelayed(10000) {
            println("timeout!")
        }
        return START_STICKY
    }
}
