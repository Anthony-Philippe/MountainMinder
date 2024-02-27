package fr.isen.derkrikorian.skimouse

import android.media.Image
import androidx.compose.ui.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
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
    val colorHex = "#${Integer.toHexString(color.toArgb()).substring(2)}"
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
    }

}
