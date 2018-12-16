package com.example.igorgrankin.vkphotofriends.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.igorgrankin.vkphotofriends.R
import com.example.igorgrankin.vkphotofriends.models.FriendModel
import com.example.igorgrankin.vkphotofriends.models.PhotoModel
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

class PhotosAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mSourceList: ArrayList<PhotoModel> = ArrayList()

    fun setupPhotos(photosList: ArrayList<PhotoModel>) {
        mSourceList.clear()
        mSourceList.addAll(photosList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val layoutInlfatter = LayoutInflater.from(p0.context)
        val itemView = layoutInlfatter.inflate(R.layout.cell_photo, p0, false)
//        return FriendsAdapter.FriendsViewHolder(itemView = itemView)
        return PhotosAdapter.PhotosViewHolder(itemView = itemView)
    }

    override fun getItemCount(): Int {
        return mSourceList.count()
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        if (p0 is PhotosAdapter.PhotosViewHolder) {
            p0.bind(photoModel =  mSourceList[p1])
        }
    }

    class PhotosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private var mCivAvatar: CircleImageView = itemView.findViewById(R.id.photo_civ_avatar)
        private var mTxtUsername: TextView = itemView.findViewById(R.id.photo_txt_name)
        private var mPublishedDate: TextView = itemView.findViewById(R.id.photo_txt_date)
        private var mLikes:TextView = itemView.findViewById(R.id.photo_txt_likes)
        private var mComments: TextView = itemView.findViewById(R.id.photo_txt_comments)
        private var mImgonline: View = itemView.findViewById(R.id.photo_img_online)
        private var mImageView: ImageView = itemView.findViewById(R.id.photo_img_source_image)

        fun bind(photoModel: PhotoModel) {
            val friendModel = photoModel.owner
            friendModel.avatar?.let {url ->
                Picasso.with(itemView.context).load(url).into(mCivAvatar)
            }
            mTxtUsername.text = friendModel.name + " " + friendModel.surname
            mLikes.text = itemView.context.getString(R.string.photo_txt_likes, photoModel.likes.toString())
            mComments.text = itemView.context.getString(R.string.photo_txt_comments, photoModel.comments.toString())
            var timeFormatter = SimpleDateFormat("dd.MM.yy HH:mm")
            var photoDate = Date(photoModel.timestamp.toLong() * 1000)
            var dateString = timeFormatter.format(photoDate)
            mPublishedDate.text = itemView.context.getString(R.string.photo_txt_published, dateString)
            if (friendModel.isOnline) {
                mImgonline.visibility = View.VISIBLE
            } else {
                mImgonline.visibility = View.GONE
            }
            Picasso.with(itemView.context).load(photoModel.image).into(mImageView)
        }

    }

}