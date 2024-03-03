package fr.isen.derkrikorian.skimouse

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme

val database = Firebase.database
val commentsRef = database.getReference("comments")

class DetailActivitySlope : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //slope
        val slopeName = intent.getStringExtra("slope_name") ?: ""
        val slopeColorString = intent.getStringExtra("slope_color") ?: ""
        val isOpen = intent.getBooleanExtra("is_open", false)
        val id = intent.getIntExtra("slope_id", 0)

        //Lift
        val itemType = intent.getStringExtra("item_type")
        val liftName = intent.getStringExtra("lift_name") ?: ""
        val liftType = intent.getStringExtra("lift_status") ?: ""
        val liftisOpen = intent.getBooleanExtra("lift_is_open", false)

        val slopeColor = if (slopeColorString.isNotEmpty()) {
            parseColor(slopeColorString)
        } else {
            Color.Transparent
        }

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
                        if (itemType == "lift") {
                            LiftDetails(
                                name = liftName,
                                type = liftType,
                                liftisOpen = liftisOpen
                            )
                        } else {
                            SlopeDetails(
                                name = slopeName,
                                color = slopeColor,
                                isOpen = isOpen,
                                id = id
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar() {
    val logo: Painter = painterResource(id = R.drawable.logo)
    TopAppBar(
        title = { },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
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
        }
    )
}

fun parseColor(colorString: String): Color {
    return Color(android.graphics.Color.parseColor(colorString))
}

@Composable
fun SlopeDetails(
    name: String,
    color: Color,
    isOpen: Boolean,
    modifier: Modifier = Modifier,
    id: Int
) {
    var commentaire by remember { mutableStateOf("") }
    val colorHex = "#${Integer.toHexString(color.toArgb()).substring(2)}"
    var note: Int by remember { mutableIntStateOf(0) }
    val slopesReference = FirebaseDatabase.getInstance().getReference("slopes")
    val slopeReference = slopesReference.child(id.toString())
    val context = LocalContext.current
    var openState by remember { mutableStateOf(isOpen) }
    var open = ""
    if (isOpen == true) {
        open = "Ouverte"
    } else {
        open = "Fermée"
    }

    val comments = remember { mutableStateListOf<Comment>() }
    commentsRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newComments = mutableListOf<Comment>()
            for (childSnapshot in snapshot.children) {
                val comment = childSnapshot.getValue(Comment::class.java)
                comment?.let {
                    if (it.slopeName == name) {
                        newComments.add(it)
                    }
                }
            }
            comments.clear()
            comments.addAll(newComments)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })
    fun extractUsername(email: String): String {
        val atIndex = email.indexOf('@')
        return if (atIndex != -1) {
            email.substring(0, atIndex)
        } else {
            email
        }
    }

    fun writeComment(comment: Comment, slopeName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val username = extractUsername(it.email ?: "")
            val commentWithUsername = comment.copy(userName = username, slopeName = slopeName)
            commentsRef.push().setValue(commentWithUsername)
        }
    }

    var userComment by remember { mutableStateOf("") }
    var rating by remember { mutableIntStateOf(0) }

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
                            val newComment = Comment(
                                userId = "userId",
                                userName = "userName",
                                comment = userComment,
                                timestamp = System.currentTimeMillis(),
                                rating = rating
                            )
                            writeComment(newComment, name)
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

            if (comments.isEmpty()) {
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
                comments.forEach { comment ->
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
    type: String,
    liftisOpen: Boolean,
    modifier: Modifier = Modifier
) {
    var commentaire by remember { mutableStateOf("") }
    var note: Int by remember { mutableStateOf(0) }
    var open = if (liftisOpen) "Ouverte" else "Fermée"
    val comments = remember { mutableStateListOf<Comment>() }
    val context = LocalContext.current
    var openState by remember { mutableStateOf(liftisOpen) }

    commentsRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newComments = mutableListOf<Comment>()
            for (childSnapshot in snapshot.children) {
                val comment = childSnapshot.getValue(Comment::class.java)
                comment?.let {
                    if (it.liftName == name) {
                        newComments.add(it)
                    }
                }
            }
            comments.clear()
            comments.addAll(newComments)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })

    fun extractUsername(email: String): String {
        val atIndex = email.indexOf('@')
        return if (atIndex != -1) {
            email.substring(0, atIndex)
        } else {
            email
        }
    }

    fun writeComment(comment: Comment, liftName: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val username = extractUsername(it.email ?: "")
            val commentWithUsername = comment.copy(userName = username, liftName = liftName)
            commentsRef.push().setValue(commentWithUsername)
        }
    }

    var userComment by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(0) }

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
                            text = "→",
                            fontSize = 24.sp
                        )
                        Text(
                            text = type,
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
                    val pistes = listOf(
                        "La pistache",
                        "Le floriant",
                        "Capibara",
                        "Anna",
                        "Dandelot",
                        "Barabara",
                    )
                    items(pistes) { piste ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(color = colorResource(id = R.color.orange).copy(alpha = 0.5f)),
                        ) {
                            Text(
                                text = piste,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(4.dp),
                                textAlign = TextAlign.Center
                            )
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
                            val newComment = Comment(
                                userId = "userId",
                                userName = "userName",
                                comment = commentaire,
                                timestamp = System.currentTimeMillis(),
                                rating = rating
                            )
                            writeComment(newComment, name)
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

            if (comments.isEmpty()) {
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
                comments.forEach { comment ->
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