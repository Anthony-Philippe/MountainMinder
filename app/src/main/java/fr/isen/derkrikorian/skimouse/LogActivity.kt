package fr.isen.derkrikorian.skimouse

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.derkrikorian.skimouse.composables.CustomOutlinedTextField
import fr.isen.derkrikorian.skimouse.composables.Header
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme

class LogActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkiMouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    LogView("Android")
                }
            }
        }
    }
}

@Composable
fun LogView(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    val auth = FirebaseAuth.getInstance()
    var isLogin by remember { mutableStateOf(true) }
    var passwordConfirmation by remember { mutableStateOf("") }

    fun extractUsername(email: String): String {
        val atIndex = email.indexOf('@')
        return if (atIndex != -1) {
            email.substring(0, atIndex)
        } else {
            email
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header()
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(color = Color.Transparent)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                CustomOutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    emailKeyBoard = true,
                    leadingIcon = Icons.Default.Email,
                    labelId = R.string.log_form1,
                    modifier = Modifier.fillMaxWidth()
                )
                CustomOutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    hiddenPassword = true,
                    leadingIcon = Icons.Default.Lock,
                    labelId = R.string.log_form2,
                    modifier = Modifier.fillMaxWidth()
                )
                if (!isLogin) {
                    CustomOutlinedTextField(
                        value = passwordConfirmation,
                        onValueChange = { passwordConfirmation = it },
                        leadingIcon = Icons.Default.Lock,
                        labelId = R.string.log_form3,
                        hiddenPassword = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        if (isLogin) {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(context as Activity) { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "signInWithEmail:success")
                                        val currentUser = auth.currentUser
                                        currentUser?.let {
                                            email = it.email ?: ""
                                            username = extractUsername(email)
                                        }
                                        val intent = Intent(context, MainActivity::class.java)
                                        context.startActivity(intent)
                                    } else {
                                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                                        Toast.makeText(
                                            context,
                                            "Authentication failed.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else {
                            if (password == passwordConfirmation) {
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(context as Activity) { task ->
                                        if (task.isSuccessful) {
                                            val user = auth.currentUser
                                            Log.d(TAG, "createUserWithEmail:success")
                                            user?.uid?.let { uid ->
                                                val database = Firebase.database
                                                val usersRef = database.getReference("users")
                                                val userData = mapOf(
                                                    "email" to email,
                                                    "uid" to uid,
                                                )
                                                usersRef.child(uid).setValue(userData)
                                            }
                                            val intent = Intent(context, MainActivity::class.java)
                                            context.startActivity(intent)
                                        } else {
                                            Log.w(
                                                TAG,
                                                "createUserWithEmail:failure",
                                                task.exception
                                            )
                                            Toast.makeText(
                                                context,
                                                "Registration failed.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Passwords do not match.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                        .width(200.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.orange))
                ) {
                    Text(
                        text = if (isLogin) stringResource(id = R.string.Boutton_log) else stringResource(
                            id = R.string.Boutton_sign
                        ),
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                }

                Text(
                    text = if (isLogin) stringResource(id = R.string.Boutton_sign) else stringResource(
                        id = R.string.Boutton_log
                    ),
                    color = colorResource(id = R.color.grey),
                    modifier = Modifier
                        .clickable { isLogin = !isLogin }
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 15.dp),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    ),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    SkiMouseTheme {
        LogView("Android")
    }
}