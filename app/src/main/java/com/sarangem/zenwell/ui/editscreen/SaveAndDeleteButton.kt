package com.sarangem.zenwell.ui.editscreen

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@Composable
fun SaveButton(
    modifier: Modifier = Modifier,
    onSave: () -> Unit = {},
) {
    Button(
        onClick = onSave,
        modifier = modifier
    ) {
        Text("Save")
    }
}


@Preview(showBackground = true)
@Composable
fun SaveButtonPreview() {
    ZenwellTheme {
        SaveButton()
    }
}
