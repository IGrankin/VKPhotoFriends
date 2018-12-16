package com.example.igorgrankin.vkphotofriends.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.igorgrankin.vkphotofriends.R
import com.example.igorgrankin.vkphotofriends.adapters.FriendsAdapter
import com.example.igorgrankin.vkphotofriends.models.FriendModel
import com.example.igorgrankin.vkphotofriends.presenters.FriendsPresenter
import com.example.igorgrankin.vkphotofriends.views.FriendsView
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.vk.sdk.VKSdk

class FriendsActivity : MvpAppCompatActivity(), FriendsView, FriendsAdapter.OnFriendClick {

    @InjectPresenter
    lateinit var friendsPresenter: FriendsPresenter

    private lateinit var mRvFriends: RecyclerView
    private lateinit var mTxtNoItems: TextView
    private lateinit var mCpvWait: CircularProgressView
    private lateinit var mAdapter: FriendsAdapter
    private lateinit var mPhotosButton: Button
    private lateinit var mExitButton: Button
    private lateinit var mSwitchFriends: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        mRvFriends = findViewById(R.id.recycler_friends)
        mTxtNoItems = findViewById(R.id.txt_friends_no_items)
        mCpvWait = findViewById(R.id.cpv_friends)

        mPhotosButton = findViewById(R.id.btn_open_photos)
        mPhotosButton.setOnClickListener {
            startActivity(Intent(applicationContext, PhotosActivity::class.java))
        }
        mExitButton = findViewById(R.id.btn_exit)
        mExitButton.setOnClickListener {
            VKSdk.logout()
            this.finish()
        }

        val mTxtSearch: EditText = findViewById(R.id.txt_friends_search)
        mTxtSearch.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mAdapter.filter(s.toString())
            }

        })

        mSwitchFriends = findViewById(R.id.switch_show_added_friends)
        mSwitchFriends.setOnCheckedChangeListener {
                buttonView: CompoundButton?, isChecked: Boolean ->
            if (isChecked) {
                friendsPresenter.showAddedFriends()
            } else {
                friendsPresenter.showFriends()
            }
        }

        friendsPresenter.loadFriends(applicationContext)
        mAdapter = FriendsAdapter(this)

        mRvFriends.adapter = mAdapter
        mRvFriends.layoutManager = LinearLayoutManager(applicationContext, OrientationHelper.VERTICAL, false)
        mRvFriends.setHasFixedSize(true)
    }

    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    // Item view methods
    override fun onFriendClicked(itemView: View, friendModel: FriendModel) {
        val wasAdded = mAdapter.tryToAddObservedFriend(friendModel)
        if (wasAdded) {
            friendsPresenter.saveFriend(context = applicationContext, friendModel = friendModel)
            markViewAsAdded(itemView)
        } else {
            friendsPresenter.removeFriend(context = applicationContext, friendModel = friendModel)
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
