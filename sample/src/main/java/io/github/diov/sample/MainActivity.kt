package io.github.diov.sample

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import io.github.diov.syncpreferences.getSyncPreferences
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupView()
    }

    private fun setupView() {
        val syncPreferences = getSyncPreferences()
        saveButton.setOnClickListener {
            val intent = Intent(this, ForegroundService::class.java)
            ContextCompat.startForegroundService(this, intent)
            Handler().postDelayed(1000) {
                syncPreferences.edit().putLong("Hello", 1000).putString("NIHAO", "NIHAO").apply()
            }
        }

        loadButton.setOnClickListener {
            val hello = syncPreferences.getString("NIHAO", "nihao")
            Toast.makeText(this, hello, Toast.LENGTH_LONG).show()
        }
    }
}
