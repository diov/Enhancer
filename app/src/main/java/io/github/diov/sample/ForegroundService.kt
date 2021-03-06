package io.github.diov.sample

import android.app.Service
import android.content.Intent
import android.os.IBinder
import io.github.diov.syncpreferences.getSyncPreferences

/**
 * Created by Dio_V on 2019-05-27.
 * Copyright © 2019 diov.github.io. All rights reserved.
 */

class ForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val syncPreferences = getSyncPreferences("default")
        syncPreferences.registerOnSharedPreferenceChangeListener { sp, key ->
            println("$key: ${sp.all[key]}")
            println("null: ${sp.getBoolean("null", false)}")
        }
        println("${syncPreferences.all.count()}")
        return START_STICKY
    }
}
