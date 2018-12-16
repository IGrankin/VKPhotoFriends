package com.example.igorgrankin.vkphotofriends.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.example.igorgrankin.vkphotofriends.R
import com.example.igorgrankin.vkphotofriends.presenters.LoginPresenter
import com.example.igorgrankin.vkphotofriends.views.LoginView
import com.github.rahatarmanahmed.cpv.CircularProgressView
import com.vk.sdk.VKAccessToken
import com.vk.sdk.VKCallback
import com.vk.sdk.VKScope
import com.vk.sdk.VKSdk
import com.vk.sdk.api.VKError
import com.vk.sdk.util.VKUtil



class LoginActivity : MvpAppCompatActivity(), LoginView {

    private val TAG: String = LoginActivity::class.java.simpleName
    private lateinit var mTxtHello: TextView
    private lateinit var mBtnEnter: Button
    private lateinit var mCpvWait: CircularProgressView

    @InjectPresenter
    lateinit var loginPresenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        mTxtHello = findViewById<TextView>(R.id.txt_login_hello)
        mBtnEnter = findViewById<Button>(R.id.btn_login_enter)
        mCpvWait = findViewById<CircularProgressView>(R.id.cpv_login)

        mBtnEnter.setOnClickListener {
            VKSdk.login(this@LoginActivity, VKScope.FRIENDS, VKScope.WALL)
        }

        VKSdk.wakeUpSession(applicationContext, object: VKCallback<VKSdk.LoginState> {
            override fun onResult(res: VKSdk.LoginState?) {
                res.let {
                    if (it == VKSdk.LoginState.LoggedIn || it == VKSdk.LoginState.Pending) {
                        openFriends()
                    }
                }
            }

            override fun onError(error: VKError?) {
                //nothing to do
            }

        })

//        val fingerprints = VKUtil.getCertificateFingerprint(this, this.packageName)
//        Log.e(TAG, "fingerprint ${fingerprints[0]}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!loginPresenter.loginVk(requestCode = requestCode, resultCode = resultCode, data = data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun startLoading() {
        mBtnEnter.visibility = View.GONE
        mCpvWait.visibility = View.VISIBLE
    }

    override fun endLoading() {

        mBtnEnter.visibility = View.VISIBLE
        mCpvWait.visibility = View.GONE
    }

    override fun showError(textResource: Int) {
        Toast.makeText(applicationContext, getString(textResource), Toast.LENGTH_SHORT).show()
    }

    override fun openFriends() {
        startActivity(Intent(applicationContext, FriendsActivity::class.java))
    }
}
