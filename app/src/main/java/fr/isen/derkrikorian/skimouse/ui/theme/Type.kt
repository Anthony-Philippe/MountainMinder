package fr.isen.derkrikorian.skimouse.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fr.isen.derkrikorian.skimouse.R

val button = TextStyle(
    fontFamily = FontFamily(Font(R.font.nexa_heavy)),
    fontWeight = FontWeight.W500,
    fontSize = 12.sp,
    letterSpacing = 1.25.sp
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.nexa_heavy)),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)