package fr.isen.derkrikorian.skimouse

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Send
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TopBar()
                }
            }
        }

        database = FirebaseDatabase.getInstance().reference

        writeTest()
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

    BottomNavigation(backgroundColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

        items.forEachIndexed { index, screen ->
            BottomNavigationItem(
                icon = {
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
    val navController = rememberNavController()
    val items = listOf("SlopeView", "LiftView")
    var searchQuery by remember { mutableStateOf("") }
    var showOpenOnly by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                content = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = { scope.launch { drawerState.close() } },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "Profile Icon",
                                tint = Color.White
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .clickable {
                                        val intent = Intent(context, ChatActivity::class.java)
                                        context.startActivity(intent)
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Send,
                                    contentDescription = "Leave Icon",
                                    tint = Color.White
                                )
                                Text(
                                    "Live Chat",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .clickable {
                                        Toast
                                            .makeText(context, "Itinéraires", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.LocationOn,
                                    contentDescription = "Leave Icon",
                                    tint = Color.White
                                )
                                Text(
                                    "Itinéraires",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(vertical = 10.dp)
                                    .clickable {
                                        Toast
                                            .makeText(context, "Logout", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.ExitToApp,
                                    contentDescription = "Leave Icon",
                                    tint = Color.White
                                )
                                Text(
                                    "Logout",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxSize(fraction = 0.45f),
                drawerContainerColor = colorResource(id = R.color.orange)
            )
        },
        drawerState = drawerState,
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }) {
                            Icon(
                                Icons.Outlined.Menu,
                                contentDescription = "Profile Icon",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        CustomOutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            labelId = R.string.SearchPlaceholder,
                            leadingIcon = Icons.Outlined.Search,
                            modifier = Modifier
                                .height(40.dp)
                                .padding(bottom = 8.dp),
                            showPlaceholder = false,
                            isSearchBar = true
                        )
                        Image(
                            painter = logo,
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(55.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                    Button(
                        onClick = { showOpenOnly = !showOpenOnly },
                        modifier = Modifier
                            .height(30.dp)
                            .padding(start = 10.dp, end = 10.dp)
                            .fillMaxWidth()
                            .widthIn(min = 100.dp),
                        colors = ButtonDefaults.buttonColors(colorResource(id = R.color.orange))
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
                        innerPadding = PaddingValues(top = 100.dp, bottom = 75.dp),
                        searchQuery = searchQuery,
                        showOpenOnly = showOpenOnly
                    )
                }
                composable("LiftView") {
                    LiftView(
                        database = FirebaseDatabase.getInstance().reference,
                        innerPadding = PaddingValues(top = 100.dp, bottom = 75.dp),
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
                    .padding(horizontal = 35.dp, vertical = 13.dp)
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
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp)) {
                    Text(
                        text = slope.name?.uppercase() ?: "No name",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(id = R.string.Color),
                            modifier = Modifier.padding(end = 8.dp),
                            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        )
                        Canvas(modifier = Modifier.size(12.dp)) {
                            drawCircle(color = color)
                        }
                    }
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