package com.example.igorgrankin.vkphotofriends.views

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.igorgrankin.vkphotofriends.models.FriendModel

@StateStrategyType(value = AddToEndSingleStrategy::class)
interface FriendsView: MvpView {
    fun showError(textResource: Int)
    fun setupEmptyList()
    fun setupFriendsList(
        friendsList: ArrayList<FriendModel>,
        idsSet: Set<String>
    )
    fun startLoading()
    fun endLoading()
    fun showObservedFriends()
    fun updateObservedFriendsCountWithInt(count: Int)
    fun showRegularFriends()
}