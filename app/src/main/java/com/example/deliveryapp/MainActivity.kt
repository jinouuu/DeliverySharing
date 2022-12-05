package com.example.deliveryapp

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.deliveryapp.adapter.RestaurantListAdapter
import com.example.deliveryapp.models.RestaurentModel
import com.google.gson.Gson
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class MainActivity : AppCompatActivity(), RestaurantListAdapter.RestaurantListClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        val restaurantModel = getRestaurantData()
        initRecyclerView(restaurantModel)
    }

    private fun initRecyclerView(restaurantList: List<RestaurentModel?>?) {
        val recyclerViewRestaurant = findViewById<RecyclerView>(R.id.recyclerViewRestaurant)
        recyclerViewRestaurant.layoutManager = LinearLayoutManager(this)
        val adapter = RestaurantListAdapter(restaurantList, this)
        recyclerViewRestaurant.adapter = adapter
    }

    // 음식점 데이터 파싱 (restaurent.json)
    private fun getRestaurantData(): List<RestaurentModel?>? {
        val inputStream: InputStream = resources.openRawResource(R.raw.restaurent)
        val writer: Writer = StringWriter()
        val buffer = CharArray(1024)
        try {
            val reader: Reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
            var n: Int
            while (reader.read(buffer).also { n = it } != -1) {
                writer.write(buffer, 0, n)
            }
        } catch (e: Exception) { }
        val jsonStr: String = writer.toString()
        val gson = Gson()
        val restaurentModel = gson.fromJson<Array<RestaurentModel>>(jsonStr, Array<RestaurentModel>::class.java).toList()
        return restaurentModel
    }

    override fun onItemClick(restaurantModel: RestaurentModel) {
        val intent = Intent(this@MainActivity, RestaurantMenuActivity::class.java)
        intent.putExtra("RestaurantModel",restaurantModel)
        startActivity(intent)
    }
}