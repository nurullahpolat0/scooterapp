package com.nurullah.scooterapp.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.nurullah.scooterapp.R
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.nurullah.scooterapp.models.Response
import com.nurullah.scooterapp.models.UserModel

class ProfileActivity : AppCompatActivity() {
    private var btnExit: ImageView? = null
    private var email: TextView? = null
    private var puan: TextView? = null
    private var auth : FirebaseAuth?= null
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("users")
    var uid = ""
    val response = Response()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        btnExit = findViewById(R.id.exit) as ImageView
        email = findViewById(R.id.email) as TextView
        puan = findViewById(R.id.puan) as TextView
        auth = FirebaseAuth.getInstance()

        uid = intent.getStringExtra("uid")!!

        btnExit!!.setOnClickListener {
            Firebase.auth.signOut()
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        myRef.get().addOnCompleteListener { task ->

            if (task.isSuccessful) {
                val result = task.result
                result?.let {
                    response.users = result.children.map { snapShot ->
                        snapShot.getValue(UserModel::class.java)!!
                    }
                    for (item in response.users!!) {
                        if (item.uid.equals(uid)) {
                            email!!.text = item.email
                            puan!!.text = item.starCount.toString()+" Puan"
                        }
                    }
                }
            } else {
                response.exception = task.exception
            }

        }
    }
}