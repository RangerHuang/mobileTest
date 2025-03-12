package com.zj.mobileTest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.zj.mobileTest.data.DataManager
import com.zj.mobileTest.ui.screens.BookingListScreen

import com.zj.mobileTest.ui.theme.ComposeDoubanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DataManager.init(this)
        setContent {
            ComposeDoubanTheme {
                Surface(color = MaterialTheme.colors.background) {
                    BookingListScreen()
                }
            }
        }
    }
}

