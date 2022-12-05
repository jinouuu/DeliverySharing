package com.example.deliveryapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.deliveryapp.R
import com.example.deliveryapp.models.Hours
import com.example.deliveryapp.models.RestaurentModel
import java.text.SimpleDateFormat
import java.util.*

class RestaurantListAdapter(val restaurantList: List<RestaurentModel?>?, val clickListener: RestaurantListClickListener): RecyclerView.Adapter<RestaurantListAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RestaurantListAdapter.MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurant_list_row,parent,false)

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantListAdapter.MyViewHolder, position: Int) {
        holder.bind(restaurantList?.get(position))
        holder.itemView.setOnClickListener{
            clickListener.onItemClick(restaurantList?.get(position)!!)
        }
    }

    override fun getItemCount(): Int {
        return restaurantList?.size!!
    }

    inner class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
    {
        val thumbImage: ImageView = view.findViewById(R.id.thumbImage)
        val tvRestaurantName: TextView = view.findViewById(R.id.tvRestaurantName)
        val tvRestaurantMinimum: TextView = view.findViewById(R.id.tvRestaurantMinimum)
        val tvRestaurantTip: TextView = view.findViewById(R.id.tvRestaurantTip)
        val tvRestaurantHours: TextView = view.findViewById(R.id.tvRestaurantHours)
        fun bind(restaurentModel: RestaurentModel?){
            tvRestaurantName.text = restaurentModel?.name
            tvRestaurantMinimum.text = "배달 도착 시간 12:30"
            tvRestaurantTip.text = "주문 마감 시간 ~11:30"
            tvRestaurantHours.text = "수령장소 : 순천향대학교"

            Glide.with(thumbImage)
                .load(restaurentModel?.image)
                .into(thumbImage)
        }
    }
    interface RestaurantListClickListener{
        fun onItemClick(restaurentModel: RestaurentModel)
    }
}