package com.ynt.purrytify.ui.screen.libraryscreen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ynt.purrytify.R

@Composable
fun LibraryTopBar(
    title: String,
    onAddClick: () -> Unit,
    onChoiceSelected: (Int) -> Unit,
    selectedChoiceIndex: Int,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(
                top = 25.dp,
                start = 18.dp,
                end = 18.dp,
                bottom = 12.dp,
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
            )
            IconButton(
                onClick = onAddClick
                ,
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Upload Song",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            LibraryButtons(
                selectedChoiceIndex = selectedChoiceIndex,
                onChoiceSelected = onChoiceSelected
            )
            AudioRoutesButton(navController = navController)
        }


    }
    HorizontalDivider(
        thickness = 2.dp,
        color = colorResource(R.color.dark_gray),
        modifier = Modifier
            .padding(top = 145.dp)
    )

}
