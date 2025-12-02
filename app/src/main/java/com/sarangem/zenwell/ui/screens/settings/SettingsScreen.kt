package com.sarangem.zenwell.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.ZenwellTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    goBack: () -> Unit = {}
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = goBack,
                        modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.go_back)
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.headlineSmall,
                        maxLines = 1,
                    )
                },
            )
        }
    ) { innerPadding ->

        Column(
            modifier = modifier.padding(innerPadding)
        ){
            DataTransferCard(Modifier.fillMaxWidth())
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview(){
    ZenwellTheme {
        Column {
            DataTransferCardBody(Modifier.fillMaxWidth())
            Spacer(Modifier.weight(1f))
        }
    }
}