package com.klewerro.mitemperaturenospyware.presentation.mainscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.klewerro.mitemperaturenospyware.ui.LocalSpacing

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddHeaterBottomSheet(
    modalBottomSheetState: ModalBottomSheetState,
    content: @Composable () -> Unit
) {
    val spacing = LocalSpacing.current
    ModalBottomSheetLayout(
        sheetShape = RoundedCornerShape(
            topStart = spacing.radiusNormal,
            topEnd = spacing.radiusNormal
        ),
        sheetState = modalBottomSheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = spacing.radiusNormal,
                            topEnd = spacing.radiusNormal
                        )
                    )
                    .background(MaterialTheme.colors.primary)
                    .padding(spacing.spaceNormal)
            ) {
                Text(
                    text = "Bottom sheet content",
                    color = MaterialTheme.colors.onPrimary
                )
            }
        }
    ) {
        content()
    }
}
