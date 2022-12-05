package com.example.deliveryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.deliveryapp.models.RestaurentModel
import kotlinx.android.synthetic.main.activity_success_order.*

class SuccessOrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_order)

        val restaurantModel: RestaurentModel? = intent.getParcelableExtra("RestaurantModel")
        val actionbar: ActionBar? = supportActionBar
        actionbar?.setTitle(restaurantModel?.name)
        actionbar?.setDisplayHomeAsUpEnabled(false)

        buttonDone.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }
}