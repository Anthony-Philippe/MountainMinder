package fr.isen.derkrikorian.skimouse.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.derkrikorian.skimouse.R

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelId: Int,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    offset: Boolean = false,
    searchbar: Boolean = false,
    emailKeyBoard: Boolean = false,
    hiddenPassword: Boolean = false,
    leadingIcon: ImageVector? = null,
) {
    Box() {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = stringResource(id = labelId),
                    modifier = if (offset) Modifier.offset(y = (-8).dp) else Modifier,
                    style = TextStyle(fontSize = 16.sp)
                )
            },
            modifier = modifier.align(Alignment.Center),
            shape = shape,
            visualTransformation = if (hiddenPassword) PasswordVisualTransformation() else VisualTransformation.None,
            leadingIcon = {
                leadingIcon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            },
            trailingIcon = {
                if (searchbar) {
                    Image(
                        painter = painterResource(id = R.drawable.recherche),
                        contentDescription = "Searchbar",
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            keyboardOptions = if (emailKeyBoard) KeyboardOptions(keyboardType = KeyboardType.Email) else KeyboardOptions.Default,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = colorResource(id = R.color.grey),
                unfocusedBorderColor = colorResource(id = R.color.orange),
                unfocusedLabelColor = colorResource(id = R.color.grey),
                unfocusedLeadingIconColor = colorResource(id = R.color.orange),
                focusedBorderColor = colorResource(id = R.color.orange),
                unfocusedContainerColor = colorResource(id = R.color.orange).copy(alpha = 0.2f),
            ),
        )
    }
}