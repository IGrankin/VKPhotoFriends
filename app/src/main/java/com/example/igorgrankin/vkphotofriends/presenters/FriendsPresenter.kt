package com.example.igorgrankin.vkphotofriends.presenters

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.igorgrankin.vkphotofriends.R
import com.example.igorgrankin.vkphotofriends.models.FriendModel
import com.example.igorgrankin.vkphotofriends.providers.FriendsProvider
import com.example.igorgrankin.vkphotofriends.views.FriendsView

@InjectViewState
class FriendsPresenter: MvpPresenter<FriendsView>() {
    private val provider: FriendsProvider = FriendsProvider(presenter = this)

    fun loadFriends(context: Context?) {
        viewState.startLoading()
        provider.loadFriends(context)
    }

    fun friendsLoaded(
        context: Context?,
        friendsList: ArrayList<FriendModel>
    ) {
        viewState.endLoading()
        if (friendsList.size == 0) {
            viewState.setupEmptyList()
            viewState.showError(textResource = R.string.friends_no_items)
        } else {
            val addedIds = getSavedFriends(context)
            viewState.setupFriendsList(friendsList = friendsList, idsSet = addedIds)
        }

        val count = provider.getSavedFriends(context).count()
        viewState.updateObservedFriendsCountWithInt(count)
    }

    fun showError(textResource: Int) {
        viewState.showError(textResource = textResource)
    }

    fun showAddedFriends() {
        viewState.showObservedFriends()

    }

    fun showFriends() {
        viewState.showRegularFriends()
    }

    fun saveFriend(context: Context?, friendModel: FriendModel) {
        provider.saveFriend(context, friendModel)
        val count = provider.getSavedFriends(context).count()
        viewState.updateObservedFriendsCountWithInt(count)
    }

    fun removeFriend(context: Context?, friendModel: FriendModel) {
        provider.removeFriend(context, friendModel)
        val count = provider.getSavedFriends(context).count()
        viewState.updateObservedFriendsCountWithInt(count)
    }

    fun getSavedFriends(context: Context?): Set<String> {
        return provider.getSavedFriends(context)
    }

}