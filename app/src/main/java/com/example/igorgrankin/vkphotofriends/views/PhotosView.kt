package com.example.igorgrankin.vkphotofriends.views

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.igorgrankin.vkphotofriends.models.FriendModel
import com.example.igorgrankin.vkphotofriends.models.PhotoModel

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface PhotosView: MvpView {
    fun showError(textResource: Int)
    fun setupEmptyList()
    fun setupPhotosList(photosList: ArrayList<PhotoModel>)
    fun startLoading()
    fun endLoading()
}