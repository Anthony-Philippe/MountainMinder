package fr.isen.derkrikorian.skimouse

import android.app.Activity
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation

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
                    Greeting2("Android")
                }
            }
        }
    }
}



@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Référence à FirebaseAuth
    val auth = FirebaseAuth.getInstance()

    var isLogin by remember { mutableStateOf(true) }
    var passwordConfirmation by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top =0.dp)

        ) {
            Image(
                painter = painterResource(id = R.drawable.flocon),
                contentDescription = "behind logo",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()

            )
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(250.dp)
                )
                Text(
                    text = stringResource(id = R.string.logo_title),
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    ),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent ,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(color = Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = stringResource(id = R.string.log_form1)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = colorResource(id =R.color.grey),
                        unfocusedBorderColor =colorResource(id =R.color.orange),
                        unfocusedLabelColor = colorResource(id =R.color.grey),
                        unfocusedLeadingIconColor = colorResource(id =R.color.orange),
                        focusedBorderColor = colorResource(id =R.color.orange),
                        unfocusedContainerColor = colorResource(id =R.color.orange).copy(alpha = 0.2f),

                    ),

                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = stringResource(id = R.string.log_form2)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),

                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedTextColor = colorResource(id =R.color.orange),
                        unfocusedBorderColor =colorResource(id =R.color.orange),
                        unfocusedLabelColor = colorResource(id =R.color.grey),
                        unfocusedLeadingIconColor = colorResource(id =R.color.orange),
                        focusedBorderColor = colorResource(id =R.color.orange),
                        unfocusedContainerColor = colorResource(id =R.color.orange).copy(alpha = 0.2f),

                        ),
                    visualTransformation = PasswordVisualTransformation()
                )

                if (!isLogin) {
                    // New OutlinedTextField for confirming password
                    OutlinedTextField(
                        value = passwordConfirmation,
                        onValueChange = { passwordConfirmation = it },
                        label = { Text(text = "Confirmer le mot de passe") }, // Provide appropriate label
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = colorResource(id = R.color.orange),
                            unfocusedBorderColor = colorResource(id = R.color.orange),
                            unfocusedLabelColor = colorResource(id = R.color.grey),
                            unfocusedLeadingIconColor = colorResource(id = R.color.orange),
                            focusedBorderColor = colorResource(id = R.color.orange),
                            unfocusedContainerColor = colorResource(id = R.color.orange).copy(alpha = 0.2f),
                        ),
                        visualTransformation = PasswordVisualTransformation()
                    )
                }

                Button(
                    onClick = {
                        if (isLogin) {
                            // Handle login
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(context as Activity) { task ->
                                    if (task.isSuccessful) {
                                        Log.d(TAG, "signInWithEmail:success")
                                        // Redirect user to another activity
                                    } else {
                                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            // Handle registration
                            if (password == passwordConfirmation) {
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(context as Activity) { task ->
                                        if (task.isSuccessful) {
                                            Log.d(TAG, "createUserWithEmail:success")
                                            // Redirect user to another activity
                                        } else {
                                            Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                            Toast.makeText(context, "Registration failed.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                            } else {
                                Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(200.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(colorResource(id = R.color.orange))
                ) {
                    Text(
                        text = if (isLogin) stringResource(id = R.string.Boutton_log) else stringResource(
                            id = R.string.Boutton_sign
                        ),
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        ),
                    )
                }

                // Toggle button
                Text(
                    text = if (isLogin) stringResource(id = R.string.Boutton_sign) else stringResource(
                        id = R.string.Boutton_log
                    ),
                    color = colorResource(id = R.color.grey),
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier
                        .clickable {
                            isLogin = !isLogin
                        }
                        .align(Alignment.CenterHorizontally),
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
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
        Greeting2("Android")
    }
}