package com.example.deliveryapp.adapter

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deliveryapp.OrderShareActivity
import com.example.deliveryapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.ramotion.foldingcell.FoldingCell
import kotlinx.android.synthetic.main.recycler_ordershare_list.view.*
import java.io.Serializable

data class DATA(var OrderState:String, var ShopImage:String, var OrderTime:String, var ShopName:String, var Menu:List<Map<String, Any>>, var Address:String, var PhoneNum:String, var Price:Any):Serializable

val db = FirebaseFirestore.getInstance()
var DataList = arrayListOf<DATA>()

class ViewHolder(v: View): RecyclerView.ViewHolder(v)
{
    val foldingCell:FoldingCell = v.folding_cell
    val profile = v.profile
    val orderTime: TextView = v.orderTime
    val shopName: TextView = v.shopName
    val menuName: TextView = v.menuName

    val shopName2: TextView = v.shopName2
    val shopImage: ImageView = v.head_image
    val menuList: ArrayList<TextView> = arrayListOf(v.menuList1, v.menuList2, v.menuList3, v.menuList4, v.menuList5)
    val menuPrice: ArrayList<TextView> = arrayListOf(v.menuPrice1, v.menuPrice2, v.menuPrice3, v.menuPrice4, v.menuPrice5)
    val orderAddress: TextView = v.address
    val phoneNum: TextView = v.phoneNum
    val orderTime2: TextView = v.orderTime2
    val totalPrice: TextView = v.totalPrice

    val payment: TextView = v.payment
    val orderState: TextView = v.orderState2
}

@SuppressLint("NotifyDataSetChanged")
class OrderShareAdapter(val context:Context, val clickListener: OrderShareClickListener, val PhoneNum: String?) : RecyclerView.Adapter<ViewHolder>() {
    init{
        db.collection("User").get().addOnSuccessListener { result ->
            DataList.clear()
            for (document in result){
                if(document.data.get("PhoneNum")==PhoneNum){
                    //var arrayMenu:List<Map<String, Any>> = document.data.get("Menus") as List<Map<String, Any>>
                    DataList.add(0,DATA(OrderState = document.data.get("OrderState") as String, Price = document.data.get("Price") as Any, PhoneNum = document.data.get("PhoneNum") as String, Address = document.data.get("Address") as String, ShopImage = document.data.get("ShopImage") as String, OrderTime = document.id, ShopName = document.data.get("ShopName") as String, Menu = document.data.get("Menus") as List<Map<String, Any>>))
                }
            }
            notifyDataSetChanged()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.recycler_ordershare_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = DataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//      holder.profile.setImageResource(DataList[position])
        Glide.with(holder.profile)
            .load(DataList.get(position).ShopImage)
            .into(holder.profile)
        holder.orderTime.text = DataList.get(position).OrderTime
        holder.shopName.text = DataList.get(position).ShopName
        holder.menuName.text = DataList.get(position).Menu.get(0).get("name").toString()
        if((DataList.get(position).Menu.size-1) > 0)
            holder.menuName.text = holder.menuName.text.toString().plus(" 외 ${DataList.get(position).Menu.size-1}개")

        holder.shopName2.text = DataList.get(position).ShopName
        Glide.with(holder.shopImage)
            .load(DataList.get(position).ShopImage)
            .into(holder.shopImage)
        for (i in 0 until DataList.get(position).Menu.size){
            holder.menuList.get(i).text = "· ${DataList.get(position).Menu.get(i).get("name")} x ${DataList.get(position).Menu.get(i).get("totalInCart")}"
            holder.menuPrice.get(i).text = "${DataList.get(position).Menu.get(i).get("price").toString().toInt() * DataList.get(position).Menu.get(i).get("totalInCart").toString().toInt()}원"
            holder.menuPrice.get(i).visibility = View.VISIBLE
            holder.menuList.get(i).visibility = View.VISIBLE
        }
        holder.orderAddress.text = DataList.get(position).Address
        holder.phoneNum.text = DataList.get(position).PhoneNum
        holder.orderTime2.text = DataList.get(position).OrderTime
        holder.totalPrice.text = "${DataList.get(position).Price}원"
        when(DataList.get(position).OrderState){
            "wait" -> {
                holder.payment.isEnabled = true
                holder.payment.text = "결제하기"
                holder.payment.setBackgroundColor(ContextCompat.getColor(context, R.color.btnRequest))
                holder.orderState.text = "결제대기중"
            }
            "payOk" -> {
                holder.payment.isEnabled = false
                holder.payment.text = "결제완료"
                holder.payment.setBackgroundColor(Color.GRAY)
                holder.orderState.text = "주문접수"
            }
            "cooking" -> {
                holder.payment.isEnabled = false
                holder.payment.text = "결제완료"
                holder.payment.setBackgroundColor(Color.GRAY)
                holder.orderState.text = "조리중"
                holder.orderState.setTextColor(ContextCompat.getColor(context,
                    android.R.color.system_accent1_300
                ))
            }
            "onDelivery" -> {
                holder.payment.isEnabled = false
                holder.payment.text = "결제완료"
                holder.payment.setBackgroundColor(Color.GRAY)
                holder.orderState.text = "배달중"
                holder.orderState.setTextColor(ContextCompat.getColor(context,
                    android.R.color.holo_red_dark))
            }
        }
        holder.itemView.setOnClickListener{
            holder.foldingCell.toggle(false)
        }
        holder.payment.setOnClickListener{
            clickListener.onItemClick(DataList.get(position))
        }
    }
    interface OrderShareClickListener{
        fun onItemClick(data: DATA)
    }
}
