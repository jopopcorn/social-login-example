package com.example.loginexample

import android.content.Context
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.data.OAuthLoginState

class SocialAuthHelper {

    fun logout(context: Context, activity: MainActivity) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().signOut()
            GoogleSignIn.getClient(context, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
            Toast.makeText(context, "로그아웃", Toast.LENGTH_SHORT).show()
            activity.directToLoginActivity(true)
        } else if (OAuthLoginState.OK == OAuthLogin.getInstance().getState(context)) {
            OAuthLogin.getInstance().logout(context)
            Toast.makeText(context, "로그아웃", Toast.LENGTH_SHORT).show()
            activity.directToLoginActivity(true)
        }
    }

    fun withdraw(context: Context, activity: MainActivity) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().currentUser!!.delete().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activity.directToLoginActivity(true)
                    Toast.makeText(context, "회원 정보 삭제", Toast.LENGTH_SHORT).show()
                }
            }
        }else if(OAuthLoginState.OK == OAuthLogin.getInstance().getState(context)){
            OAuthLogin.getInstance().logoutAndDeleteToken(context)
            Toast.makeText(context, "회원 정보 연동 해제", Toast.LENGTH_SHORT).show()
            activity.directToLoginActivity(true)
        }
    }
}