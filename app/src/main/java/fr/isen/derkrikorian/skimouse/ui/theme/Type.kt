package fr.isen.derkrikorian.skimouse.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import fr.isen.derkrikorian.skimouse.R

val button = TextStyle(
    fontFamily = FontFamily(Font(R.font.spicy_rice)),
    fontWeight = FontWeight.W500,
    fontSize = 12.sp,
    letterSpacing = 1.25.sp
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily(Font(R.font.spicy_rice)),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    // Add other text styles here and replace `FontFamily.Default` with `FontFamily(Font(R.font.spicy_rice))`
)
    // Add other text styles here and replace `FontFamily.Default` with `FontFamily(Font(R.font.spicy_rice))`

    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */