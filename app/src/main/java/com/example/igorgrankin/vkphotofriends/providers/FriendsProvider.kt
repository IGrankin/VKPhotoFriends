package com.example.igorgrankin.vkphotofriends.providers
import android.content.Context
import com.example.igorgrankin.vkphotofriends.R
import com.example.igorgrankin.vkphotofriends.helpers.SharedPreferencesHelper
import com.example.igorgrankin.vkphotofriends.models.FriendModel
import com.example.igorgrankin.vkphotofriends.presenters.FriendsPresenter
import com.google.gson.JsonParser
import com.vk.sdk.api.*

class FriendsProvider(var presenter: FriendsPresenter) {
    private val TAG: String = FriendsProvider::class.java.simpleName

    fun loadFriends(context: Context?) {
        val request = VKApi.friends().get(VKParameters.from("order", "hints", VKApiConst.FIELDS,
            "city, photo_100, online"))
        request.executeWithListener(object: VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse) {
                super.onComplete(response)
                val jsonParser = JsonParser()
                var parsedJson = jsonParser.parse(response.json.toString()).asJsonObject
                val friendsList: ArrayList<FriendModel> = ArrayList()

                parsedJson.get("response").asJsonObject.getAsJsonArray("items").forEach {
                    val city = if (it.asJsonObject.get("city") == null) {
                        null
                    } else {
                        it.asJsonObject.get("city").asJsonObject.get("title").asString
                    }
                    val friend = FriendModel(
                        name = it.asJsonObject.get("first_name").asString,
                        surname = it.asJsonObject.get("last_name").asString,
                        city = city,
                        avatar = it.asJsonObject.get("photo_100").asString,
                        isOnline = it.asJsonObject.get("online").asInt == 1,
                        id = it.asJsonObject.get("id").asString)
                    friendsList.add(friend)

                }
                presenter.friendsLoaded(context = context, friendsList = friendsList)
            }

            override fun onError(error: VKError?) {
                super.onError(error)
                presenter.showError(textResource = R.string.friends_error_loading)
            }
        })

    }

    fun saveFriend(context: Context?, friendModel: FriendModel) {
        SharedPreferencesHelper().saveFriend(context, friendModel)
    }

    fun removeFriend(context: Context?, friendModel: FriendModel) {
        SharedPreferencesHelper().removeFriend(context, friendModel)
    }

    fun getSavedFriends(context: Context?): Set<String> {
        return SharedPreferencesHelper().getSavedFriends(context)
    }

}