package com.example.loginexample

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.loginexample.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.data.OAuthLoginState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.ref.WeakReference

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val RC_SIGN_IN = 10
        const val TAG = "LoginActivity"
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var naverLoginModule: OAuthLogin
    private lateinit var googleAuth: FirebaseAuth
    private lateinit var googleLoginModule: GoogleSignInClient
    private lateinit var retrofit: Retrofit
    private lateinit var naverAPI: NaverAPI

    private class NaverLoginHandler(context: LoginActivity): OAuthLoginHandler(){
        private val activityReference: WeakReference<LoginActivity> = WeakReference(context)
        private val activity = activityReference.get()
        override fun run(success: Boolean) {
            if (success) {
                activity?.getNaverUserInfo(OAuthLogin.getInstance().getAccessToken(activity.applicationContext))
            } else {
                val errorCode = OAuthLogin.getInstance().getLastErrorCode(activity).code
                val errorDesc = OAuthLogin.getInstance().getLastErrorDesc(activity)
                Toast.makeText(
                    activity,
                    "error code: $errorCode, error description: $errorDesc",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initRetrofit()
        initView()
        hasSocialSession()
    }

    private fun initData() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleLoginModule = GoogleSignIn.getClient(this, gso)
        googleAuth = Firebase.auth

        naverLoginModule = OAuthLogin.getInstance()
        naverLoginModule.init(
            this,
            getString(R.string.naver_client_id),
            getString(R.string.naver_client_secret),
            getString(R.string.app_name)
        )
    }

    private fun initView() {
        binding.signInNaverBtn.setOAuthLoginHandler(NaverLoginHandler(this))
        binding.signInNaverBtn.setOnClickListener(this)
        binding.signInGoogleBtn.setOnClickListener(this)
    }


    private fun initRetrofit() {
        retrofit = RetrofitClient.getInstance()
        naverAPI = retrofit.create(NaverAPI::class.java)
    }

    private fun hasSocialSession() {
        when {
            hasGoogleSession() -> {
                val intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                    putExtra("email", googleAuth.currentUser!!.email)
                }
                Log.d(TAG, "googleAuth currentUser email: ${googleAuth.currentUser?.email}")
                startActivity(intent)
                finish()
            }
            hasNaverSession() -> {
                getNaverUserInfo(naverLoginModule.getAccessToken(applicationContext))
            }
        }
    }

    private fun hasGoogleSession(): Boolean {
        if (googleAuth.currentUser == null) {
            return false
        }
        return true
    }

    private fun hasNaverSession(): Boolean {
        if (OAuthLoginState.NEED_LOGIN == naverLoginModule.getState(applicationContext)
            || OAuthLoginState.NEED_INIT == naverLoginModule.getState(applicationContext)
        ) {
            return false
        }
        return true
    }

    private fun getNaverUserInfo(accessToken: String){
        naverAPI.getUserInfo("Bearer $accessToken").enqueue(object : Callback<Result> {
            override fun onResponse(call: Call<Result>, response: Response<Result>) {
                if (response.isSuccessful && response.body() != null) {
                    val result: Result = response.body()!!
                    intent = Intent(this@LoginActivity, MainActivity::class.java).apply {
                        putExtra("email", result.response.email)
                    }
                    startActivity(intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<Result>, t: Throwable) {
                Log.d(TAG, "${t.message}")
            }
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_google_btn -> signInGoogle()
            R.id.sign_in_naver_btn -> signInNaver()
        }
    }

    private fun signInNaver() {
        naverLoginModule.startOauthLoginActivity(this, NaverLoginHandler(this))
    }

    private fun signInGoogle() {
        val signInIntent = googleLoginModule.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
            }
        } else {
            Log.d(TAG, "onActivityResult: resultCode = $resultCode")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        googleAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val intent = Intent(this, MainActivity::class.java).apply {
                        putExtra("email", googleAuth.currentUser!!.email)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    val view = binding.root
                    Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }
            }
    }
}
