package com.sarangem.zenwell.ui.editscreen.details

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DetailsCardWithSwitch(
    modifier: Modifier = Modifier,
    isStacked: Boolean = false,
    mainText: String,
    checked: Boolean,
    motionScheme: MotionScheme = MotionScheme.standard(),
    onCheckedChange: (Boolean) -> Unit = {}
) {
    DetailsCard(
        modifier = modifier,
        isStacked = isStacked
    ) {
        Text(
            text = mainText,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.weight(1f))
        ZenwellTheme(motionScheme = motionScheme) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                modifier = Modifier.padding(end = dimensionResource(R.dimen.padding_small))
            )
        }
    }
}