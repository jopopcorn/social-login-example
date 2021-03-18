package com.example.loginexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.loginexample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.userEmailText.text = intent.extras?.get("email").toString()
        binding.logoutBtn.setOnClickListener(this)
    }

    fun directToLoginActivity(result: Boolean) {
        intent = Intent(this@MainActivity, LoginActivity::class.java)
        if (result) {
            Toast.makeText(applicationContext, "로그아웃", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(applicationContext, "로그아웃 실패", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.logout_btn -> SocialAuthHelper().logout(applicationContext, this@MainActivity)
        }
    }

}