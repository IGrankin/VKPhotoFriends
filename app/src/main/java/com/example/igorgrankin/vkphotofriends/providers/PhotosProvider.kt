package com.example.igorgrankin.vkphotofriends.providers

import android.content.Context
import com.example.igorgrankin.vkphotofriends.R
import com.example.igorgrankin.vkphotofriends.helpers.SharedPreferencesHelper
import com.example.igorgrankin.vkphotofriends.models.FriendModel
import com.example.igorgrankin.vkphotofriends.models.PhotoModel
import com.example.igorgrankin.vkphotofriends.presenters.PhotosPresenter
import com.google.gson.JsonParser
import com.vk.sdk.api.*

class PhotosProvider(var presenter: PhotosPresenter) {
    private val TAG: String = PhotosPresenter::class.java.simpleName
    fun loadPhotos(friendsSet: Set<String>, silentMode: Boolean) {
        var ids = friendsSet.toString()
        ids = ids.replace("[", "")
        ids = ids.replace("]", "")
        val request = VKRequest("newsfeed.get", VKParameters.from(
            VKApiConst.FILTERS, "photo, wall_photo, photo_tag",
            "source_ids", ids,
            "max_photos", "1",
            VKApiConst.FIELDS, "id, first_name, last_name, photo_100",
            VKApiConst.COUNT, "100"))
        request.executeWithListener(object: VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse) {
                super.onComplete(response)
                val jsonParser = JsonParser()
                var parsedJson = jsonParser.parse(response.json.toString()).asJsonObject
                var photosList: ArrayList<PhotoModel> = ArrayList()
                parsedJson.get("response").asJsonObject.getAsJsonArray("items").forEach {photoObject ->
                    parsedJson.get("response").asJsonObject.getAsJsonArray(("profiles")).forEach {profileObject ->
                        val photoObjectId = photoObject.asJsonObject.get("source_id").asString
                        val profileObjectId = profileObject.asJsonObject.get("id").asString
                        if (photoObjectId == profileObjectId) {
                            val friend = FriendModel(
                                name = profileObject.asJsonObject.get("first_name").asString,
                                surname = profileObject.asJsonObject.get("last_name").asString,
                                city = "",
                                avatar = profileObject.asJsonObject.get("photo_100").asString,
                                isOnline = profileObject.asJsonObject.get("online").asInt == 1,
                                id = profileObjectId)
                            var photoField = photoObject.asJsonObject.get("photos")
                            if (photoField == null) {
                                photoField = photoObject.asJsonObject.get("photo_tags")
                            }
                            val photo = PhotoModel(
                                owner = friend,
                                timestamp = photoObject.asJsonObject.get("date").asString,
                                image =  photoField.asJsonObject.get("items").asJsonArray[0].asJsonObject.get("photo_604").asString,
                                likes =  photoField.asJsonObject.get("items").asJsonArray[0].asJsonObject.get("likes").asJsonObject.get("count").asInt,
                                comments = photoField.asJsonObject.get("items").asJsonArray[0].asJsonObject.get("reposts").asJsonObject.get("count").asInt)
                            photosList.add(photo)
                        }

                    }

                }
                if (silentMode) {
                    presenter.silentPhotosLoaded(photosList = photosList)
                } else {
                    presenter.photosLoaded(photosList = photosList)
                }
//                Log.e(TAG, "RESPONSE ${parsedJson}")
            }
            override fun attemptFailed(request: VKRequest, attemptNumber: Int, totalAttempts: Int) {
                super.attemptFailed(request, attemptNumber, totalAttempts)
                if (!silentMode) {
                    presenter.showError(textResource = R.string.photos_error_loading)
                }
            }
            override fun onError(error: VKError) {
                super.onError(error)
                if (!silentMode) {
                    presenter.showError(textResource = R.string.photos_error_loading)
                }
            }
        })

    }

    fun getSavedFriends(context: Context?): Set<String> {
        return SharedPreferencesHelper().getSavedFriends(context)
    }
}