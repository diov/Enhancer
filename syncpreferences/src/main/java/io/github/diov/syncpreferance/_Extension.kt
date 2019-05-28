@file:Suppress("unused", "NOTHING_TO_INLINE")

package io.github.diov.syncpreferance

import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by Dio_V on 2019-05-24.
 * Copyright © 2019 diov.github.io. All rights reserved.
 */

fun Context.getSyncPreferences(): SharedPreferences {
    return SyncPreferences.instance(this)
}

private inline fun <T> SharedPreferences.delegate(
    defaultValue: T,
    key: String?,
    crossinline getter: SharedPreferences.(String, T) -> T,
    crossinline setter: SharedPreferences.Editor.(String, T) -> SharedPreferences.Editor
): ReadWriteProperty<Any, T> {
    return object : ReadWriteProperty<Any, T> {
        override fun getValue(thisRef: Any, property: KProperty<*>) =
            getter(key ?: property.name, defaultValue)

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            if (null == value) {
                edit().remove(key ?: property.name).apply()
            } else {
                edit().setter(key ?: property.name, value).apply()
            }
        }
    }
}

fun SharedPreferences.int(def: Int = 0, key: String? = null) =
    delegate(def, key, SharedPreferences::getInt, SharedPreferences.Editor::putInt)

fun SharedPreferences.long(def: Long = 0, key: String? = null) =
    delegate(def, key, SharedPreferences::getLong, SharedPreferences.Editor::putLong)

fun SharedPreferences.boolean(def: Boolean = false, key: String? = null) =
    delegate(def, key, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)

fun SharedPreferences.string(def: String = "", key: String? = null) =
    delegate(def, key, SharedPreferences::getString, SharedPreferences.Editor::putString)

fun SharedPreferences.stringOrNull(def: String? = null, key: String? = null) =
    delegate(def, key, SharedPreferences::getString, SharedPreferences.Editor::putString)
