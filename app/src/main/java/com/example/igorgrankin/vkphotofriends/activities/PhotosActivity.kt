package com.example.igorgrankin.vkphotofriends.activities

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.igorgrankin.vkphotofriends.R
import com.example.igorgrankin.vkphotofriends.adapters.PhotosAdapter
import com.example.igorgrankin.vkphotofriends.models.PhotoModel
import com.example.igorgrankin.vkphotofriends.presenters.PhotosPresenter
import com.example.igorgrankin.vkphotofriends.views.PhotosView
import com.github.rahatarmanahmed.cpv.CircularProgressView

class PhotosActivity : MvpAppCompatFragment(), PhotosView {

    @InjectPresenter
    lateinit var photosPresenter: PhotosPresenter

    private lateinit var mRvPhotos: RecyclerView
    private lateinit var mTxtNoItems: TextView
    private lateinit var mCpvWait: CircularProgressView
    private lateinit var mAdapter: PhotosAdapter
    private lateinit var mSwipeContainer: SwipeRefreshLayout
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.activity_photos, container, false)
        mRvPhotos = view.findViewById(R.id.recycler_photos)
        mTxtNoItems = view.findViewById(R.id.txt_photos_no_photos)
        mCpvWait = view.findViewById(R.id.cpv_photos)
        mSwipeContainer = view.findViewById(R.id.photo_swipe_container)
        photosPresenter.loadPhotos(activity?.applicationContext)
        mSwipeContainer.setOnRefreshListener {
            photosPresenter.loadPhotos(activity?.applicationContext)
        }
        handler = Handler()
        runnable = Runnable {
            kotlin.run {
                handler.postDelayed(runnable, 4000)
                photosPresenter.silentLoadPhotos(activity?.applicationContext)
            }
        }

        handler.postDelayed(runnable, 4000)


        mAdapter = PhotosAdapter()
        mRvPhotos.adapter = mAdapter
        mRvPhotos.layoutManager = LinearLayoutManager(activity?.applicationContext, OrientationHelper.VERTICAL, false)
        mRvPhotos.setHasFixedSize(true)
        return view
    }


    //Photos view implementation
    override fun showError(textResource: Int) {
        mTxtNoItems.text = getString(textResource)
    }

    override fun setupEmptyList() {
        mRvPhotos.visibility = View.GONE
        mTxtNoItems.visibility = View.VISIBLE
    }

    override fun setupPhotosList(photosList: ArrayList<PhotoModel>) {
        mRvPhotos.visibility = View.VISIBLE
        mTxtNoItems.visibility = View.GONE
        mSwipeContainer.isRefreshing = false
        mAdapter.setupPhotos(photosList = photosList)
    }

    override fun startLoading() {
        mRvPhotos.visibility = View.GONE
        mTxtNoItems.visibility = View.GONE
        mCpvWait.visibility = View.VISIBLE
    }

    override fun endLoading() {
        mCpvWait.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
