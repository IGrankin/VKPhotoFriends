package com.example.igorgrankin.vkphotofriends.helpers

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import com.example.igorgrankin.vkphotofriends.models.FriendModel

class SharedPreferencesHelper {
    private val TAG: String = SharedPreferencesHelper::class.java.simpleName
    companion object {
        @SuppressLint("CommitPrefEdits")
        @JvmStatic
        fun saveFriend(context: Context?, friendModel: FriendModel) {
            context?.let {
                val preferences = getSharedPreferencesWithContext(it)
                val savedIds: Set<String>? = preferences.getStringSet("ids", setOf())
                if (!savedIds!!.contains(friendModel.id)) {
                    var newIds: MutableSet<String> = mutableSetOf()
                    newIds.addAll(savedIds)
                    newIds.add(friendModel.id)
                    preferences.edit().putStringSet("ids", newIds).commit()

                }
            }
        }

        @JvmStatic
        fun removeFriend(context: Context?, friendModel: FriendModel) {
            context?.let {
                var preferences = getSharedPreferencesWithContext(it)
                val savedIds:Set<String> = preferences.getStringSet("ids", setOf())
                if (savedIds.contains(friendModel.id)) {
                    var newIds: MutableSet<String> = mutableSetOf()
                    newIds.addAll(savedIds)
                    newIds.remove(friendModel.id)
                    preferences.edit().putStringSet("ids", newIds).commit()
                }
            }
        }

        @JvmStatic
        fun getSharedPreferencesWithContext(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        @JvmStatic
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

}