package fr.isen.derkrikorian.skimouse

import android.media.Image
import androidx.compose.ui.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme

class DetailActivitySlope : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Récupérer les données transmises depuis l'intent
        val slopeName = intent.getStringExtra("slope_name") ?: ""
        val slopeColorString = intent.getStringExtra("slope_color") ?: ""
        val isOpen = intent.getBooleanExtra("is_open", false)

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
                        isOpen = isOpen
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
    Row {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Slope Image",
            modifier = Modifier.size(100.dp)
        )
    }
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Slope Name: $name")
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Color: ")
            Canvas(modifier = Modifier.size(20.dp)) {
                drawCircle(color = color)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Status: ${if (isOpen) "Open" else "Closed"}",
            color = if (isOpen) Color.Green else Color.Red
        )
    }
}
