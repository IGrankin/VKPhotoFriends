package com.example.igorgrankin.vkphotofriends.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.example.igorgrankin.vkphotofriends.models.FriendModel

class SharedPreferencesHelper {
    private val TAG: String = SharedPreferencesHelper::class.java.simpleName

    @SuppressLint("CommitPrefEdits")
    fun saveFriend(context: Context?, friendModel: FriendModel) {
        context?.let {
            val preferences = getSharedPreferencesWithContext(it)
            val savedIds: Set<String>? = preferences.getStringSet("ids", setOf())
            if (!savedIds!!.contains(friendModel.id)) {
                var newIds: MutableSet<String> = mutableSetOf()
                newIds.addAll(savedIds)
                newIds.add(friendModel.id)
                val prefs = preferences.edit().putStringSet("ids", newIds)
                prefs.commit()
                //Todo исправить на apply()
            }
        }
    }


    fun removeFriend(context: Context?, friendModel: FriendModel) {
        context?.let {
            var preferences = getSharedPreferencesWithContext(it)
            val savedIds:Set<String> = preferences.getStringSet("ids", setOf())
            if (savedIds.contains(friendModel.id)) {
                var newIds: MutableSet<String> = mutableSetOf()
                newIds.addAll(savedIds)
                newIds.remove(friendModel.id)
                val prefs = preferences.edit().putStringSet("ids", newIds)
                prefs.commit()
            }
        }
    }

    fun getSharedPreferencesWithContext(context: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getSavedFriends(context: Context?): Set<String> {
        context?.let {
            var preferences = getSharedPreferencesWithContext(it)
            val savedIds:Set<String> = preferences.getStringSet("ids", setOf())
            var newIds: MutableSet<String> = mutableSetOf()
            newIds.addAll(savedIds)
            return newIds
        }
        return setOf()
    }
}