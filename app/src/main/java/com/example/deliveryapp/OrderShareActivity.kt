package com.example.deliveryapp

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.deliveryapp.adapter.DATA
import com.example.deliveryapp.adapter.OrderShareAdapter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_ordershare.*
import kr.co.bootpay.android.Bootpay
import kr.co.bootpay.android.constants.BootpayBuildConfig
import kr.co.bootpay.android.events.BootpayEventListener
import kr.co.bootpay.android.models.BootExtra
import kr.co.bootpay.android.models.BootItem
import kr.co.bootpay.android.models.BootUser
import kr.co.bootpay.android.models.Payload

val db = FirebaseFirestore.getInstance()

class OrderShareActivity : AppCompatActivity(), OrderShareAdapter.OrderShareClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ordershare)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle("주문내역")
        actionBar?.setDisplayHomeAsUpEnabled(true)

        if (BootpayBuildConfig.DEBUG) {
            applicationId = "635259bad01c7e001c96a57e"
        }
        val PhoneNum:String? = intent.getStringExtra("PhoneNum")
        swipeRef.setOnRefreshListener {
            refreshRecyclerView(PhoneNum)
            swipeRef.isRefreshing = false
        }
        refreshRecyclerView(PhoneNum)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
            else -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    var applicationId = "635259bad01c7e001c96a57e"
    override fun onItemClick(data: DATA) {
        val extra = BootExtra()
            .setCardQuota("0,2,3") // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)
        val items: MutableList<BootItem> = ArrayList()
        val item = BootItem().setName("${data.Menu.get(0).get("name").toString()} 외 ${data.Menu.size}개").setId("ITEM_CODE_MOUSE").setQty(1).setPrice(data.Price.toString().toDouble())
        items.add(item)
        val payload = Payload()
        val orderTime = data.OrderTime

        payload.setApplicationId(applicationId)
            .setOrderName(data.ShopName)
            //.setPg("페이레터")
//            .setMethod("카드자동")
            .setOrderId("1234")
            .setPrice(data.Price.toString().toDouble())
            .setUser(getBootUser(data))
            .setExtra(extra).items = items

        val map: MutableMap<String, Any> = HashMap()
        map["1"] = "abcdef"
        map["2"] = "abcdef55"
        map["3"] = 1234
        payload.metadata = map
        //        payload.setMetadata(new Gson().toJson(map));
        Bootpay.init(supportFragmentManager, applicationContext)
            .setPayload(payload)
            .setEventListener(object : BootpayEventListener {
                override fun onCancel(data: String) {
                    Log.d("bootpay", "cancel: $data")
                }

                override fun onError(data: String) {
                    Log.d("bootpay", "error: $data")
                }

                override fun onClose() {
                    Log.d("bootpay", "close: ")
                    Bootpay.removePaymentWindow()
                    finish()
                    val intent = intent //인텐트
                    startActivity(intent) //액티비티 열기
                }

                override fun onIssued(data: String) {
                    Log.d("bootpay", "issued: $data")
                }

                override fun onConfirm(data: String): Boolean {
                    Log.d("bootpay", "confirm: $data")
                    //                        Bootpay.transactionConfirm(data); //재고가 있어서 결제를 진행하려 할때 true (방법 1)
                    return true //재고가 있어서 결제를 진행하려 할때 true (방법 2)
                    //                        return false; //결제를 진행하지 않을때 false
                }

                override fun onDone(data: String) {
                    Log.d("done", data)
                    db.collection("User").document(orderTime).update(mapOf("OrderState" to "payOk"))
                }
            }).requestPayment()
    }
    fun getBootUser(data: DATA): BootUser? {
//        val userId = "123411aaaaaaaaaaaabd4ss121"
        val user = BootUser()
//        user.id = userId
//        user.area = "서울"
//        user.gender = 1 //1: 남자, 0: 여자
//        user.email = "test1234@gmail.com"
//        user.phone = data.PhoneNum.toString()
//        user.birth = "1988-06-10"
//        user.username = "홍길동"
        return user
    }

    private fun refreshRecyclerView(PhoneNum:String?){
        recyclerView.adapter = OrderShareAdapter(this,this, PhoneNum = PhoneNum)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}