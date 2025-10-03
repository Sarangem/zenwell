package com.sarangem.zenwell.service.overlay.screens.mathequations

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.sarangem.zenwell.R
import com.sarangem.zenwell.ui.theme.Orbitron
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuestionCard(
    modifier: Modifier = Modifier,
    question: MathQuestion
){
    var isExpanded by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val tooltipState = rememberTooltipState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_small)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.problem),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small))
            )
            Spacer(Modifier.weight(1f))
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Left
                ),
                state = tooltipState,
                tooltip = {
                    RichTooltip(
                        title = {
                            Text(
                                text = stringResource(R.string.order_of_equations),
                                style = MaterialTheme.typography.titleSmallEmphasized
                            )
                        },
                        text = {
                            Text(
                                text = if (isExpanded) {
                                    stringResource(R.string.top_to_bottom)
                                } else {
                                    stringResource(R.string.left_to_right)
                                }
                            )
                        }
                    )
                }
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            tooltipState.show()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = stringResource(R.string.more_information)
                    )
                }
            }
            IconButton(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
                onClick = { isExpanded = !isExpanded }
            ) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) stringResource(R.string.collapse) else stringResource(
                        R.string.expand
                    )
                )
            }
        }

        HorizontalDivider(thickness = dimensionResource(R.dimen.card_elevation))

        Spacer(Modifier.size(dimensionResource(R.dimen.image_size)))
        Row(Modifier.horizontalScroll(rememberScrollState())) {
            Text(
                text = if (isExpanded) question.longQuestion else question.shortQuestion,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                fontFamily = Orbitron,
                maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                modifier = Modifier
                    .graphicsLayer(scaleY = 1.5f)
                    .padding(dimensionResource(R.dimen.padding_small))
            )
        }
        Spacer(Modifier.size(dimensionResource(R.dimen.image_size)))
    }
}