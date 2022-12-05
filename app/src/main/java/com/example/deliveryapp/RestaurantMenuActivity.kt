package com.example.deliveryapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.GridLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.GridLayoutManager
import com.example.deliveryapp.adapter.MenuListAdapter
import com.example.deliveryapp.models.Menus
import com.example.deliveryapp.models.RestaurentModel
import kotlinx.android.synthetic.main.activity_restaurant_menu.*

class RestaurantMenuActivity : AppCompatActivity(), MenuListAdapter.MenuListClickListener {

    private var itemsInTheCartList: MutableList<Menus?>? = null
    private var totalItemInCartCount = 0
    private var menuList: List<Menus?>? = null
    private var menuListAdapter: MenuListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)

        val restaurantModel = intent?.getParcelableExtra<RestaurentModel>("RestaurantModel")

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle(restaurantModel?.name)
        actionBar?.setDisplayHomeAsUpEnabled(true)

        menuList = restaurantModel?.menus

        initRecyclerView(menuList)
        checkoutButton.setOnClickListener{
            if(totalItemInCartCount == 0 ){
                Toast.makeText(this@RestaurantMenuActivity, "메뉴를 1개 이상 선택 해 주세요.", Toast.LENGTH_LONG).show()
            }
            else{
                restaurantModel?.menus = itemsInTheCartList
                val intent = Intent(this@RestaurantMenuActivity, PlaceYourOrderActivity::class.java)
                intent.putExtra("RestaurantModel", restaurantModel)
                startActivityForResult(intent, 1000)
            }
        }
    }
    private fun initRecyclerView(menus: List<Menus?>?){
        menuRecyclerView.layoutManager = GridLayoutManager(this,2)
        menuListAdapter = MenuListAdapter(menus, this)
        menuRecyclerView.adapter = menuListAdapter
    }

    override fun addToCartClickListener(menu: Menus) {
        if(itemsInTheCartList == null){
            itemsInTheCartList = ArrayList()
        }
        itemsInTheCartList?.add(menu)
        totalItemInCartCount = 0
        for(menu in itemsInTheCartList!!){
            totalItemInCartCount = totalItemInCartCount + menu?.totalInCart!!
        }
        checkoutButton.text = "주문하기 (" + totalItemInCartCount + ")"

    }

    override fun updateCartClickListener(menu: Menus) {
        val index = itemsInTheCartList!!.indexOf(menu)
        itemsInTheCartList?.removeAt(index)
        itemsInTheCartList?.add(menu)
        totalItemInCartCount = 0
        for(menu in itemsInTheCartList!!){
            totalItemInCartCount = totalItemInCartCount + menu?.totalInCart!!
        }
        checkoutButton.text = "주문하기 (" + totalItemInCartCount + ")"
    }

    override fun removeFromCartClickListener(menu: Menus) {
        if(itemsInTheCartList!!.contains(menu)){
            itemsInTheCartList?.remove(menu)
            totalItemInCartCount = 0
            for(menu in itemsInTheCartList!!){
                totalItemInCartCount = totalItemInCartCount + menu?.totalInCart!!
            }
            checkoutButton.text = "주문하기 (" + totalItemInCartCount + ")"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1000 && requestCode == RESULT_OK){
        }
    }
}