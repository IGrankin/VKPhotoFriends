package com.example.igorgrankin.vkphotofriends.activities

import android.media.Image
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.example.igorgrankin.vkphotofriends.R
import com.example.igorgrankin.vkphotofriends.models.FriendModel
import com.google.gson.JsonParser
import com.squareup.picasso.Picasso
import com.vk.sdk.VKSdk
import com.vk.sdk.api.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_menu.*
import kotlinx.android.synthetic.main.app_bar_menu.*

class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mAvatar: CircleImageView
    private lateinit var mName: TextView
    private lateinit var mCity: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        setSupportActionBar(toolbar)
        mAvatar = nav_view.getHeaderView(0).findViewById<CircleImageView>(R.id.menu_image_avatar)
        mName = nav_view.getHeaderView(0).findViewById(R.id.menu_txt_name)
        mCity = nav_view.getHeaderView(0).findViewById(R.id.menu_txt_city)
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()
        supportFragmentManager.beginTransaction().replace(R.id.nav_container, FriendsActivity()).commit()
        nav_view.setNavigationItemSelectedListener(this)
        loadProfile()
    }

    fun loadProfile() {
        val request = VKApi.users().get(
            VKParameters.from(VKApiConst.FIELDS,
                "city, photo_200, online"))
        request.executeWithListener(object: VKRequest.VKRequestListener() {
            override fun onComplete(response: VKResponse) {
                super.onComplete(response)
                val jsonParser = JsonParser()
                var parsedJson = jsonParser.parse(response.json.toString()).asJsonObject
                var response = parsedJson.get("response").asJsonArray[0].asJsonObject
                    val city = if (response.asJsonObject.get("city") == null) {
                        null
                    } else {
                        response.asJsonObject.get("city").asJsonObject.get("title").asString
                    }
                    val friend = FriendModel(
                        name = response.asJsonObject.get("first_name").asString,
                        surname = response.asJsonObject.get("last_name").asString,
                        city = city,
                        avatar = response.asJsonObject.get("photo_200").asString,
                        isOnline = response.asJsonObject.get("online").asInt == 1,
                        id = response.asJsonObject.get("id").asString)
                    mName.text = friend.name + " " + friend.surname
                    Picasso.with(this@MenuActivity).load(friend.avatar).into(mAvatar)
                    mCity.text = friend.city
            }

            override fun onError(error: VKError?) {
                super.onError(error)
            }
        })
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
//            super.onBackPressed()
            moveTaskToBack(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_friends -> {
                supportFragmentManager.beginTransaction().replace(R.id.nav_container, FriendsActivity()).commit()
            }
            R.id.nav_photos -> {
                supportFragmentManager.beginTransaction().replace(R.id.nav_container, PhotosActivity()).commit()
            }
            R.id.nav_exit -> {
                VKSdk.logout()
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }
}
