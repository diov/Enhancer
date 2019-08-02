package io.github.diov.xppreferences

import android.content.SharedPreferences
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler

/**
 * Created by Dio_V on 2019-05-27.
 * Copyright © 2019 diov.github.io. All rights reserved.
 */

internal class XpContentObserver(private val contentPreferences: SharedPreferences) : ContentObserver(Handler()) {

    private val innerListeners = mutableListOf<SharedPreferences.OnSharedPreferenceChangeListener>()

    override fun onChange(selfChange: Boolean, uri: Uri?) {
        super.onChange(selfChange, uri)
        val key = uri?.lastPathSegment
        innerListeners.forEach {
            it.onSharedPreferenceChanged(contentPreferences, key)
        }
    }

    fun addOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        listener?.let {
            innerListeners.add(it)
        }
    }

    fun removeOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        listener?.let {
            innerListeners.remove(it)
        }
    }

    fun validate(): Boolean {
        return innerListeners.isNotEmpty()
    }
}
