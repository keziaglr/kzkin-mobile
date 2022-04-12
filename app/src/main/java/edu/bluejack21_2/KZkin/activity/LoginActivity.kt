package edu.bluejack21_2.KZkin.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import edu.bluejack21_2.KZkin.R
import edu.bluejack21_2.KZkin.model.User

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth;
    private lateinit var googleSignInClient: GoogleSignInClient
    companion object{
        private const val RC_SIGN_IN = 120
    }
    val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth

        val linkRegister = findViewById<TextView>(R.id.link_register)
        linkRegister!!.setOnClickListener{
            val goToRegister = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(goToRegister)
        }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("109122604925-vc57vh7nfl1edh697c37b0uudprnlg73.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val buttonLoginGoogle = findViewById<TextView>(R.id.buttonSubmitLoginGoogle)
        buttonLoginGoogle!!.setOnClickListener{
            signInWithGoogle()
        }

        val inputPassword = findViewById<TextInputLayout>(R.id.inputLoginPassword)
        val inputEmail = findViewById<TextInputLayout>(R.id.inputLoginEmail)

        val buttonLogin = findViewById<TextView>(R.id.buttonSubmitLogin)
        buttonLogin.setOnClickListener {
            val email = inputEmail.editText!!.text.toString()
            val password = inputPassword.editText!!.text.toString()
            if (password.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, getString(R.string.err_field_empty), Toast.LENGTH_LONG).show()
            }else {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                baseContext,
                                getString(R.string.succ_login),
                                Toast.LENGTH_SHORT
                            ).show()
                            val docRef =  db.collection("users").document(auth.currentUser!!.uid)
                            var user: User? = null
                            docRef.get()
                                .addOnSuccessListener {  document ->
                                    if(document != null){
                                        user = document.toObject(User::class.java)!!
                                        Toast.makeText(applicationContext, getString(R.string.succ_login),Toast.LENGTH_SHORT).show()

                                        var goToHome: Intent? = null
                                        if(user!!.role == "admin"){
                                            goToHome = Intent(this, MainActivityAdmin::class.java)
                                        }else{
                                            goToHome = Intent(this, MainActivityUser::class.java)
                                        }

                                        startActivity(goToHome)
                                        finish()
                                    }
                                }
                        } else {
                            Toast.makeText(
                                baseContext,
                                getString(R.string.err_login),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }

    }
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if(task.isSuccessful){
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("MainActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.w("MainActivity", "Google sign in failed", e)
                }
            }else{
                Log.w("MainActivity", exception.toString())
            }

        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("MainActivity", "signInWithCredential:success")

                    val docRef =  db.collection("users").document(auth.currentUser!!.uid)
                    var user: User? = null
                    docRef.get()
                        .addOnSuccessListener {  document ->
                            if(document == null){
                                user = User("", auth.currentUser!!.displayName, "", auth.currentUser!!.phoneNumber, auth.currentUser!!.email, null, "", "", auth.currentUser!!.photoUrl.toString(), "user", Timestamp.now(), Timestamp.now())
                                db.collection("users").document(auth.currentUser!!.uid).set(user!!, SetOptions.merge())
                            }else{
                                user = document.toObject(User::class.java)!!
                            }
                            Toast.makeText(applicationContext, getString(R.string.succ_login),Toast.LENGTH_SHORT).show()

                            var goToHome: Intent? = null
                            if(user!!.role == "admin"){
                                goToHome = Intent(this, MainActivityAdmin::class.java)
                            }else{
                                goToHome = Intent(this, MainActivityUser::class.java)
                            }

                            startActivity(goToHome)
                            finish()
                        }
                } else {
                    Log.w("MainActivity", getString(R.string.err_login), task.exception)
                }
            }
    }
}