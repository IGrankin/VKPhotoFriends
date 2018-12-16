package com.example.igorgrankin.vkphotofriends.adapters

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.igorgrankin.vkphotofriends.R
import com.example.igorgrankin.vkphotofriends.models.FriendModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FriendsAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private var mSourceList: ArrayList<FriendModel> = ArrayList()
    private var mFriendsList: ArrayList<FriendModel> = ArrayList()
    private var mObservedList: ArrayList<FriendModel> = ArrayList()
    private var mListener: OnFriendClick? = null
    private var isRegularFriendsAreShown: Boolean = true

    constructor(listener: OnFriendClick):super() {
        this.mListener = listener
    }

    fun setupFriends(
        friendsList: ArrayList<FriendModel>,
        observedFriendIds: Set<String>
    ) {
        mSourceList.clear()
        mSourceList.addAll(friendsList)
        mObservedList.clear()
        friendsList.forEach {
            if (observedFriendIds.contains(it.id)) {
                mObservedList.add(it)
            }
        }
        filter(query = "")
    }

    fun tryToAddObservedFriend(friendModel: FriendModel): Boolean {
        if (mObservedList.contains(friendModel)) {
            mObservedList.remove(friendModel)
            return false
        } else {
            mObservedList.add(friendModel)
            return true
        }
    }


    fun filter(
        query: String
    ) {
        mFriendsList.clear()
        var sourceFriendsArrayList: ArrayList<FriendModel>
        if (isRegularFriendsAreShown) {
            sourceFriendsArrayList = mSourceList
        } else {
            sourceFriendsArrayList = mObservedList
        }
        sourceFriendsArrayList.forEach {
            if (it.name.contains(query, ignoreCase = true) || it.surname.contains(query, ignoreCase = true)) {
                mFriendsList.add(it)
            } else {
                it.city?.let {
                    city ->
                    if (city.contains(query, ignoreCase = true)) {
                        mFriendsList.add(it)
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val layoutInlfatter = LayoutInflater.from(p0.context)
        val itemView = layoutInlfatter.inflate(R.layout.cell_friend, p0, false)
        return FriendsViewHolder(itemView = itemView)
    }

    override fun getItemCount(): Int {
        return mFriendsList.count()
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if (p0 is FriendsViewHolder) {
            p0.bind(friendModel =  mFriendsList[p1])
            if (mObservedList.contains(mFriendsList[p1])) {
                mListener?.let {
                    it.markViewAsAdded(p0.itemView)
                }
            } else {
                mListener?.let {
                    it.markViewAsRegular(p0.itemView)
                }
            }
            p0.itemView.setOnClickListener {
                mListener?.let { listener ->
                    listener.onFriendClicked(it, mFriendsList[p1])
                }
            }
        }
    }

    fun loadObservedFriends() {
        isRegularFriendsAreShown = false
        filter(query = "")
    }

    fun loadRegularFriends() {
        isRegularFriendsAreShown = true
        filter(query = "")
    }

    interface OnFriendClick{
        fun onFriendClicked(itemView: View, friendModel: FriendModel)
        fun markViewAsAdded(itemView: View)
        fun markViewAsRegular(itemView: View)
    }

    class FriendsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var mCivAvatar: CircleImageView = itemView.findViewById(R.id.friend_civ_avatar)
        private var mTxtUsername: TextView = itemView.findViewById(R.id.friend_txt_name)
        private var mTxtCity: TextView = itemView.findViewById(R.id.friend_txt_city)
        private var mImgonline: View = itemView.findViewById(R.id.friend_img_online)

        @SuppressLint("SetTextI18n")
        fun bind(friendModel: FriendModel) {
            friendModel.avatar?.let {url ->
                Picasso.with(itemView.context).load(url).into(mCivAvatar)
            }
            mTxtUsername.text = friendModel.name + " " + friendModel.surname
            mTxtCity.text = itemView.context.getString(R.string.friend_no_city)
            friendModel.city?.let { mTxtCity.text = it }

            if (friendModel.isOnline) {
                mImgonline.visibility = View.VISIBLE
            } else {
                mImgonline.visibility = View.GONE
            }
        }
    }
}
