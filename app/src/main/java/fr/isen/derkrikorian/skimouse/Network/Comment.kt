package fr.isen.derkrikorian.skimouse.Network

data class Comment(
    val userId: String = "",
    val userName: String = "",
    val comment: String = "",
    val timestamp: Long = 0,
    val rating: Int = 0,
    val slopeName: String = "",
    val liftName: String = ""
)