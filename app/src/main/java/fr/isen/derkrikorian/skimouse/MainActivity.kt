package fr.isen.derkrikorian.skimouse

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.derkrikorian.skimouse.Network.Lift
import fr.isen.derkrikorian.skimouse.Network.Slope
import fr.isen.derkrikorian.skimouse.Network.SlopeDifficulty
import fr.isen.derkrikorian.skimouse.Network.SlopeDifficulty.Companion.getSlopeImageResource
import fr.isen.derkrikorian.skimouse.Network.User
import fr.isen.derkrikorian.skimouse.composables.TopBar
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference

        setContent {
            SkiMouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TopBar()
                }
            }
        }

        writeTest()
        readTest()
    }

    private fun writeTest() {
        val user = User("Ada", "Lovelace", 1815)
        database.child("users").child(user.firstName ?: "").setValue(user)
    }

    private fun readTest() {
        val userReference = database.child("users").child("Ada")

        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                Log.d(TAG, "User is: ${user?.firstName} ${user?.lastName}")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }
        })
    }

    companion object {
        private const val TAG = "ReadWriteTest"
        const val KEY_ROUTE = "route_key"
    }
}

@Composable
fun SlopeView(
    database: DatabaseReference,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    searchQuery: String = "",
    showOpenOnly: Boolean = false
) {
    val context = LocalContext.current
    val slopes = remember { mutableStateListOf<Slope>() }
    val slopesReference = database.child("slopes")

    LaunchedEffect(slopesReference) {
        slopesReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var slopeIdCounter = 0
                slopes.clear()
                for (slopeSnapshot in dataSnapshot.children) {
                    val slope = slopeSnapshot.getValue(Slope::class.java)
                    slope?.id = slopeIdCounter
                    if (slope != null) {
                        slopes.add(slope)
                    }
                    slopeIdCounter += 1
                }
                slopes.sortBy { SlopeDifficulty.fromString(it.color ?: "").value }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }
        })
    }

    LazyColumn(modifier = modifier.padding(innerPadding)) {
        items(slopes.filter {
            it.name?.contains(
                searchQuery,
                ignoreCase = true
            ) == true && (!showOpenOnly || it.status == true)
        }) { slope ->
            val color = Color(android.graphics.Color.parseColor(slope.color ?: ""))
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 35.dp, vertical = 13.dp)
                    .clickable {
                        val intent = slope.createDetailIntent(context)
                        context.startActivity(intent)
                    }
            ) {
                Image(
                    painter = painterResource(id = getSlopeImageResource(slope.color)),
                    contentDescription = "Slope",
                    modifier = Modifier.size(40.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp)
                ) {
                    ItemDetailsView(item = slope, color = color)
                }
                Text(text = slope.status?.let {
                    if (it) stringResource(id = R.string.OpenStatus)
                    else stringResource(id = R.string.CloseStatus)
                } ?: stringResource(id = R.string.UnknownStatus))
            }
            Divider(color = Color.Gray)
        }
    }
}

@Composable
fun LiftView(
    database: DatabaseReference,
    modifier: Modifier = Modifier,
    innerPadding: PaddingValues,
    searchQuery: String = "",
    showOpenOnly: Boolean = false
) {
    val lifts = remember { mutableStateListOf<Lift>() }
    val liftsReference = database.child("lifts")
    val context = LocalContext.current

    LaunchedEffect(liftsReference) {
        liftsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var liftIdCounter = 0
                lifts.clear()
                for (liftSnapshot in dataSnapshot.children) {
                    val lift = liftSnapshot.getValue(Lift::class.java)
                    lift?.id = liftIdCounter
                    if (lift != null) {
                        lifts.add(lift)
                    }
                    liftIdCounter += 1
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }
        })
    }

    LazyColumn(modifier = modifier.padding(innerPadding)) {
        items(lifts.filter {
            it.name?.contains(
                searchQuery,
                ignoreCase = true
            ) == true && (!showOpenOnly || it.status == true)
        }) { lift ->
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 35.dp, vertical = 13.dp)
                    .clickable {
                        val intent = lift.createDetailIntent(context)
                        context.startActivity(intent)
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ski_lift),
                    contentDescription = "Lift",
                    modifier = Modifier.size(40.dp)
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 20.dp)
                ) {
                    ItemDetailsView(item = lift, color = null)
                }
                Text(text = lift.status?.let {
                    if (it) stringResource(id = R.string.OpenStatus)
                    else stringResource(id = R.string.CloseStatus)
                } ?: stringResource(id = R.string.UnknownStatus))
            }
            Divider(color = Color.Gray)
        }
    }
}

fun Lift.createDetailIntent(context: Context): Intent {
    val intent = Intent(context, DetailActivity::class.java)
    intent.putExtra("lift_Name", this.name)
    intent.putExtra("connected_slope", this.connectedSlope?.joinToString(", "))
    intent.putExtra("lift_Status", this.status)
    intent.putExtra("lift_Type", this.type)
    intent.putExtra("lift_id", this.id)
    intent.putExtra("item_Type", "lift")
    return intent
}

fun Slope.createDetailIntent(context: Context): Intent {
    val intent = Intent(context, DetailActivity::class.java)
    intent.putExtra("slope_name", this.name)
    intent.putExtra("slope_color", this.color ?: "")
    intent.putExtra("is_open", this.status ?: false)
    intent.putExtra("slope_id", this.id)
    intent.putExtra("item_Type", "slope")
    return intent
}

@Composable
fun ItemDetailsView(item: Any, color: Color? = null) {
    Column {
        Text(
            text = when (item) {
                is Lift -> item.name?.uppercase() ?: stringResource(id = R.string.UnknownStatus)
                is Slope -> item.name?.uppercase() ?: stringResource(id = R.string.UnknownStatus)
                else -> stringResource(id = R.string.UnknownStatus)
            },
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = when (item) {
                    is Lift -> stringResource(id = R.string.LiftType) + " - " + (item.type
                        ?: stringResource(id = R.string.UnknownStatus))

                    is Slope -> stringResource(id = R.string.Color)
                    else -> ""
                },
                modifier = Modifier.padding(end = 8.dp),
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
            )
            if (item is Slope) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawCircle(color = color ?: Color.Transparent)
                }
            }
        }
    }
}