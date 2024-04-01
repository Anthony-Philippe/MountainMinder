package fr.isen.derkrikorian.skimouse

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
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
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import fr.isen.derkrikorian.skimouse.composables.CustomOutlinedTextField
import fr.isen.derkrikorian.skimouse.composables.Header
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme

class LogActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkiMouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    LogView()
                }
            }
        }
    }
}

enum class StringResource(val value: Int) {
    LOG_FORM_0(R.string.log_form0),
    LOG_FORM_1(R.string.log_form1),
    LOG_FORM_2(R.string.log_form2),
    LOG_FORM_3(R.string.log_form3),
    BUTTON_LOG(R.string.Boutton_log),
    BUTTON_SIGN(R.string.Boutton_sign)
}

@Composable
fun LogView() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirmation by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    var isLogin by remember { mutableStateOf(true) }

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
                    labelId = StringResource.LOG_FORM_1.value,
                    modifier = Modifier.fillMaxWidth()
                )
                if (!isLogin) {
                    CustomOutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        leadingIcon = Icons.Default.Person,
                        labelId = StringResource.LOG_FORM_0.value,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                CustomOutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    hiddenPassword = true,
                    leadingIcon = Icons.Default.Lock,
                    labelId = StringResource.LOG_FORM_2.value,
                    modifier = Modifier.fillMaxWidth()
                )
                if (!isLogin) {
                    CustomOutlinedTextField(
                        value = passwordConfirmation,
                        onValueChange = { passwordConfirmation = it },
                        leadingIcon = Icons.Default.Lock,
                        labelId = StringResource.LOG_FORM_3.value,
                        hiddenPassword = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Button(
                    onClick = {
                        userAuthentication(
                            context = context,
                            auth = auth,
                            email = email,
                            password = password,
                            username = username,
                            isLogin = isLogin,
                            passwordConfirmation = passwordConfirmation
                        )
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
                        text = if (isLogin) stringResource(id = StringResource.BUTTON_LOG.value) else stringResource(
                            id = StringResource.BUTTON_SIGN.value
                        ),
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold
                        ),
                    )
                }

                Text(
                    text = if (isLogin) stringResource(id = StringResource.BUTTON_SIGN.value) else stringResource(
                        id = StringResource.BUTTON_LOG.value
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

fun startMainActivity(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.flags =
        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    context.startActivity(intent)
}

fun extractUsername(email: String): String {
    val atIndex = email.indexOf('@')
    return if (atIndex != -1) {
        email.substring(0, atIndex)
    } else {
        email
    }
}

fun userAuthentication(
    context: Context,
    auth: FirebaseAuth,
    email: String,
    password: String,
    username: String,
    isLogin: Boolean,
    passwordConfirmation: String? = null
) {
    if (isLogin) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithEmail:success")
                    val currentUser = auth.currentUser
                    currentUser?.let {
                        startMainActivity(context)
                    }
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
        if (password == passwordConfirmation && username.isNotEmpty()) {
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
                                "username" to username
                            )
                            usersRef.child(uid).setValue(userData)
                        }
                        startMainActivity(context)
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
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
}