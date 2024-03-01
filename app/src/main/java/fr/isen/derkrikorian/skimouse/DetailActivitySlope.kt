package fr.isen.derkrikorian.skimouse

import android.media.Image
import androidx.compose.ui.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme
import androidx.compose.ui.text.style.TextAlign
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

val database = Firebase.database
val commentsRef = database.getReference("comments")

class DetailActivitySlope : ComponentActivity() {
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
            Color.Transparent // Ou une autre couleur par défaut
        }

        setContent {
            SkiMouseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if(itemType == "lift") {
                        LiftDetails(
                            name = liftName,
                            type = liftType,
                            liftisOpen = liftisOpen)
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
fun parseColor(colorString: String): Color {
    return Color(android.graphics.Color.parseColor(colorString))
}
@Composable
fun SlopeDetails(name: String, color: Color, isOpen: Boolean, modifier: Modifier = Modifier, id : Int) {
    var commentaire by remember { mutableStateOf("") }
    val colorHex = "#${Integer.toHexString(color.toArgb()).substring(2)}"
    var note: Int by remember { mutableStateOf(0) }
    val slopesReference = FirebaseDatabase.getInstance().getReference("slopes")
    val slopeReference = slopesReference.child(id.toString())
    val context = LocalContext.current
    var openState by remember { mutableStateOf(isOpen) }
    var open = ""
    if(isOpen == true) {
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
                    if (it.slopeName == name) { // Filtrer les commentaires par nom de piste
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
    var rating by remember { mutableStateOf(0) }


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
                        text = "Etat: ${if (openState) "Ouverte" else "Fermée"}",
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
                text = "Est-ce que la piste : $name est toujours ${if (openState) "Ouverte" else "Fermée"} ?",
                fontSize = 25.sp,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center,

                )
            Row( modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    slopeReference.child("status").setValue(true)
                    Toast.makeText(context, "Merci pour cette information", Toast.LENGTH_SHORT).show()
                    openState = true
                }, enabled = !openState) {
                    Text(text = "Ouverte")
                }
                Button(onClick = {
                    slopeReference.child("status").setValue(false)
                    Toast.makeText(context, "Merci pour cette information", Toast.LENGTH_SHORT).show()
                    openState = false
                }, enabled = openState) {
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
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < rating) Color.Blue else Color.LightGray,
                        modifier = Modifier
                            .clickable {
                                rating = index + 1
                            }
                            .padding(4.dp)
                            .size(40.dp)
                    )
                }

            }
            OutlinedTextField(
                value = userComment,
                onValueChange = { userComment = it },
                label = { Text("Votre commentaire") },
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
            Button(
                onClick = {
                    // Écrire le commentaire dans la base de données avec le nom de la piste
                    val newComment = Comment(
                        userId = "userId",
                        userName = "userName",
                        comment = userComment,
                        timestamp = System.currentTimeMillis(),
                        rating = rating
                    )
                    writeComment(newComment, name) // Inclure le nom de la piste
                    // Effacer le champ de commentaire après l'envoi
                    userComment = ""
                }
            ) {
                Text("Envoyer")
            }
            comments.forEach { comment ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(10.dp)
                        .border(
                            1.dp,
                            colorResource(id = R.color.grey),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(color = colorResource(id = R.color.grey).copy(alpha = 0.2f)),

                    ) {
                    Column {
                        // Afficher le nom de l'utilisateur
                        Text(
                            text = "${comment.userName} - ${comment.rating} étoiles",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(4.dp),
                            textAlign = TextAlign.Center
                        )
                        // Afficher le commentaire
                        Text(
                            text = comment.comment,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                }
            }


        }
    }

}


@Composable
fun LiftDetails(name: String, type: String, liftisOpen: Boolean, modifier: Modifier = Modifier) {
    var commentaire by remember { mutableStateOf("") }
    var note: Int by remember { mutableStateOf(0) }
    var open = if (liftisOpen) "Ouverte" else "Fermée"

    val comments = remember { mutableStateListOf<Comment>() }

    commentsRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newComments = mutableListOf<Comment>()
            for (childSnapshot in snapshot.children) {
                val comment = childSnapshot.getValue(Comment::class.java)
                comment?.let {
                    if (it.liftName == name) { // Filtrer les commentaires par nom de remontée
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
                    Text(
                        text = "Type : $type ",
                        fontSize = 25.sp
                    )
                    Text(
                        text = "Etat: $open",
                        fontSize = 25.sp
                    )
                }
                Row(
                    modifier = Modifier.padding(end = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Image(
                        painter = painterResource(id = R.drawable.liftrabbit),
                        contentDescription = "Rabbit Image",
                        modifier = Modifier.size(175.dp)
                    )
                }

            }
        }

        item {
            Text(
                text = "Est-ce que la remontée : $name est toujours $open ?",
                fontSize = 25.sp,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
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
                text = "Afficher la liste des pistes",
                fontSize = 25.sp,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
        }

        item {
            Text(
                text = "Notez la remontée",
                fontSize = 25.sp,
                modifier = Modifier.padding(8.dp),
                textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < rating) Color.Blue else Color.LightGray,
                        modifier = Modifier
                            .clickable {
                                rating = index + 1
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
                    unfocusedTextColor = colorResource(id = R.color.grey),
                    unfocusedBorderColor = colorResource(id = R.color.grey),
                    unfocusedLabelColor = colorResource(id = R.color.grey),
                    unfocusedLeadingIconColor = colorResource(id = R.color.grey),
                    focusedBorderColor = colorResource(id = R.color.grey),
                    unfocusedContainerColor = colorResource(id = R.color.grey).copy(alpha = 0.2f),
                ),
            )

            Button(
                onClick = {
                    // Écrire le commentaire dans la base de données avec le nom de la remontée
                    val newComment = Comment(
                        userId = "userId",
                        userName = "userName",
                        comment = commentaire,
                        timestamp = System.currentTimeMillis(),
                        rating = rating
                    )
                    writeComment(newComment, name) // Inclure le nom de la remontée
                    // Effacer le champ de commentaire après l'envoi
                    commentaire = ""
                }
            ) {
                Text("Envoyer")
            }

            comments.forEach { comment ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(10.dp)
                        .border(
                            1.dp,
                            colorResource(id = R.color.grey),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clip(RoundedCornerShape(20.dp))
                        .background(color = colorResource(id = R.color.grey).copy(alpha = 0.2f)),
                ) {
                    Column {
                        // Afficher le nom de l'utilisateur
                        Text(
                            text = "${comment.userName} - ${comment.rating} étoiles",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(4.dp),
                            textAlign = TextAlign.Center
                        )
                        // Afficher le commentaire
                        Text(
                            text = comment.comment,
                            fontSize = 15.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
