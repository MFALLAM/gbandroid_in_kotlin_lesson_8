package com.example.gblesson4.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.gblesson4.R
import com.example.gblesson4.view.cities.CitiesListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CitiesListFragment.newInstance())
                .commitNow()
        }

    }
}