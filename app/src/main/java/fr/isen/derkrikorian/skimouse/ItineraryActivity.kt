package fr.isen.derkrikorian.skimouse

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme

class ItineraryActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkiMouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            CustomTopBar()
                        }
                    ) {
                        ItineraryView("Android")
                        ItineraryDetails()
                    }
                }
            }
        }
    }
}

@Composable
fun ItineraryView(name: String) {
    var departInput by remember { mutableStateOf("") }
    var destinationInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .padding(top = 75.dp)
            .padding(horizontal = 20.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        OutlinedTextField(
            value = departInput,
            onValueChange = { departInput = it },
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text("Point de départ") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { }),
            leadingIcon = {
                Text("⛺️")
            },
        )
        OutlinedTextField(
            value = destinationInput,
            onValueChange = { destinationInput = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            placeholder = { Text("Point d'arrivé") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { }),
            leadingIcon = {
                Text("\uD83D\uDEA9")
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = colorResource(id = R.color.orange),
                focusedBorderColor = colorResource(id = R.color.orange),
                unfocusedPlaceholderColor = colorResource(id = R.color.orange),
            )
        )

        Button(
            onClick = { },
            modifier = Modifier
                .align(Alignment.End)
                .height(35.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.orange))
        ) {
            Text("Rechercher")
        }
    }
}

@Composable
fun ItineraryDetails(modifier: Modifier = Modifier) {
    var nbTrajet by remember { mutableIntStateOf(0) }
    var departInput by remember { mutableStateOf("") }
    var destinationInput by remember { mutableStateOf("") }

    if (departInput.isEmpty()) {
        departInput = "Non spécifié"
    }
    if (destinationInput.isEmpty()) {
        destinationInput = "Non spécifiée"
    }

    LazyColumn(
        modifier = modifier.padding(top = 250.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Row {
                    Text("Départ → ")
                    Text(departInput, color = colorResource(id = R.color.orange))
                }
                Row {
                    Text("Destination → ")
                    Text(destinationInput, color = colorResource(id = R.color.orange))
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 15.dp)
                ) {
                    Text("$nbTrajet", fontSize = 20.sp, color = colorResource(id = R.color.orange))
                    Text(
                        "trajets trouvés \uD83D\uDCCD",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(start = 5.dp)
                    )
                }
            }
        }
        item {
            if (nbTrajet == 0) {
                Text("Aucun trajet trouvé", modifier = Modifier.padding(start = 20.dp), color = colorResource(id = R.color.orange))
            } else {
                repeat(nbTrajet) { numeroTrajet ->
                    ItineraryItem(listOf("Point1", "Point2", "Point3", "Point4", "Point5"), numeroTrajet + 1)
                }
            }
        }
    }
}

@Composable
fun ItineraryItem(liste1: List<String>, numeroTrajet: Int) {
    if (liste1.isEmpty()) {
        return
    }

    Text("Trajet $numeroTrajet", modifier = Modifier.padding(start = 20.dp), color = colorResource(id = R.color.orange))
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(start = 20.dp, bottom = 15.dp)
    ) {
        liste1.forEach { item ->
            if (item != liste1.first()){
                Text(" →", color = colorResource(id = R.color.orange))
            }
            Text(
                text = item,
            )
        }
        Text("\uD83D\uDEA9")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    SkiMouseTheme {
        ItineraryView("Android")
    }
}