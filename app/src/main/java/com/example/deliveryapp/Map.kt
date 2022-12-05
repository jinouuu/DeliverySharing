package com.example.deliveryapp

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.deliveryapp.R
import kotlinx.android.synthetic.main.activity_map.*
import net.daum.mf.map.api.MapView

class Map<T, U> :AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        val mapView = MapView(this)
        map_view.addView(mapView)
    }
}