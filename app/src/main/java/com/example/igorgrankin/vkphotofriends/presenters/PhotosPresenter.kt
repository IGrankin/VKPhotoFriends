package com.example.igorgrankin.vkphotofriends.presenters

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.igorgrankin.vkphotofriends.R
import com.example.igorgrankin.vkphotofriends.models.PhotoModel
import com.example.igorgrankin.vkphotofriends.providers.PhotosProvider
import com.example.igorgrankin.vkphotofriends.views.PhotosView

@InjectViewState
class PhotosPresenter: MvpPresenter<PhotosView>() {
    fun loadPhotos(context: Context?) {
        viewState.startLoading()
        var provider = PhotosProvider(presenter = this)
        var savedFriends = provider.getSavedFriends(context)
        provider.loadPhotos(savedFriends, false)
    }

    fun photosLoaded(photosList: ArrayList<PhotoModel>) {
        viewState.endLoading()
        if (photosList.size == 0) {
            viewState.setupEmptyList()
            viewState.showError(textResource = R.string.photos_no_photos)
        } else {
            viewState.setupPhotosList(photosList = photosList)
        }
    }

    fun showError(textResource: Int) {
        viewState.showError(textResource = textResource)
    }

    fun silentLoadPhotos(context: Context?) {
        var provider = PhotosProvider(presenter = this)
        var savedFriends = provider.getSavedFriends(context)
        provider.loadPhotos(savedFriends, true)
    }

    fun silentPhotosLoaded(photosList: ArrayList<PhotoModel>) {
        if (photosList.size != 0) {
            viewState.setupPhotosList(photosList = photosList)
        }
    }

}