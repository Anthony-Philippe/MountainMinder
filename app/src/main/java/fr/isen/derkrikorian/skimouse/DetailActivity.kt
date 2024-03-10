package fr.isen.derkrikorian.skimouse

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.derkrikorian.skimouse.Network.Message
import fr.isen.derkrikorian.skimouse.Network.NetworkConstants
import fr.isen.derkrikorian.skimouse.Network.SlopeDifficulty
import fr.isen.derkrikorian.skimouse.composables.Navbar
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme
import java.util.Locale

val commentsRef = NetworkConstants.COMMENTS_DB

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent

        setContent {
            SkiMouseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            Navbar()
                        }
                    ) {
                        DetailView(intent = intent)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailView(intent: Intent) {
    val itemType = intent.getStringExtra("item_Type") ?: ""

    // Slope details
    val slopeName = intent.getStringExtra("slope_name") ?: ""
    val slopeColorString = intent.getStringExtra("slope_color") ?: ""
    val slopeStatus = intent.getBooleanExtra("slope_Status", false)
    val slopeId = intent.getIntExtra("slope_id", 0)

    // Lift details
    val liftName = intent.getStringExtra("lift_Name") ?: ""
    val connectedSlopeList: ArrayList<String>? = intent.getStringArrayListExtra("connected_Slope")
    val liftStatus = intent.getBooleanExtra("lift_Status", false)
    val liftType = intent.getStringExtra("lift_Type") ?: ""
    val liftId = intent.getIntExtra("lift_id", 0)

    val slopeColor = if (slopeColorString.isNotEmpty()) {
        parseColor(slopeColorString)
    } else {
        Color.Transparent
    }

    if (itemType == "lift") {
        LiftDetails(
            name = liftName,
            connectedSlopeList = connectedSlopeList,
            type = liftType,
            liftIsOpen = liftStatus,
            id = liftId
        )
    } else if (itemType == "slope") {
        SlopeDetails(
            name = slopeName,
            color = slopeColor,
            isOpen = slopeStatus,
            id = slopeId
        )
    }
}

@Composable
fun SlopeDetails(
    name: String,
    color: Color,
    isOpen: Boolean,
    modifier: Modifier = Modifier,
    id: Int
) {
    val colorHex = "#${Integer.toHexString(color.toArgb()).substring(2)}"
    val slopesReference = FirebaseDatabase.getInstance().getReference("slopes")
    val slopeReference = slopesReference.child(id.toString())
    val context = LocalContext.current
    var openState by remember { mutableStateOf(isOpen) }

    var userComment by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }

    val messages = remember { mutableStateListOf<Message>() }
    commentsRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newMessages = mutableListOf<Message>()
            for (childSnapshot in snapshot.children) {
                val message = childSnapshot.getValue(Message::class.java)
                message?.let {
                    if (it.slopeName == name) {
                        newMessages.add(it)
                    }
                }
            }
            messages.clear()
            messages.addAll(newMessages)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })

    fun writeComment(message: Message, slopeName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val username = extractUsername(it.email ?: "")
            val commentWithUsername = message.copy(userName = username, slopeName = slopeName)
            commentsRef.push().setValue(commentWithUsername)
        }
    }

    LazyColumn(
        modifier = modifier.padding(top = 75.dp),
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
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Niveau",
                            fontSize = 24.sp
                        )
                        Canvas(
                            modifier = Modifier
                                .size(35.dp)
                                .padding(start = 10.dp)
                        ) {
                            drawCircle(color = color)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (openState) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .padding(end = 10.dp),
                        )
                        Text(
                            text = "${if (openState) "Ouvert" else "Fermé"}",
                            fontSize = 22.sp
                        )
                    }
                }
                Image(
                    painter = painterResource(
                        id = SlopeDifficulty.getRabbitImageResource(
                            colorHex
                        )
                    ),
                    contentDescription = "Rabbit Image",
                    modifier = Modifier.size(125.dp)
                )
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 50.dp)
            ) {
                Text(
                    text = "Signaler un changement d'état de la piste",
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = openState,
                    onCheckedChange = { isChecked ->
                        openState = isChecked
                        slopeReference.child("status").setValue(isChecked)
                        Toast.makeText(context, "Merci pour l'information", Toast.LENGTH_SHORT)
                            .show()
                    },
                    thumbContent = if (openState) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(id = R.color.orange),
                        checkedTrackColor = colorResource(id = R.color.orange).copy(alpha = 0.5f),
                    )
                )
            }
        }

        item {
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Note",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < rating) colorResource(id = R.color.orange) else Color.LightGray,
                            modifier = Modifier
                                .clickable {
                                    rating = index + 1
                                }
                                .padding(4.dp)
                                .size(25.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = userComment,
                onValueChange = { userComment = it },
                label = { Text("Votre commentaire") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = colorResource(id = R.color.grey),
                    unfocusedBorderColor = colorResource(id = R.color.grey),
                    unfocusedLabelColor = colorResource(id = R.color.grey),
                    unfocusedLeadingIconColor = colorResource(id = R.color.grey),
                    focusedBorderColor = colorResource(id = R.color.orange),
                    focusedLabelColor = colorResource(id = R.color.orange),
                    unfocusedContainerColor = colorResource(id = R.color.grey).copy(alpha = 0.2f),
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val newMessage = Message(
                                userId = "userId",
                                userName = "userName",
                                comment = userComment,
                                timestamp = System.currentTimeMillis(),
                                rating = rating
                            )
                            writeComment(newMessage, name)
                            userComment = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
            Divider(modifier = Modifier.padding(top = 10.dp))

            Text(
                text = "Avis sur la piste",
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 8.dp)
            )

            if (messages.isEmpty()) {
                Text(
                    text = "Aucun avis sur cette piste",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = colorResource(id = R.color.orange)
                )
            } else {
                messages.forEach { comment ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 48f,
                                    topEnd = 48f,
                                    bottomStart = 0f,
                                    bottomEnd = 48f
                                )
                            )
                            .background(color = colorResource(id = R.color.orange).copy(alpha = 0.25f)),
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${comment.userName}",
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                repeat(5) { index ->
                                    val starColor = if (index < comment.rating) {
                                        colorResource(id = R.color.orange)
                                    } else {
                                        Color.Gray
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = starColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = comment.comment,
                                fontSize = 15.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LiftDetails(
    name: String,
    connectedSlopeList: ArrayList<String>?,
    type: String,
    liftIsOpen: Boolean,
    modifier: Modifier = Modifier,
    id: Int
) {
    val liftsReference = FirebaseDatabase.getInstance().getReference("lifts")
    val liftReference = liftsReference.child(id.toString())
    val context = LocalContext.current
    var openState by remember { mutableStateOf(liftIsOpen) }

    var commentaire by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }

    val messages = remember { mutableStateListOf<Message>() }
    commentsRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newMessages = mutableListOf<Message>()
            for (childSnapshot in snapshot.children) {
                val message = childSnapshot.getValue(Message::class.java)
                message?.let {
                    if (it.liftName == name) {
                        newMessages.add(it)
                    }
                }
            }
            messages.clear()
            messages.addAll(newMessages)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })

    fun writeComment(message: Message, liftName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val username = extractUsername(it.email ?: "")
            val commentWithUsername = message.copy(userName = username, liftName = liftName)
            commentsRef.push().setValue(commentWithUsername)
        }
    }

    LazyColumn(
        modifier = modifier
            .padding(top = 75.dp)
            .fillMaxSize(),
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
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "→",
                            fontSize = 24.sp
                        )
                        Text(
                            text = type.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() },
                            fontSize = 25.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (openState) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .padding(end = 10.dp),
                        )
                        Text(
                            text = "${if (openState) "Ouvert" else "Fermé"}",
                            fontSize = 25.sp
                        )
                    }
                }
                Image(
                    painter = painterResource(id = R.drawable.liftrabbit),
                    contentDescription = "Rabbit Image",
                    modifier = Modifier.size(125.dp)
                )
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 50.dp)
            ) {
                Text(
                    text = "Signaler un changement d'état de la remontée",
                    fontSize = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = openState,
                    onCheckedChange = { isChecked ->
                        openState = isChecked
                        liftReference.child("status").setValue(isChecked)
                        Toast.makeText(context, "Merci pour l'information", Toast.LENGTH_SHORT)
                            .show()
                    },
                    thumbContent = if (openState) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else {
                        {
                            Icon(
                                imageVector = Icons.Filled.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(id = R.color.orange),
                        checkedTrackColor = colorResource(id = R.color.orange).copy(alpha = 0.5f),
                    )
                )
            }
        }

        item {
            if (connectedSlopeList.isNullOrEmpty()) {
                Text(
                    text = "Aucune piste desservie",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = colorResource(id = R.color.orange)
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Pistes desservies",
                        fontSize = 22.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                    LazyRow {
                        items(connectedSlopeList) { slope ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        color = colorResource(id = R.color.orange).copy(alpha = 0.5f)
                                    )
                                    .clickable {
                                        fetchSlopeDetails(slope, context)
                                    },
                            ) {
                                Text(
                                    text = slope,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(4.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Note",
                    fontSize = 20.sp,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < rating) colorResource(id = R.color.orange) else Color.LightGray,
                            modifier = Modifier
                                .clickable {
                                    rating = index + 1
                                }
                                .padding(4.dp)
                                .size(25.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = commentaire,
                onValueChange = { commentaire = it },
                label = { Text(text = stringResource(id = R.string.log_form4)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(85.dp)
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedTextColor = colorResource(id = R.color.grey),
                    unfocusedBorderColor = colorResource(id = R.color.grey),
                    unfocusedLabelColor = colorResource(id = R.color.grey),
                    unfocusedLeadingIconColor = colorResource(id = R.color.grey),
                    focusedBorderColor = colorResource(id = R.color.orange),
                    focusedLabelColor = colorResource(id = R.color.orange),
                    unfocusedContainerColor = colorResource(id = R.color.grey).copy(alpha = 0.2f),
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val newMessage = Message(
                                userId = "userId",
                                userName = "userName",
                                comment = commentaire,
                                timestamp = System.currentTimeMillis(),
                                rating = rating
                            )
                            writeComment(newMessage, name)
                            commentaire = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            )
            Divider(modifier = Modifier.padding(top = 10.dp))

            Text(
                text = "Avis sur la remontée",
                fontSize = 15.sp,
                modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 8.dp)
            )

            if (messages.isEmpty()) {
                Text(
                    text = "Aucun avis sur cette remontée",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    color = colorResource(id = R.color.orange)
                )
            } else {
                messages.forEach { comment ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 48f,
                                    topEnd = 48f,
                                    bottomStart = 0f,
                                    bottomEnd = 48f
                                )
                            )
                            .background(color = colorResource(id = R.color.orange).copy(alpha = 0.25f)),
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${comment.userName}",
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                repeat(5) { index ->
                                    val starColor = if (index < comment.rating) {
                                        colorResource(id = R.color.orange)
                                    } else {
                                        Color.Gray
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = starColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = comment.comment,
                                fontSize = 15.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}

fun fetchSlopeDetails(slopeName: String, context: Context) {
    val slopesReference = FirebaseDatabase.getInstance().getReference("slopes")

    slopesReference.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            var slopeId = ""
            for (slopeSnapshot in dataSnapshot.children) {
                val name = slopeSnapshot.child("name").getValue(String::class.java) ?: ""
                if (name == slopeName) {
                    slopeId = slopeSnapshot.key ?: ""
                    break
                }
            }

            if (slopeId.isNotEmpty()) {
                val slopeReference = slopesReference.child(slopeId)
                slopeReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(slopeDataSnapshot: DataSnapshot) {
                        val slopeColorString =
                            slopeDataSnapshot.child("color").getValue(String::class.java) ?: ""
                        val slopeStatus =
                            slopeDataSnapshot.child("status").getValue(Boolean::class.java) ?: false

                        val intent = Intent(context, DetailActivity::class.java)
                        intent.putExtra("slope_name", slopeName)
                        intent.putExtra("slope_color", slopeColorString)
                        intent.putExtra("slope_Status", slopeStatus)
                        intent.putExtra("slope_id", slopeId.toInt())
                        intent.putExtra("item_Type", "slope")
                        context.startActivity(intent)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle error
                    }
                })
            } else {
                Toast.makeText(context, "Piste non trouvée", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle error
        }
    })
}

fun parseColor(colorString: String): Color {
    return Color(android.graphics.Color.parseColor(colorString))
}