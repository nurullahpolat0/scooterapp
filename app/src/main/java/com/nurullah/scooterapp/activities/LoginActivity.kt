package com.nurullah.scooterapp.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nurullah.scooterapp.R
import com.nurullah.scooterapp.databinding.ActivityMainBinding
import com.nurullah.scooterapp.models.Response
import com.nurullah.scooterapp.models.UserModel

class LoginActivity : AppCompatActivity() {
    private val binding by lazy { DataBindingUtil.setContentView<ActivityMainBinding>(this, com.nurullah.scooterapp.R.layout.activity_login) }
    private var inputEmail: EditText? = null
    private var inputPassword:EditText? = null
    private var btnSignup: Button? =null
    private var btnLogin :Button?=null
    private var auth: FirebaseAuth?= FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("users")
    val response = Response()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        var uid = ""
        if (auth!!.currentUser !=null){
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
                            }
                        }
                        val intent = Intent(this, ProfileActivity::class.java)
                        intent.putExtra("uid", uid)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    response.exception = task.exception
                }
            }
        }
        inputEmail = findViewById(R.id.email) as EditText
        inputPassword = findViewById(R.id.password) as EditText
        btnSignup = findViewById(R.id.signbutton) as Button
        btnLogin = findViewById(R.id.login) as Button

        btnSignup!!.setOnClickListener {
            startActivity(Intent(this@LoginActivity,SingupActivity::class.java))
        }

        btnLogin!!.setOnClickListener {
            val email = inputEmail!!.text.toString().trim()
            val password = inputPassword!!.text.toString().trim()

            auth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener {
                        task ->

                    if (task.isSuccessful){
                        myRef.get().addOnCompleteListener { taskRef ->
                            if (taskRef.isSuccessful) {
                                val result = taskRef.result
                                result?.let {
                                    response.users = result.children.map { snapShot ->
                                        snapShot.getValue(UserModel::class.java)!!
                                    }
                                    for (item in response.users!!) {
                                        if (item.email.equals(auth!!.currentUser?.email)) {
                                            uid = item.uid!!
                                        }
                                    }
                                    val intent = Intent(this, ProfileActivity::class.java)
                                    intent.putExtra("uid", uid)
                                    startActivity(intent)
                                    finish()
                                }
                            } else {
                                response.exception = task.exception
                            }
                        }
                    }
                })
        }
    }
}