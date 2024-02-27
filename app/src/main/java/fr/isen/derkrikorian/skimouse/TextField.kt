package fr.isen.derkrikorian.skimouse

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    labelId: Int,
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    offset: Boolean = false,
    searchbar: Boolean = false
) {
    Box() {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = stringResource(id = labelId),
                    modifier = if (offset) Modifier.offset(y = (-8).dp) else Modifier
                )
            },
            modifier = modifier
                .align(Alignment.Center),
            shape = shape,
            trailingIcon = {
                if (searchbar) {
                    Image(
                        painter = painterResource(id = R.drawable.recherche),
                        contentDescription = "Searchbar",
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = colorResource(id =R.color.grey),
                unfocusedBorderColor =colorResource(id =R.color.orange),
                unfocusedLabelColor = colorResource(id =R.color.grey),
                unfocusedLeadingIconColor = colorResource(id =R.color.orange),
                focusedBorderColor = colorResource(id =R.color.orange),
                unfocusedContainerColor = colorResource(id =R.color.orange).copy(alpha = 0.2f),
            )
        )
    }
}