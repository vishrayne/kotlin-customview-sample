package com.example.kotlincustomviewsample

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.emoHappyView
import kotlinx.android.synthetic.main.activity_main.emoSadView
import kotlinx.android.synthetic.main.activity_main.feedBack

class MainActivity : AppCompatActivity() {

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    feedBack.text = savedInstanceState?.getString("feedback") ?: "Happy"

    emoHappyView.setOnClickListener {
      feedBack.text = "Happy"
    }

    emoSadView.setOnClickListener {
      feedBack.text = "Sad"
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putString("feedback", feedBack.text.toString())
    super.onSaveInstanceState(outState)
  }
}
