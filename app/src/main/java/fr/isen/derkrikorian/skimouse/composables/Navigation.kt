package fr.isen.derkrikorian.skimouse.composables

import android.annotation.SuppressLint
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.google.firebase.database.FirebaseDatabase
import fr.isen.derkrikorian.skimouse.ItineraryActivity
import fr.isen.derkrikorian.skimouse.LiftView
import fr.isen.derkrikorian.skimouse.LiveChatActivity
import fr.isen.derkrikorian.skimouse.MainActivity
import fr.isen.derkrikorian.skimouse.R
import fr.isen.derkrikorian.skimouse.SlopeView
import fr.isen.derkrikorian.skimouse.ui.theme.button
import kotlinx.coroutines.launch

@Composable
fun BottomBar(navController: NavController) {
    val items = listOf("SlopeView", "LiftView")

    BottomNavigation(backgroundColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.arguments?.getString(MainActivity.KEY_ROUTE)

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
                                        val intent = Intent(context, LiveChatActivity::class.java)
                                        context.startActivity(intent)
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Send,
                                    contentDescription = "Live Chat",
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
                                        val intent = Intent(context, ItineraryActivity::class.java)
                                        context.startActivity(intent)
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