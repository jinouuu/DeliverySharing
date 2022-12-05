package com.example.deliveryapp

import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsMessage
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deliveryapp.adapter.PlaceYourOrderAdapter
import com.example.deliveryapp.models.RestaurentModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_place_your_order.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import android.telephony.SmsManager as SmsManager1

class PlaceYourOrderActivity : AppCompatActivity() {

    var placeYourOrderAdapter: PlaceYourOrderAdapter? = null
    var isDeliveryOn: Boolean = true
    val db = FirebaseFirestore.getInstance()
    var subTotalAmount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_your_order)

        val restaurantModel: RestaurentModel? = intent.getParcelableExtra("RestaurantModel")
        val actionbar: ActionBar? = supportActionBar
        actionbar?.setTitle(restaurantModel?.name)
        actionbar?.setDisplayHomeAsUpEnabled(true)

        var addressArray: Array<String> = arrayOf("순천향대학교 향설생활관", "순천향대학교 멀티미디어관", "순천향대학교 학생회관")
        inputAddress.setOnClickListener{
            val dlg = AlertDialog.Builder(this)
            dlg.setTitle("수령장소")
                .setItems(addressArray,
                    DialogInterface.OnClickListener{ dialog, which ->
                        inputAddress.setText(addressArray[which])
                    })
            dlg.show()
        }
        // 카카오맵 API
//        button.setOnClickListener{
//            val intent = Intent(this@PlaceYourOrderActivity, Map::class.java)
//            startActivity(intent)
//        }

        buttonPlaceYourOrder.setOnClickListener{
            onPlaceOrderButtonClick(restaurantModel)
        }

        initRecyclerView(restaurantModel)
        calculateTotalAmount(restaurantModel)
    }
    private fun initRecyclerView(restaurantModel: RestaurentModel?){
        cartItemsRecyclerView.layoutManager = LinearLayoutManager(this)
        placeYourOrderAdapter = PlaceYourOrderAdapter(restaurantModel?.menus)
        cartItemsRecyclerView.adapter = placeYourOrderAdapter
    }

    private fun calculateTotalAmount(restaurantModel: RestaurentModel?){
        for(menu in restaurantModel?.menus!!){
            subTotalAmount += menu?.price!! * menu?.totalInCart!!
        }
        tvTotalAmount.text = String.format("%d",subTotalAmount) + " 원"
    }

    private fun onPlaceOrderButtonClick(restaurantModel: RestaurentModel?) {
        if (TextUtils.isEmpty(inputAddress.text.toString())) {
            inputAddress.error = "배송 주소를 입력하세요"
            return
        } else if (TextUtils.isEmpty(inputPhoneNum.text.toString())) {
            inputPhoneNum.error = "휴대폰 번호를 입력하세요"
            return
        }

        val aPhoneNum = inputPhoneNum.text
        val aAddress = inputAddress.text

        val dbData = hashMapOf(
            "PhoneNum" to aPhoneNum.toString(),
            "Address" to aAddress.toString(),
            "Price" to (subTotalAmount),
            "Menus" to restaurantModel?.menus
        )
        val currentTime = System.currentTimeMillis()
        val t_date = Date(currentTime)
        val t_dateFormat = SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale("ko","KR"))
        val str_date = t_dateFormat.format(t_date)

        val userData = hashMapOf(
            "ShopImage" to restaurantModel?.image,
            "PhoneNum" to aPhoneNum.toString(),
            "Address" to aAddress.toString(),
            "Price" to (subTotalAmount),
            "Menus" to restaurantModel?.menus,
            "ShopName" to restaurantModel?.name,
            "OrderState" to "wait"
        )

        //db.collection("Order").document(restaurantModel?.name.toString())
        //    .set(dbData, SetOptions.merge())
        db.collection("User").document(str_date)
           .set(userData)

        val intent = Intent(this@PlaceYourOrderActivity, SuccessOrderActivity::class.java)
        intent.putExtra("RestaurantModel",restaurantModel)
        startActivityForResult(intent, 1000)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == 1000){
            setResult(RESULT_OK)
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(RESULT_CANCELED)
        finish()
    }
}