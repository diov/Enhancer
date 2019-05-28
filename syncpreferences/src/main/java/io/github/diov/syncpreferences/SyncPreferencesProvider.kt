package io.github.diov.syncpreferences

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.core.content.edit

/**
 * Created by Dio_V on 2019-05-24.
 * Copyright © 2019 diov.github.io. All rights reserved.
 */

internal class SyncPreferencesProvider : ContentProvider() {

    private var preferences: SharedPreferences? = null
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    override fun onCreate(): Boolean {
        val preferences =
            context?.getSharedPreferences("SyncPreferencesProvider", Context.MODE_PRIVATE) ?: return false
        this.preferences = preferences
        setupUriMatcher()
        return true
    }

    private fun setupUriMatcher() {
        val context = context ?: return
        val authority = "${context.packageName}.syncPreferencesProvider"
        uriMatcher.addURI(authority, PATH_WILDCARD + CONTAINS, CONTAINS_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + GET_BOOLEAN, GET_BOOLEAN_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + GET_INT, GET_INT_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + GET_LONG, GET_LONG_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + GET_FLOAT, GET_FLOAT_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + GET_STRING, GET_STRING_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + GET_STRINGSET, GET_STRINGSET_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + GET_ALL, GET_ALL_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + PUT_BOOLEAN, PUT_BOOLEAN_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + PUT_INT, PUT_INT_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + PUT_LONG, PUT_LONG_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + PUT_FLOAT, PUT_FLOAT_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + PUT_STRING, PUT_STRING_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + PUT_STRINGSET, PUT_STRINGSET_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + REMOVE, REMOVE_CODE)
        uriMatcher.addURI(authority, PATH_WILDCARD + CLEAR, CLEAR_CODE)
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val editor = preferences?.edit() ?: return null
        val value = values?.valueSet()?.first() ?: error("Bad value")
        when (uriMatcher.match(uri)) {
            PUT_BOOLEAN_CODE -> editor.putBoolean(value.key, value.value as Boolean).apply()
            PUT_INT_CODE -> editor.putInt(value.key, value.value as Int).apply()
            PUT_LONG_CODE -> editor.putLong(value.key, value.value as Long).apply()
            PUT_FLOAT_CODE -> editor.putFloat(value.key, value.value as Float).apply()
            PUT_STRING_CODE -> editor.putString(value.key, value.value as String).apply()
        }
        context?.contentResolver?.notifyChange(Uri.withAppendedPath(uri, value.key), null)
        return null
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val preferences = preferences ?: return null
        if (!preferences.contains(selection)) {
            return null
        }
        val bundle = Bundle()
        when (uriMatcher.match(uri)) {
            CONTAINS_CODE -> bundle.putBoolean(QUERY_RESULT, preferences.contains(selection))
            GET_ALL_CODE -> bundle.putSerializable(QUERY_RESULT, preferences.all as HashMap)
            GET_BOOLEAN_CODE -> bundle.putBoolean(QUERY_RESULT, preferences.getBoolean(selection, false))
            GET_INT_CODE -> bundle.putInt(QUERY_RESULT, preferences.getInt(selection, 0))
            GET_LONG_CODE -> bundle.putLong(QUERY_RESULT, preferences.getLong(selection, 0L))
            GET_FLOAT_CODE -> bundle.putFloat(QUERY_RESULT, preferences.getFloat(selection, 0F))
            GET_STRING_CODE -> bundle.putString(QUERY_RESULT, preferences.getString(selection, ""))
            GET_STRINGSET_CODE -> {
                bundle.putStringArray(
                    QUERY_RESULT,
                    preferences.getStringSet(selection, emptySet<String>())?.toTypedArray()
                )
            }
            else -> error("Unsupported URI")
        }
        return ExtraCursor(bundle)
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        error("Unsupported function")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val preferences = preferences ?: return 0
        when (uriMatcher.match(uri)) {
            REMOVE_CODE -> preferences.edit { remove(selection) }
            CLEAR_CODE -> preferences.edit { clear() }
        }
        context?.contentResolver?.notifyChange(Uri.withAppendedPath(uri, selection), null)
        return 0
    }

    override fun getType(uri: Uri): String? = null

    companion object {
        const val PATH_WILDCARD = "*/"
        const val CONTAINS = "contains"
        private const val CONTAINS_CODE = 1
        const val GET_BOOLEAN = "getBoolean"
        private const val GET_BOOLEAN_CODE = 2
        const val GET_INT = "getInt"
        private const val GET_INT_CODE = 3
        const val GET_LONG = "getLong"
        private const val GET_LONG_CODE = 4
        const val GET_FLOAT = "getFloat"
        private const val GET_FLOAT_CODE = 5
        const val GET_STRING = "getString"
        private const val GET_STRING_CODE = 6
        const val GET_STRINGSET = "getStringSet"
        private const val GET_STRINGSET_CODE = 7
        const val GET_ALL = "getAll"
        private const val GET_ALL_CODE = 8
        const val PUT_BOOLEAN = "putBoolean"
        private const val PUT_BOOLEAN_CODE = 9
        const val PUT_INT = "putInt"
        private const val PUT_INT_CODE = 10
        const val PUT_LONG = "putLong"
        private const val PUT_LONG_CODE = 11
        const val PUT_FLOAT = "putFloat"
        private const val PUT_FLOAT_CODE = 12
        const val PUT_STRING = "putString"
        private const val PUT_STRING_CODE = 13
        const val PUT_STRINGSET = "putStringSet"
        private const val PUT_STRINGSET_CODE = 14
        const val REMOVE = "remove"
        private const val REMOVE_CODE = 15
        const val CLEAR = "clear"
        private const val CLEAR_CODE = 16

        const val QUERY_RESULT = "query_result"
    }
}
