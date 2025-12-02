package com.sarangem.zenwell.ui.screens.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes.Companion.Circle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun EditScreenPlaceholder(
    modifier: Modifier = Modifier,
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .clip(Circle.toShape())
                .background(MaterialTheme.colorScheme.inversePrimary)
                .weight(0.25f)
                .padding(dimensionResource(R.dimen.padding_small)),
            contentAlignment = Alignment.Center
        ){
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxSize(0.7f)
            )
        }
        Text(
            text = stringResource(R.string.edit_screen_placeholder_text),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .weight(0.25f)
                .padding(dimensionResource(R.dimen.padding_small))
        )
        Spacer(Modifier.weight(1f))
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun EditScreenPlaceholderPreview(){
    ZenwellTheme {
        EditScreenPlaceholder()
    }
}