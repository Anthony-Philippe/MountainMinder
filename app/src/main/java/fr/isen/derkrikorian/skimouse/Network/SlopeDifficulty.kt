package fr.isen.derkrikorian.skimouse.Network

import androidx.compose.ui.graphics.Color
import fr.isen.derkrikorian.skimouse.R

enum class SlopeDifficulty(val color: Color, val value: Int) {
    GREEN(Color.Green, 1),
    BLUE(Color.Blue, 2),
    RED(Color.Red, 3),
    BLACK(Color.Black, 4),
    UNKNOWN(Color.Gray, 5);

    companion object {
        fun fromString(difficulty: String): SlopeDifficulty {
            return when (difficulty.uppercase()) {
                "BLACK" -> BLACK
                "BLUE" -> BLUE
                "RED" -> RED
                "GREEN" -> GREEN
                else -> UNKNOWN
            }
        }

        fun getSlopeImageResource(slopeColor: String?): Int {
            return when (slopeColor) {
                "green" -> R.drawable.ski_stickman_g
                "blue" -> R.drawable.ski_stickman_b
                "red" -> R.drawable.ski_stickman_r
                "black" -> R.drawable.ski_stickman_black
                else -> R.drawable.ski_stickman_black
            }
        }
        fun getRabbitImageResource(slopeColor: String?): Int {
            return when (slopeColor) {
                "#00ff00" -> R.drawable.lapingreen
                "#0000ff" -> R.drawable.lapinblue
                "#ff0000" -> R.drawable.lapinred
                "#000000" -> R.drawable.lapinblack
                else -> R.drawable.lapinblack
            }
        }
    }
}