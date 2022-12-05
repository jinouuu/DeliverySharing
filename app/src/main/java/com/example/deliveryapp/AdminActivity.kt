package com.example.deliveryapp

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.deliveryapp.adapter.AdminAdapter
import com.example.deliveryapp.adapter.DATA
import com.example.deliveryapp.fragment.DormitoryFragment
import com.example.deliveryapp.fragment.MultiFragment
import com.example.deliveryapp.fragment.UnionFragment
import kotlinx.android.synthetic.main.activity_admin.*
import kotlin.collections.Map

data class DATA(var OrderState:String, var ShopImage:String, var OrderTime:String, var ShopName:String, var Menu:List<Map<String, Any>>, var Address:String, var PhoneNum:String, var Price:Any)
var DataList = arrayListOf<com.example.deliveryapp.DATA>()

//  유저 01145454545 / 123456

//  맥도날드 관리자 01199999999 / 999999
//  청년피자 관리자 01188888888 / 888888
//  서브웨이 관리자 01177777777 / 777777
//  네네치킨 관리자 01166666666 / 666666
//  공차 관리자 01155555555 / 555555
//  탐앤탐스 관리자 01144444444 / 444444

class AdminActivity : AppCompatActivity() {
    private var backKeyPressedTime : Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        else{
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)
        val actionBar: ActionBar? = supportActionBar
        val ShopName = intent.getStringExtra("ShopName")
        actionBar?.setTitle(ShopName.toString())
        DataList.clear()

        db.collection("User").whereEqualTo("ShopName",ShopName.toString()).get().addOnSuccessListener { result->
            var i=0
            for(document in result){
                if(document.data.get("OrderState").toString() != "wait"){
                    DataList.add(i,com.example.deliveryapp.DATA(OrderState = document.data.get("OrderState") as String, Price = document.data.get("Price") as Any, PhoneNum = document.data.get("PhoneNum") as String, Address = document.data.get("Address") as String, ShopImage = document.data.get("ShopImage") as String, OrderTime = document.id, ShopName = document.data.get("ShopName") as String, Menu = document.data.get("Menus") as List<Map<String, Any>>))
                    i++
                }
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val adapter = AdminAdapter(supportFragmentManager)
            adapter.addFragment(DormitoryFragment(DataList), "순천향대학교\n향설생활관 1층")
            adapter.addFragment(MultiFragment(DataList), "순천향대학교\n멀티미디어관 1층")
            adapter.addFragment(UnionFragment(DataList), "순천향대학교\n학생회관 1층")
            main_viewPager.adapter = adapter
            main_tablayout.setupWithViewPager(main_viewPager)
        }, 3000)

        // 새로고침
        swipe.setOnRefreshListener {
            DataList.clear()
            db.collection("User").whereEqualTo("ShopName",ShopName.toString()).get().addOnSuccessListener { result->
                var i=0
                for(document in result){
                    if(document.data.get("OrderState").toString() != "wait"){
                        DataList.add(i,com.example.deliveryapp.DATA(OrderState = document.data.get("OrderState") as String, Price = document.data.get("Price") as Any, PhoneNum = document.data.get("PhoneNum") as String, Address = document.data.get("Address") as String, ShopImage = document.data.get("ShopImage") as String, OrderTime = document.id, ShopName = document.data.get("ShopName") as String, Menu = document.data.get("Menus") as List<Map<String, Any>>))
                        i++
                    }
                }
            }

            Handler(Looper.getMainLooper()).postDelayed({
                val adapter = AdminAdapter(supportFragmentManager)
                adapter.addFragment(DormitoryFragment(DataList), "순천향대학교\n향설생활관 1층")
                adapter.addFragment(MultiFragment(DataList), "순천향대학교\n멀티미디어관 1층")
                adapter.addFragment(UnionFragment(DataList), "순천향대학교\n학생회관 1층")
                swipe.isRefreshing = false
                main_viewPager.adapter = adapter
                main_tablayout.setupWithViewPager(main_viewPager)
            }, 1000)
        }
    }
}
