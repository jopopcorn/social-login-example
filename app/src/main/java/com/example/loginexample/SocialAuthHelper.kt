package com.example.loginexample

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.data.OAuthLoginState

class SocialAuthHelper {
    fun logout(context: Context, activity: MainActivity){
        if(FirebaseAuth.getInstance().currentUser != null){
            FirebaseAuth.getInstance().signOut()
            GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
            activity.directToLoginActivity(true)
        }else if(OAuthLoginState.OK == OAuthLogin.getInstance().getState(context)){
            OAuthLogin.getInstance().logout(context)
            activity.directToLoginActivity(true)
        }
    }
}