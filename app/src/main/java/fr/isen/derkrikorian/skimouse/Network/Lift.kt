package fr.isen.derkrikorian.skimouse.Network

data class Lift(
    var connectedSlope: List<String>? = null,
    var name: String? = "",
    var status: Boolean? = false,
    var type: String? = "",
    var id : Int = 0,
)