package com.nurullah.scooterapp.activities

import android.R.attr
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.nurullah.scooterapp.R
import android.R.attr.accountType
import com.nurullah.scooterapp.models.UserModel


class SingupActivity: AppCompatActivity() {

    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var btnSignIn: Button? = null
    private var btnSignUp: Button? = null

    private var auth : FirebaseAuth?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_singup)

//        add my code
        auth = FirebaseAuth.getInstance()
        btnSignIn = findViewById(R.id.signinbutton) as Button
        btnSignUp = findViewById(R.id.signupbutton) as Button
        inputEmail = findViewById(R.id.email) as EditText
        inputPassword = findViewById(R.id.password) as EditText
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("users")
        btnSignIn!!.setOnClickListener {
            finish()
        }
        btnSignUp!!.setOnClickListener{
            val email = inputEmail!!.text.toString().trim()
            val password = inputPassword!!.text.toString().trim()

            //create user
            auth!!.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, OnCompleteListener {
                        task ->
                    if (!task.isSuccessful){
                        Toast.makeText(this@SingupActivity,"User Not crated",Toast.LENGTH_SHORT).show()
                        return@OnCompleteListener
                    }else{
                        val key = myRef.push().key
                        val user = UserModel(key,email, password, 0)
                        myRef.child(key!!).setValue(user)
                        startActivity(Intent(this@SingupActivity, MainActivity::class.java))
                        finish()
                    }
                })

        }
    }
}