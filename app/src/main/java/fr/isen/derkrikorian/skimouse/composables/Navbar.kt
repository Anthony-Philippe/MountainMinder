package fr.isen.derkrikorian.skimouse.composables

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.isen.derkrikorian.skimouse.MainActivity
import fr.isen.derkrikorian.skimouse.R

@Composable
fun Navbar() {
    val context = LocalContext.current
    val logo: Painter = painterResource(id = R.drawable.logo)
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = {
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }) {
                Icon(
                    Icons.Outlined.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(40.dp)
                )
            }
        },
        actions = {
            Image(
                painter = logo,
                contentDescription = "Logo",
                modifier = Modifier
                    .size(55.dp)
                    .align(Alignment.CenterVertically)
            )
        },
        backgroundColor = Color.White
    )
}