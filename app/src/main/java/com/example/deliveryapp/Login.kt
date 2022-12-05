package com.example.deliveryapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_place_your_order.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timer
import kotlin.math.log


class Login:AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    var verificationId = ""
    var sendingCheck = 1
    private var backKeyPressedTime : Long = 0
    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
        else {
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        edit_phoneNum.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(edit_phoneNum.length() == 11)
                {
                    btn_authentic.isEnabled = true
                    btn_authentic.setTextColor(Color.parseColor("#009688"))
                }
                else{
                    btn_authentic.isEnabled = false
                    btn_authentic.setTextColor(ContextCompat.getColor(application!!, R.color.black_overlay))
                }
            }
        })
        edit_authenticNum.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if(edit_authenticNum.length() > 0 && sendingCheck == 0)
                {
                    btn_authentic2.isEnabled = true
                    btn_authentic2.setBackgroundColor(ContextCompat.getColor(application!!, R.color.teal_700))
                    btn_authentic2.setTextColor(Color.WHITE)
                }
                else{
                    btn_authentic2.isEnabled = false
                    btn_authentic2.setBackgroundColor(ContextCompat.getColor(application!!,R.color.contentBadgeTitle))
                }
            }
        })

        btn_authentic.setOnClickListener{
            sendingCheck = 0
            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) { }
                override fun onVerificationFailed(e: FirebaseException) {
                }
                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    this@Login.verificationId = verificationId
                }
            }
            val phoneEdit = edit_phoneNum.text.toString().substring(1)
            val optionsCompat =  PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber("+82${phoneEdit}")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(optionsCompat)
            auth.setLanguageCode("kr")
            Toast.makeText(this, "인증번호가 전송되었습니다.", Toast.LENGTH_SHORT).show()
        }
        btn_authentic2.setOnClickListener{
            val credential = PhoneAuthProvider.getCredential(verificationId, edit_authenticNum.text.toString())
            signInWithPhoneAuthCredential(credential)
        }
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                var a= 1
                if (task.isSuccessful) {
                    Toast.makeText(this, "인증이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                    db.collection("Admin").whereEqualTo("PhoneNum", edit_phoneNum.text.toString()).get().addOnSuccessListener { result ->
                        for(document in result){
                            val ShopName = document.id
                            if(document.data.get("PhoneNum").toString() == edit_phoneNum.text.toString()){
                                val intent = Intent(this@Login, AdminActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("ShopName", ShopName)
                                startActivity(intent)
                                a=0
                            }
                        }
                        if(a==1){
                            val intent = Intent(this@Login, CarouselActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            intent.putExtra("PhoneNum",edit_phoneNum.text.toString())
                            startActivity(intent)
                        }
                    }
                }
                else {
                    Toast.makeText(this, "인증번호를 다시 확인해 주세요.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}