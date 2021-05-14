package com.mitsuki.portal

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mitsuki.portal.base.annotation.Portal

@Portal(path = "M")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}