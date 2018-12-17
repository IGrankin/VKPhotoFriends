package com.example.igorgrankin.vkphotofriends.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.MvpFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.igorgrankin.vkphotofriends.R
import com.example.igorgrankin.vkphotofriends.adapters.FriendsAdapter
import com.example.igorgrankin.vkphotofriends.models.FriendModel
import com.example.igorgrankin.vkphotofriends.presenters.FriendsPresenter
import com.example.igorgrankin.vkphotofriends.views.FriendsView
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.vk.sdk.VKSdk

class FriendsActivity : MvpAppCompatFragment(), FriendsView, FriendsAdapter.OnFriendClick {
    @InjectPresenter
    lateinit var friendsPresenter: FriendsPresenter

    private lateinit var mRvFriends: RecyclerView
    private lateinit var mTxtNoItems: TextView
    private lateinit var mCpvWait: CircularProgressView
    private lateinit var mAdapter: FriendsAdapter
    private lateinit var mPhotosButton: Button
    private lateinit var mExitButton: Button
    private lateinit var mSwitchFriends: Switch
    private lateinit var mCountFriendsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        friendsPresenter.loadFriends(activity?.applicationContext)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.activity_friends, container, false)
        mRvFriends = view.findViewById(R.id.recycler_friends)
        mTxtNoItems = view.findViewById(R.id.txt_friends_no_items)
        mCpvWait = view.findViewById(R.id.cpv_friends)

//        mPhotosButton = view.findViewById(R.id.btn_open_photos)
//        mPhotosButton.setOnClickListener {
//            startActivity(Intent(activity?.applicationContext, PhotosActivity::class.java))
//        }
//        mExitButton = view.findViewById(R.id.btn_exit)
//        mExitButton.setOnClickListener {
//            VKSdk.logout()
//            activity?.finish()
//        }

        val mTxtSearch: EditText = view.findViewById(R.id.txt_friends_search)
        mTxtSearch.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mAdapter.filter(s.toString())
            }

        })

        mSwitchFriends = view.findViewById(R.id.switch_show_added_friends)
        mSwitchFriends.setOnCheckedChangeListener {
                buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                friendsPresenter.showAddedFriends()
            } else {
                friendsPresenter.showFriends()
            }
        }

        mCountFriendsText = view.findViewById(R.id.count_friends)


        mAdapter = FriendsAdapter(this)

        mRvFriends.adapter = mAdapter
        mRvFriends.layoutManager = LinearLayoutManager(activity?.applicationContext, OrientationHelper.VERTICAL, false)
        mRvFriends.setHasFixedSize(true)
        return view
    }

    // Item view methods
    override fun onFriendClicked(itemView: View, friendModel: FriendModel) {
        val wasAdded = mAdapter.tryToAddObservedFriend(friendModel)
        if (wasAdded) {
            friendsPresenter.saveFriend(context = activity?.applicationContext, friendModel = friendModel)
            markViewAsAdded(itemView)
        } else {
            friendsPresenter.removeFriend(context = activity?.applicationContext, friendModel = friendModel)
            markViewAsRegular(itemView)
        }
    }

    override fun showObservedFriends() {
        mAdapter.loadObservedFriends()
    }

    override fun showRegularFriends() {
        mAdapter.loadRegularFriends()
    }

    override fun markViewAsAdded(itemView: View) {
        itemView.setBackgroundColor(Color.MAGENTA)
    }

    override fun markViewAsRegular(itemView: View) {
        itemView.setBackgroundColor(Color.WHITE)
    }

    override fun updateObservedFriendsCountWithInt(count: Int) {
        mCountFriendsText.text = getString(R.string.txt_only_observed_friends, count.toString())
    }

    // Friends View implementation
    override fun showError(textResource: Int) {
        mTxtNoItems.text = getString(textResource)
    }

    override fun setupEmptyList() {
        mRvFriends.visibility = View.GONE
        mTxtNoItems.visibility = View.VISIBLE
    }

    override fun setupFriendsList(
        friendsList: ArrayList<FriendModel>,
        idsSet: Set<String>
    ) {
        mRvFriends.visibility = View.VISIBLE
        mTxtNoItems.visibility = View.GONE
        mAdapter.setupFriends(friendsList = friendsList, observedFriendIds = idsSet)
    }

    override fun startLoading() {
        mRvFriends.visibility = View.GONE
        mTxtNoItems.visibility = View.GONE
        mCpvWait.visibility = View.VISIBLE
    }

    override fun endLoading() {
        mCpvWait.visibility = View.GONE
    }
}
