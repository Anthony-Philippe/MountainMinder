package fr.isen.derkrikorian.skimouse.Network

import com.google.firebase.Firebase
import com.google.firebase.database.database

class NetworkConstants {
    companion object {
        val COMMENTS_DB = Firebase.database.getReference("comments")
        val LIVECHAT_DB = Firebase.database.getReference("messages")
        val USERS_DB = Firebase.database.getReference("users")
    }
}