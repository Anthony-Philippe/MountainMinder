package fr.isen.derkrikorian.skimouse

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.derkrikorian.skimouse.Network.Comment
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme

val chatMessagesRef = database.getReference("messages")

class LiveChatActivity : ComponentActivity() {
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
                        LiveChatView("Android")
                    }
                }
            }
        }
    }
}

@Composable
fun LiveChatView(name: String, modifier: Modifier = Modifier) {
    /*val messages = listOf(
        "C'est une excellente nouvelle !" to "Anonyme",
        "Je suis vraiment impressionné." to "Moi",
        "Très bien, je suis d'accord." to "Anonyme",
        "Wow, c'est incroyable !" to "Anonyme",
        "Intéressant, merci pour l'info." to "Anonyme",
        "OK, je vais le prendre en compte." to "Moi",
        "Je ne sais pas quoi dire." to "Anonyme",
        "C'est juste ce qu'il me faut." to "Anonyme",
        "Oh, vraiment ? C'est surprenant !" to "Anonyme",
        "Je vais y réfléchir sérieusement." to "Moi",
        "C'est une belle journée aujourd'hui." to "Anonyme",
        "Je pense que nous devrions continuer." to "Anonyme"
    )*/

    val comments = remember { mutableStateListOf<Comment>() }
    chatMessagesRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newComments = mutableListOf<Comment>()
            for (childSnapshot in snapshot.children) {
                val comment = childSnapshot.getValue(Comment::class.java)
                comment?.let {
                    newComments.add(it)
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

    val currentUser = FirebaseAuth.getInstance().currentUser
    val username = extractUsername(currentUser?.email ?: "")
    fun writeComment(comment: Comment) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val username = extractUsername(it.email ?: "")
            val commentWithUsername = comment.copy(userName = username)
            chatMessagesRef.push().setValue(commentWithUsername)
        }
    }

    var userComment by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = false,
        ) {
            item{
                comments.forEach { comment ->
                    val isUserMessage = comment.userName == username

                    Box(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                            .padding(start = if (isUserMessage) 80.dp else 0.dp)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 48f,
                                    topEnd = 48f,
                                    bottomStart = if (isUserMessage) 48f else 0f,
                                    bottomEnd = if (isUserMessage) 0f else 48f
                                )
                            )
                            .background(
                                color = (if (isUserMessage) colorResource(id = R.color.orange) else Color.Gray).copy(
                                    alpha = if (isUserMessage) 0.4f else 0.2f
                                )
                            )
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = userComment,
                onValueChange = { userComment = it },
                label = { Text("Ecrire un message") },
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
                    focusedTrailingIconColor = colorResource(id = R.color.orange),
                    unfocusedContainerColor = colorResource(id = R.color.grey).copy(alpha = 0.2f),
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val newComment = Comment(
                                userName = "userName",
                                comment = userComment,
                                timestamp = System.currentTimeMillis()
                            )
                            writeComment(newComment)
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview3() {
    SkiMouseTheme {
        LiveChatView("Android")
    }
}