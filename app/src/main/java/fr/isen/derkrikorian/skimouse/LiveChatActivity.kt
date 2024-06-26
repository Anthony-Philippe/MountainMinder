package fr.isen.derkrikorian.skimouse

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import fr.isen.derkrikorian.skimouse.Network.MessageChat
import fr.isen.derkrikorian.skimouse.Network.NetworkConstants
import fr.isen.derkrikorian.skimouse.Network.User
import fr.isen.derkrikorian.skimouse.composables.Navbar
import fr.isen.derkrikorian.skimouse.ui.theme.SkiMouseTheme

val chatMessagesRef = NetworkConstants.LIVECHAT_DB

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
class LiveChatActivity : ComponentActivity() {
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
                            Navbar()
                        }
                    ) {
                        LiveChatView()
                    }
                }
            }
        }
    }
}

@Composable
fun LiveChatView(modifier: Modifier = Modifier) {
    val chats = remember { mutableStateListOf<MessageChat>() }
    var userChat by remember { mutableStateOf("") }
    chatMessagesRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newChat = mutableListOf<MessageChat>()
            for (childSnapshot in snapshot.children) {
                val chat = childSnapshot.getValue(MessageChat::class.java)
                chat?.let {
                    newChat.add(it)
                }
            }
            chats.clear()
            chats.addAll(newChat)
        }

        override fun onCancelled(error: DatabaseError) {
            // Handle error
        }
    })

    val currentUser = FirebaseAuth.getInstance().currentUser
    fun writeChat(chat: MessageChat) {
        currentUser?.let {
            val userId = it.uid
            val messagesRef = chatMessagesRef
            val usersRef = NetworkConstants.USERS_DB

            usersRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userData = dataSnapshot.getValue(User::class.java)
                    val username = userData?.username ?: ""
                    val commentWithUsername = chat.copy(userName = username)
                    messagesRef.push().setValue(commentWithUsername)
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 75.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = true,
        ) {
            item {
                chats.asReversed().forEach { chat ->
                    val isUserMessage = chat.userName == "Anthony83"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUserMessage) {
                            Arrangement.End
                        } else {
                            Arrangement.Start
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(start = 20.dp, bottom = 10.dp, end = 20.dp)
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
                                        text = "${chat.userName}",
                                        fontSize = 16.sp
                                    )
                                }
                                Text(
                                    text = chat.comment,
                                    fontSize = 15.sp,
                                )
                            }
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
                value = userChat,
                onValueChange = { userChat = it },
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
                            val newChat = MessageChat(
                                userName = "userName",
                                comment = userChat,
                                timestamp = System.currentTimeMillis()
                            )
                            writeChat(newChat)
                            userChat = ""
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