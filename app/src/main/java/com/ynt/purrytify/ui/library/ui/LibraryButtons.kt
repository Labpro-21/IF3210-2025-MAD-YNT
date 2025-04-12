package com.ynt.purrytify.ui.library.ui

import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import com.ynt.purrytify.R

@Composable
fun LibraryButtons(
    selectedChoiceIndex: Int,
    onChoiceSelected: (Int) -> Unit
){
    val choices = listOf("All", "Liked")

    SingleChoiceSegmentedButtonRow{
        choices.forEachIndexed { index, choice ->
            SegmentedButton(
                selected = selectedChoiceIndex == index,
                onClick = { onChoiceSelected(index) },
                colors = SegmentedButtonDefaults.colors(
                    activeBorderColor = Color.Transparent,
                    activeContainerColor = colorResource(R.color.green),
                    activeContentColor = Color.Black,
                    inactiveContentColor = Color.White,
                    inactiveBorderColor = Color.Transparent,
                    inactiveContainerColor = colorResource(R.color.dark_gray)
                ),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = choices.count(),

                    ),
                icon = {}
            ) {
                Text(
                    text = choice,
                    fontSize = 14.sp
                )
            }
        }
    }
}
