package com.nurullah.scooterapp.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nurullah.scooterapp.R
import com.nurullah.scooterapp.models.Response
import com.nurullah.scooterapp.models.UserModel

class SuccesActivity: AppCompatActivity() {
    private var auth: FirebaseAuth?= FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("users")
    val response = Response()
    private var btnProfile: Button? =null
    private var btnMain : Button?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_succes)

        var uid = ""
        var starCount = 0
        btnProfile = findViewById(R.id.profil) as Button
        btnMain = findViewById(R.id.anasayfa) as Button

        btnProfile!!.setOnClickListener {
            startActivity(Intent(this@SuccesActivity,LoginActivity::class.java))
        }

        btnMain!!.setOnClickListener {
            startActivity(Intent(this@SuccesActivity,MainActivity::class.java))
        }

        myRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response.users = result.children.map { snapShot ->
                        snapShot.getValue(UserModel::class.java)!!
                    }
                    for (item in response.users!!) {
                        if (item.email.equals(auth!!.currentUser?.email)) {
                            uid = item.uid!!
                            starCount = item.starCount!!
                        }
                    }
                    starCount += 10
                    myRef.child(uid).child("starCount").setValue(starCount)
                }
            } else {
                response.exception = task.exception
            }
        }
    }
}