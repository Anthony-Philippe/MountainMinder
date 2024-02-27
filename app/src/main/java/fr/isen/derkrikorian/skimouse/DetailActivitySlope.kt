package fr.isen.derkrikorian.skimouse

import android.media.Image
import androidx.compose.ui.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme
import androidx.compose.ui.text.style.TextAlign



class DetailActivitySlope : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupérer les données transmises depuis l'intent
        val slopeName = intent.getStringExtra("slope_name") ?: ""
        val slopeColorString = intent.getStringExtra("slope_color") ?: ""
        val isOpen = intent.getBooleanExtra("is_open", false)
        val slopeColorRabbit = intent.getStringExtra("slope_color") ?: ""

        val slopeColor = if (slopeColorString.isNotEmpty()) {
            parseColor(slopeColorString)
        } else {
            Color.Transparent // Ou une autre couleur par défaut
        }

        setContent {
            SkiMouseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SlopeDetails(
                        name = slopeName,
                        color = slopeColor,
                        isOpen = isOpen,
                    )
                }

            }
        }
    }
}
fun parseColor(colorString: String): Color {
    return Color(android.graphics.Color.parseColor(colorString))
}
@Composable
fun SlopeDetails(name: String, color: Color, isOpen: Boolean, modifier: Modifier = Modifier) {
    var commentaire by remember { mutableStateOf("") }
    val colorHex = "#${Integer.toHexString(color.toArgb()).substring(2)}"
    var note: Int by remember { mutableStateOf(0) }
    var open = ""
    if(isOpen == true) {
        open = "Ouverte"
    } else {
        open = "Fermée"
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Slope Image",
            modifier = Modifier.size(100.dp)
        )
        Image(
            painter = painterResource(id = R.drawable.profil),
            contentDescription = "Profile Image",
            modifier = Modifier.size(70.dp)
        )

    }
    LazyColumn(
        modifier = modifier.padding(top = 100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$name",
                    fontSize = 40.sp
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Couleur : ",
                            fontSize = 25.sp
                        )
                        Canvas(modifier = Modifier.size(30.dp)) {
                            drawCircle(color = color)
                        }
                    }
                    Text(
                        text = "Etat: ${if (isOpen) "Ouverte" else "Fermée"}",
                        fontSize = 25.sp
                    )
                }
                Row(
                    modifier = Modifier.padding(end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(
                            id = SlopeDifficulty.getRabbitImageResource(
                                colorHex
                            )
                        ),
                        contentDescription = "Rabbit Image",
                        modifier = Modifier.size(175.dp)
                    )
                }

            }
        }
        item {
            Text(
                text = "Est-ce que la piste : $name est toujours $open ?",
                fontSize = 25.sp,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,

                )
            Row( modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Ouverte")
                }
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Fermée")
                }
            }
        }
        item {
            Text(
                text ="Notez la piste",
                fontSize = 25.sp,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val jauneColor = Color(R.color.jaune)
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < note) Color.Blue else Color.LightGray,
                        modifier = Modifier
                            .clickable {
                                note = index + 1
                            }
                            .padding(4.dp)
                            .size(40.dp)
                    )
                }

            }
            OutlinedTextField(
                value = commentaire,
                onValueChange = { commentaire = it },
                label = { Text(text = stringResource(id = R.string.log_form4)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(10.dp),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = colorResource(id =R.color.grey),
                    unfocusedBorderColor = colorResource(id =R.color.grey),
                    unfocusedLabelColor = colorResource(id =R.color.grey),
                    unfocusedLeadingIconColor = colorResource(id =R.color.grey),
                    focusedBorderColor = colorResource(id =R.color.grey),
                    unfocusedContainerColor = colorResource(id =R.color.grey).copy(alpha = 0.2f),

                    ),
                )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(10.dp)
                    .border(1.dp, colorResource(id = R.color.grey),  shape = RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(color = colorResource(id = R.color.grey).copy(alpha = 0.2f)),

            ) {
                Column {
                    Text(
                        text = "Nom user",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(4.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Avis sur la piste",
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center
                    )
                }

            }

        }
    }

}
