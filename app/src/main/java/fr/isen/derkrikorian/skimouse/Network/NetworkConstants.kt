package fr.isen.derkrikorian.skimouse.Network

import com.google.firebase.Firebase
import com.google.firebase.database.database

class NetworkConstants {
    companion object {
        val COMMENTS_DB = Firebase.database.getReference("comments")
        val LIVECHAT_DB = Firebase.database.getReference("messages_chat")
        val USERS_DB = Firebase.database.getReference("users")
        val SLOPES_DB = Firebase.database.getReference("slopes")
        val LIFTS_DB = Firebase.database.getReference("lifts")
    }
}