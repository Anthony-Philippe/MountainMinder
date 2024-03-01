package fr.isen.derkrikorian.skimouse

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.derkrikorian.skimouse.MainActivity.Companion.KEY_ROUTE
import fr.isen.derkrikorian.skimouse.SlopeDifficulty.Companion.getSlopeImageResource
import fr.isen.derkrikorian.skimouse.composables.CustomOutlinedTextField
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme
import fr.isen.derkrikorian.skimouse.ui.theme.button
import fr.isen.touret.skimouse.Slope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var database: DatabaseReference

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            database = FirebaseDatabase.getInstance().reference
            SkiMouseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TopBar()
                    //BottomBar()
                }
            }
        }

        database = FirebaseDatabase.getInstance().reference

        // Write test
        writeTest()

        // Read test
        readTest()
    }

    private fun writeTest() {
        val user = User("Ada", "Lovelace", 1815)
        user.firstName?.let { database.child("users").child(it).setValue(user) }
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


enum class LiftType {
    TELECABINE,
    TELESIEGE,
    TELESKI,
    TAPIS,
}


@Composable
fun BottomBar(navController: NavController) {
    val items = listOf("SlopeView", "LiftView")

    BottomNavigation(
        backgroundColor = Color.White // Set the background color to white
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

        items.forEachIndexed { index, screen ->
            BottomNavigationItem(
                icon = {
                    // Use icons instead of text
                    if (index == 0) {
                        Image(
                            painter = painterResource(id = R.drawable.ski_stickman_black),
                            contentDescription = "Slope",
                            modifier = Modifier.size(40.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ski_lift),
                            contentDescription = "Lifts",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                },
                selected = currentRoute == screen,
                onClick = {
                    navController.navigate(screen) {
                        popUpTo = navController.graph.startDestinationId
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
@ExperimentalMaterial3Api
fun TopBar() {
    val logo: Painter = painterResource(id = R.drawable.logo)
    var textState = remember { mutableStateOf(TextFieldValue()) }
    val navController = rememberNavController()
    val items = listOf("SlopeView", "LiftView")
    var searchQuery by remember { mutableStateOf("") }
    var showOpenOnly by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current


    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))
                IconButton(onClick = { scope.launch { drawerState.close() } }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Profile Icon"
                    )
                }
                //add an Icon for a chat and when we click on it we go on ChatActivity
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center

                ) {
                    Text(
                        "Chat", style = TextStyle(
                            color = Color(0xFFFFA500),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                        modifier = Modifier.padding(16.dp)
                            .clickable {
                                val intent = Intent(context, ChatActivity::class.java)
                                context.startActivity(intent)
                            },
                    )
                    Image(

                        painter = painterResource(id = R.drawable.chat),
                        contentDescription = "Profile Icon",
                        modifier = Modifier.size(50.dp)
                            .clickable {
                                val intent = Intent(context, ChatActivity::class.java)
                                context.startActivity(intent)
                            }
                    )
                }
                TextButton(onClick = {
                }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Icon(
                            imageVector = Icons.Outlined.ExitToApp,
                            contentDescription = "Leave Icon",
                            tint = Color(0xFFFFA500),
                            modifier = Modifier.padding(start = 100.dp)
                        )
                        Text(
                            "Logout", modifier = Modifier.padding(16.dp), style = TextStyle(
                                color = Color(0xFFFFA500),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                Column {
                    Row(
                        modifier = Modifier
                            .height(80.dp)
                            .padding(top = 20.dp)
                            .fillMaxWidth()
                    ) {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Profile Icon",
                                modifier = Modifier.size(60.dp)
                            )
                        }
                        CustomOutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            labelId = R.string.SearchPlaceholder,
                            searchbar = true,
                            modifier = Modifier
                                .weight(1f)
                        )
                        Image(
                            painter = logo,
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(55.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { showOpenOnly = !showOpenOnly },
                        modifier = Modifier
                            .height(30.dp)
                            .padding(start = 10.dp, end = 10.dp),
                        colors = ButtonDefaults.buttonColors(colorResource(id = R.color.orange)) // Set the background color to dark blue
                    ) {
                        Text(
                            text = if (showOpenOnly) stringResource(R.string.ShowAll) else stringResource(
                                R.string.ShowOpen
                            ),
                            style = button,
                        )
                    }
                }
            },
            bottomBar = {
                BottomBar(navController = navController)
            }
        ) {
            NavHost(navController, startDestination = items.first()) {
                composable("SlopeView") {
                    SlopeView(
                        database = FirebaseDatabase.getInstance().reference,
                        innerPadding = PaddingValues(top = 130.dp, bottom = 75.dp),
                        searchQuery = searchQuery,
                        showOpenOnly = showOpenOnly
                    )
                }
                composable("LiftView") {
                    LiftView(
                        database = FirebaseDatabase.getInstance().reference,
                        innerPadding = PaddingValues(top = 130.dp, bottom = 75.dp),
                        searchQuery = searchQuery,
                        showOpenOnly = showOpenOnly
                    )
                }
            }
        }
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
    val slopes = remember { mutableStateListOf<Slope>() }
    val context = LocalContext.current
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
                    .padding(8.dp)
                    .clickable {
                        val intent = Intent(context, DetailActivitySlope::class.java)
                        intent.putExtra("slope_name", slope.name)
                        intent.putExtra("slope_color", slope.color ?: "")
                        intent.putExtra("is_open", slope.status ?: false)
                        intent.putExtra("slope_id", slope.id)
                        context.startActivity(intent)
                    }
            ) {
                Image(
                    painter = painterResource(id = getSlopeImageResource(slope.color)),
                    contentDescription = "Slope",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = slope.name ?: "No name")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        Text(text = stringResource(id = R.string.Color) + " : ")
                        Canvas(modifier = Modifier.size(20.dp)) {
                            drawCircle(color = color)
                        }
                    }
                }
                Text(text = slope.status?.let {
                    Text(text = stringResource(id = R.string.Status) + " : ")
                    Spacer(modifier = Modifier.width(8.dp))
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
                lifts.clear()
                for (liftSnapshot in dataSnapshot.children) {
                    val lift = liftSnapshot.getValue(Lift::class.java)
                    if (lift != null) {
                        lifts.add(lift)
                    }
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
                    .padding(8.dp)
                    .clickable {
                        val intent = Intent(context, DetailActivitySlope::class.java)
                        intent.putExtra("item_type", "lift")
                        intent.putExtra("lift_name", lift.name)
                        intent.putExtra("lift_type", lift.type)
                        intent.putExtra("lift_is_open", lift.status ?: false)
                        context.startActivity(intent)
                    }) {

                Image(
                    painter = painterResource(id = R.drawable.ski_lift),
                    contentDescription = "Lift",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = lift.name ?: stringResource(id = R.string.UnknownStatus))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(id = R.string.LiftType) + " : " + (lift.type
                            ?: stringResource(id = R.string.UnknownStatus))
                    )
                }
                Text(text = lift.status?.let {
                    Text(text = stringResource(id = R.string.Status) + " : ")
                    Spacer(modifier = Modifier.width(8.dp))
                    if (it) stringResource(id = R.string.OpenStatus)
                    else stringResource(id = R.string.CloseStatus)
                } ?: stringResource(id = R.string.UnknownStatus))
            }
            Divider(color = Color.Gray)
        }
    }
}
