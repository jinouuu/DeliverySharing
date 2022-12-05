package com.example.deliveryapp.fragment

import android.content.ContentValues.TAG
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.deliveryapp.DATA
import com.example.deliveryapp.R
import com.example.deliveryapp.adapter.db
import kotlinx.android.synthetic.main.fragment_admin.*
import kotlinx.android.synthetic.main.fragment_admin.view.*

class DormitoryFragment(var DataList:ArrayList<DATA>) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var OrderState = ""
        var DeliveryAdd = ""
        var item = arrayListOf<String>()
        for(data in DataList){
            if(data.Address == "순천향대학교 향설생활관"){
                DeliveryAdd = data.Address
                var tmp = arrayListOf<String>()
                OrderState = data.OrderState
                when(OrderState){
                    "payOk" -> {
                        OrderState = "결제완료"
                    }
                    "cooking" ->{
                        OrderState = "조리중"
                    }
                    "onDelivery" ->{
                        OrderState = "배달중"
                    }
                }
                tmp.add("${data.OrderTime}\t\t\t\t\t\t\t\t\t총 주문금액 : ${data.Price}\n")
                for(x in 0..data.Menu.size-1){
                    tmp.add("${data.Menu.get(x).get("name").toString()} ${data.Menu.get(x).get("totalInCart")}개 - (${data.Menu.get(x).get("price").toString()}원)\n")
                }
                tmp.add("주문자 번호 : ${data.PhoneNum}\t\t\t\t주문 상태 : ${OrderState}")
                item.add(tmp.joinToString(" "))
            }
        }
        val view = inflater.inflate(R.layout.fragment_admin, container, false)
        view.list.adapter = ArrayAdapter(requireActivity(),android.R.layout.simple_list_item_1, item)
        view.cooking.setOnClickListener{
            if(DataList.size != 0 && DataList.get(0).Address == DeliveryAdd){
                db.collection("User").whereEqualTo("ShopName",DataList.get(0).ShopName).whereEqualTo("Address",DeliveryAdd).get().addOnSuccessListener { result->
                    for(document in result){
                        if(document.data.get("OrderState").toString() != "wait"){
                            db.collection("User").document(document.id).update(mapOf("OrderState" to "cooking"))
                        }
                    }
                }
                Toast.makeText(requireActivity(),"주문 상태를 '조리중'으로 변경합니다.",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireActivity(),"결제된 주문이 없습니다.",Toast.LENGTH_SHORT).show()
            }
        }
        view.onDelivery.setOnClickListener{
            if(DataList.size != 0 && DataList.get(0).Address == DeliveryAdd) {
                db.collection("User").whereEqualTo("ShopName", DataList.get(0).ShopName)
                    .whereEqualTo("Address", DeliveryAdd).get().addOnSuccessListener { result ->
                    for (document in result) {
                        if (document.data.get("OrderState").toString() != "wait") {
                            db.collection("User").document(document.id)
                                .update(mapOf("OrderState" to "onDelivery"))
                        }
                    }
                    Toast.makeText(requireActivity(), "주문 상태를 '배달중'으로 변경합니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            else{
                Toast.makeText(requireActivity(),"결제된 주문이 없습니다.",Toast.LENGTH_SHORT).show()
            }
        }
        return view
    }
}