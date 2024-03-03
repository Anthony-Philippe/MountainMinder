package fr.isen.derkrikorian.skimouse.Network

data class Message(
    val username: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)