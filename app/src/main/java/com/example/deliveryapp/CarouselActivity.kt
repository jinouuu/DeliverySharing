package com.example.deliveryapp

import android.content.Intent
import android.os.Bundle
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import com.example.deliveryapp.R
import kotlinx.android.synthetic.main.activity_login.*

class CarouselActivity : AppCompatActivity() {
    private var selectedIndex: Int = 0;

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

        val layout = intent.getIntExtra("layout_file_id", R.layout.demo1)
        val PhoneNum = intent.getStringExtra("PhoneNum")
        setContentView(layout)
        val actionBar: ActionBar? = supportActionBar
        actionBar?.setTitle("딜리버리 쉐어링")

        val motionLayout = findViewById<MotionLayout>(R.id.motion_container)

        val v1 = findViewById<View>(R.id.v1)
        val v2 = findViewById<View>(R.id.v2)
        val v3 = findViewById<View>(R.id.v3)

        v1.setOnClickListener {
            if (selectedIndex == 0){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            else {
                motionLayout.setTransition(R.id.s2, R.id.s1) //orange to blue transition
                motionLayout.transitionToEnd()
                selectedIndex = 0;
            }
        }
        v2.setOnClickListener {
            if (selectedIndex == 1){
                val intent = Intent(this,OrderShareActivity::class.java)
                intent.putExtra("PhoneNum",PhoneNum)
                startActivity(intent)
            }
            else if (selectedIndex == 2) {
                motionLayout.setTransition(R.id.s3, R.id.s2)  //red to orange transition
            } else {
                motionLayout.setTransition(R.id.s1, R.id.s2) //blue to orange transition
            }
            motionLayout.transitionToEnd()
            selectedIndex = 1;
        }
        v3.setOnClickListener {
            if (selectedIndex == 2) {
                val intent = Intent(this,Login::class.java)
                startActivity(intent)
            }
            else{
                motionLayout.setTransition(R.id.s2, R.id.s3) //orange to red transition
                motionLayout.transitionToEnd()
                selectedIndex = 2;
            }
        }
    }
}