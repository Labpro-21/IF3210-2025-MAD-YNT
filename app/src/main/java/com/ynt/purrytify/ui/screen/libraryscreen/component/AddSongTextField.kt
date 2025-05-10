package com.ynt.purrytify.ui.screen.libraryscreen.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.ynt.purrytify.R

@Composable
fun AddSongTextField(inputText: MutableState<String>, labelText:String, isLast:Boolean){
    var insertImeAction = ImeAction.Next
    if(isLast) insertImeAction = ImeAction.Done
    OutlinedTextField(
        singleLine = true,
        value = inputText.value,
        onValueChange = { inputText.value = it },
        label = { Text(text = labelText) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = colorResource(R.color.dark_gray),
            unfocusedContainerColor = colorResource(R.color.dark_gray),
            disabledContainerColor = colorResource(R.color.dark_gray),
            focusedLabelColor = colorResource(R.color.green),
            unfocusedLabelColor = colorResource(R.color.medium_dark_gray),
            focusedIndicatorColor = colorResource(R.color.green),
            unfocusedIndicatorColor = colorResource(R.color.medium_dark_gray),
            focusedTextColor = colorResource(R.color.white),
            unfocusedTextColor = colorResource(R.color.medium_dark_gray),
            cursorColor = colorResource(R.color.green)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        keyboardOptions = KeyboardOptions(
            imeAction = insertImeAction
        )
    )
}
