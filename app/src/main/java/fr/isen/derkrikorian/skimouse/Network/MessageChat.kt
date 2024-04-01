package fr.isen.derkrikorian.skimouse.Network

data class MessageChat(
    val userName: String = "",
    val comment: String = "",
    val timestamp: Long = 0,
)