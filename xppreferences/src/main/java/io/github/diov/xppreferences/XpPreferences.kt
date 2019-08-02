package io.github.diov.xppreferences

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.ContentObserver
import android.net.Uri
import io.github.diov.xppreferences.XpPreferencesProvider.Companion.QUERY_RESULT

/**
 * Created by Dio_V on 2019-05-24.
 * Copyright © 2019 diov.github.io. All rights reserved.
 */

internal class XpPreferences private constructor(
    private val context: Context,
    private val name: String,
    private val packageName: String? = null
) :
    SharedPreferences, SharedPreferences.Editor {

    private val contentResolver: ContentResolver = context.contentResolver
    private var contentObserver: ContentObserver? = null

    override fun contains(key: String?): Boolean {
        val uri = parseUri(XpPreferencesProvider.CONTAINS)
        val cursor = contentResolver.query(uri, null, key, null, null)
        return if (null == cursor) {
            false
        } else {
            cursor.moveToFirst()
            val result = cursor.extras.getBoolean(QUERY_RESULT)
            cursor.close()
            result
        }
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        val uri = parseUri(XpPreferencesProvider.GET_BOOLEAN)
        val cursor = contentResolver.query(uri, null, key, null, null)
        return if (null == cursor) {
            defValue
        } else {
            cursor.moveToFirst()
            val result = cursor.extras.getBoolean(QUERY_RESULT)
            cursor.close()
            result
        }
    }

    override fun getInt(key: String?, defValue: Int): Int {
        val uri = parseUri(XpPreferencesProvider.GET_INT)
        val cursor = contentResolver.query(uri, null, key, null, null)
        return if (null == cursor) {
            defValue
        } else {
            cursor.moveToFirst()
            val result = cursor.extras.getInt(QUERY_RESULT)
            cursor.close()
            result
        }
    }

    override fun getLong(key: String?, defValue: Long): Long {
        val uri = parseUri(XpPreferencesProvider.GET_LONG)
        val cursor = contentResolver.query(uri, null, key, null, null)
        return if (null == cursor) {
            defValue
        } else {
            cursor.moveToFirst()
            val result = cursor.extras.getLong(QUERY_RESULT)
            cursor.close()
            result
        }
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        val uri = parseUri(XpPreferencesProvider.GET_FLOAT)
        val cursor = contentResolver.query(uri, null, key, null, null)
        return if (null == cursor) {
            defValue
        } else {
            cursor.moveToFirst()
            val result = cursor.extras.getFloat(QUERY_RESULT)
            cursor.close()
            result
        }
    }

    override fun getString(key: String?, defValue: String?): String? {
        val uri = parseUri(XpPreferencesProvider.GET_STRING)
        val cursor = contentResolver.query(uri, null, key, null, null)
        return if (null == cursor) {
            defValue
        } else {
            cursor.moveToFirst()
            val result = cursor.extras.getString(QUERY_RESULT)
            cursor.close()
            result
        }
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
        val uri = parseUri(XpPreferencesProvider.GET_STRINGSET)
        val cursor = contentResolver.query(uri, null, key, null, null)
        return if (null == cursor) {
            defValues
        } else {
            cursor.moveToFirst()
            val result = cursor.extras.getStringArray(QUERY_RESULT)?.toMutableSet() ?: defValues
            cursor.close()
            result
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun getAll(): MutableMap<String, *> {
        val uri = parseUri(XpPreferencesProvider.GET_ALL)
        val cursor = contentResolver.query(uri, null, null, null, null)
        return if (null == cursor) {
            mutableMapOf<String, Any>()
        } else {
            cursor.moveToFirst()
            val bundle = cursor.extras
            val result = bundle.getSerializable(QUERY_RESULT) as HashMap<String, Any>
            cursor.close()
            return result
        }
    }

    override fun edit(): SharedPreferences.Editor {
        return this
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        val observer = this.contentObserver ?: XpContentObserver(this).also {
            val uri = parseUri()
            contentResolver.registerContentObserver(uri, true, it)
            this.contentObserver = it
        }
        (observer as XpContentObserver).addOnSharedPreferenceChangeListener(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        val observer = this.contentObserver as? XpContentObserver ?: return
        observer.removeOnSharedPreferenceChangeListener(listener)
        if (!observer.validate()) {
            contentResolver.unregisterContentObserver(observer)
        }
    }

    override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
        val uri = parseUri(XpPreferencesProvider.PUT_LONG)
        val contentValue = ContentValues().apply {
            put(key, value)
        }
        contentResolver.insert(uri, contentValue)
        return this
    }

    override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
        val uri = parseUri(XpPreferencesProvider.PUT_INT)
        val contentValue = ContentValues().apply {
            put(key, value)
        }
        contentResolver.insert(uri, contentValue)
        return this
    }

    override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
        val uri = parseUri(XpPreferencesProvider.PUT_BOOLEAN)
        val contentValue = ContentValues().apply {
            put(key, value)
        }
        contentResolver.insert(uri, contentValue)
        return this
    }

    override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
        val uri = parseUri(XpPreferencesProvider.PUT_FLOAT)
        val contentValue = ContentValues().apply {
            put(key, value)
        }
        contentResolver.insert(uri, contentValue)
        return this
    }

    override fun putString(key: String?, value: String?): SharedPreferences.Editor {
        val uri = parseUri(XpPreferencesProvider.PUT_STRING)
        val contentValue = ContentValues().apply {
            put(key, value)
        }
        contentResolver.insert(uri, contentValue)
        return this
    }

    override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
        return this
    }

    override fun clear(): SharedPreferences.Editor {
        val uri = parseUri(XpPreferencesProvider.CLEAR)
        contentResolver.delete(uri, null, null)
        return this
    }

    override fun remove(key: String?): SharedPreferences.Editor {
        val uri = parseUri(XpPreferencesProvider.REMOVE)
        contentResolver.delete(uri, key, null)
        return this
    }

    override fun commit(): Boolean = true

    override fun apply() = Unit

    private fun parseUri(path: String = ""): Uri {
        return Uri.parse("content://${packageName ?: context.packageName}.syncPreferencesProvider/$name/$path")
    }

    companion object {
        private var syncPreferences: SharedPreferences? = null

        fun instance(context: Context, name: String): SharedPreferences {
            return syncPreferences ?: XpPreferences(context, name).also { syncPreferences = it }
        }
    }
}
