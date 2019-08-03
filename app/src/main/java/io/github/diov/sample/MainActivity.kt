package io.github.diov.sample

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import io.github.diov.rxping.RxPing
import io.github.diov.syncpreferences.getSyncPreferences
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupView()
    }

    private fun setupView() {
        val syncPreferences = getSyncPreferences("default")
        saveButton.setOnClickListener {
            val intent = Intent(this, ForegroundService::class.java)
            ContextCompat.startForegroundService(this, intent)
            Handler().postDelayed(1000) {
                syncPreferences.edit().putLong("Hello", 1000).putString("NIHAO", "NIHAO").apply()
            }
        }

        loadButton.setOnClickListener {
            RxPing.ping("8.8.8.8", 10, 10).subscribe({
                println(it.toList())
            }, { it.printStackTrace() })
        }
    }
}
