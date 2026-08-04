/*
 * Copyright (c) 2026 Proneel Pal <palproneel@gmail.com>
 * Licensed under the GNU General Public License v3.0 or later.
 */

package com.sarangem.zenwell.ui.screens.edit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.model.MathOperators
import com.sarangem.zenwell.model.UnlockMethod
import com.sarangem.zenwell.ui.screens.edit.fields.*
import com.sarangem.zenwell.ui.theme.sizing

@Composable
fun EditScreenContents(
    modifier: Modifier = Modifier,
    uiState: EditUiState,
    updateSchedule: (Schedules) -> Unit = {},
    updateAppNames: (List<String>) -> Unit = {},
    showTopAppBar: Boolean = true,
    saveToDatabase: () -> Unit = {},
    deleteSchedule: () -> Unit = {},
    showcase2Modifier: Modifier = Modifier,
    showcase2onClick: () -> Unit = {},
    showcase2onDismiss: () -> Unit = {},
    showcase3Modifier: Modifier = Modifier,
    showcase3onClick: () -> Unit = {},
    showcase3onDismiss: () -> Unit = {},
    showcase4Modifier: Modifier = Modifier,
    showcase4onClick: () -> Unit = {},
    userScrollEnabled: Boolean = true,
    goBack: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            if (showTopAppBar) EditScreenTopAppBar(uiState.schedule.title, goBack)
        },
        floatingActionButton = {
            SaveAndDeleteButton(
                modifier = showcase4Modifier,
                onSave = saveToDatabase,
                onDelete = deleteSchedule,
                goBack = goBack,
                isError = uiState.validationErrors.isNotEmpty(),
                onShowcaseClick = showcase4onClick
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        //@formatter:off
        with(uiState.schedule) {

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState(), enabled = userScrollEnabled)
            ) {
                DetailsCardColumn {
                    DetailsCardWithTextField(R.string.schedule_title, title) { updateSchedule(copy(title = it)) }
                    DetailsCardWithTextField(R.string.message, message, singleLine = false) { updateSchedule(copy(message = it)) }
                }
                ChooseAppList(showcase2Modifier, uiState.appNames, uiState.viewsList, updateAppNames, showcase2onClick, showcase2onDismiss)

                if (!isPomodoro) {
                    DetailsCardColumn {
                        ChooseUnlockMethod(
                            modifier = showcase3Modifier,
                            unlockMethod = unlockMethod,
                            updateValue = { updateSchedule(copy(unlockMethod = it)) },
                            onShowcaseClick = showcase3onClick,
                            onShowcaseDismiss = showcase3onDismiss
                        )

                        AnimatedContent(
                            targetState = unlockMethod,
                            transitionSpec = {
                                (expandVertically() + fadeIn()) togetherWith (shrinkVertically() + fadeOut())
                            }
                        ) { method ->
                            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.tiny)) {
                                when (method) {
                                    UnlockMethod.Timer -> {
                                        DetailsCardWithNumberField(
                                            R.string.timer_duration,
                                            timerDurationInSeconds,
                                            R.string.seconds,
                                            canBeZero = false
                                        ) { updateSchedule(copy(timerDurationInSeconds = it)) }
                                    }

                                    UnlockMethod.Breathing -> {
                                        DetailsCardWithNumberField(
                                            R.string.breathing_cycle_duration,
                                            breathingCycleDurationInSeconds,
                                            R.string.seconds,
                                            canBeZero = false
                                        ) { updateSchedule(copy(breathingCycleDurationInSeconds = it)) }
                                        DetailsCardWithNumberField(
                                            R.string.number_of_breathing_cycles,
                                            breathingCycleNumber,
                                            canBeZero = false
                                        ) { updateSchedule(copy(breathingCycleNumber = it)) }
                                    }

                                    UnlockMethod.MathProblem -> {
                                        DetailsCardWithNumberField(
                                            R.string.number_of_operands,
                                            mathEquationNumOperands,
                                            null,
                                            ValidationError.MathEquationNumOperands in uiState.validationErrors,
                                            R.string.must_be_2_or_greater
                                        ) { updateSchedule(copy(mathEquationNumOperands = it)) }
                                        DetailsCardWithRangeNumberField(
                                            R.string.range_of_digit_of_operands,
                                            mathEquationMinNumber,
                                            mathEquationMaxNumber,
                                            { updateSchedule(copy(mathEquationMinNumber = it)) },
                                            { updateSchedule(copy(mathEquationMaxNumber = it)) }
                                        )
                                        AnimatedVisibility(MathOperators.MULTIPLICATION in allowedMathOperators) {
                                            DetailsCardWithRangeNumberField(
                                                R.string.range_of_digit_of_operands_in_multiplication,
                                                mathEquationMinNumberInMultiplication,
                                                mathEquationMaxNumberInMultiplication,
                                                { updateSchedule(copy(mathEquationMinNumberInMultiplication = it)) },
                                                { updateSchedule(copy(mathEquationMaxNumberInMultiplication = it)) }
                                            )
                                        }
                                        LabelDetailsCard(
                                            R.string.operators_to_use,
                                            MathOperators.entries.map { operator ->
                                                LabelState(
                                                    title = operator.titleRes,
                                                    isSelected = operator in allowedMathOperators,
                                                    onSelectChange = {
                                                        if (it) {
                                                            updateSchedule(copy(allowedMathOperators = allowedMathOperators + operator))
                                                        } else if (allowedMathOperators.size > 1) {
                                                            updateSchedule(copy(allowedMathOperators = allowedMathOperators - operator))
                                                        }
                                                    }
                                                )
                                            },
                                        )
                                    }

                                    UnlockMethod.MultiplicationTable -> {
                                        DetailsCardWithRangeNumberField(
                                            R.string.range_of_multiplication_table_number,
                                            multiplicationMinNum,
                                            multiplicationMaxNum,
                                            { updateSchedule(copy(multiplicationMinNum = it)) },
                                            { updateSchedule(copy(multiplicationMaxNum = it)) }
                                        )
                                        DetailsCardWithRangeNumberField(
                                            R.string.range_of_multiplier,
                                            multiplierMinNum,
                                            multiplierMaxNum,
                                            { updateSchedule(copy(multiplierMinNum = it)) },
                                            { updateSchedule(copy(multiplierMaxNum = it)) }
                                        )
                                    }

                                    else -> {}
                                }
                            }
                        }
                        AnimatedVisibility(
                            visible = unlockMethod != UnlockMethod.StrictBlock,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.tiny)) {
                                DetailsCardWithNumberField(
                                    R.string.usage_session_duration,
                                    usageSessionDurationInMinutes,
                                    R.string.minutes,
                                    canBeZero = false
                                ) { updateSchedule(copy(usageSessionDurationInMinutes = it)) }
                                DetailsCardWithNumberField(
                                    R.string.notify_before_closing,
                                    notificationTimeInMinutes,
                                    R.string.minutes,
                                    ValidationError.NotificationTime in uiState.validationErrors,
                                    R.string.notification_time_invalid
                                ) { updateSchedule(copy(notificationTimeInMinutes = it)) }
                            }
                        }
                        LabelDetailsCard(
                            mainText = R.string.additional_options,
                            showExpandedButton = true,
                            labelList = listOf(
                                LabelState(
                                    R.string.show_exit_button,
                                    showExit
                                ) { updateSchedule(copy(showExit = it)) },
                                LabelState(
                                    R.string.play_background_media,
                                    playBackgroundMedia
                                ) { updateSchedule(copy(playBackgroundMedia = it)) },
                                LabelState(
                                    R.string.require_manual_unlock,
                                    requireManualUnlock,
                                    unlockMethod != UnlockMethod.StrictBlock
                                ) { updateSchedule(copy(requireManualUnlock = it)) },
                                LabelState(
                                    R.string.show_parentheses,
                                    mathEquationShowParentheses,
                                    unlockMethod == UnlockMethod.MathProblem
                                            && allowedMathOperators.contains(MathOperators.MULTIPLICATION)
                                            && allowedMathOperators.size > 2
                                ) { updateSchedule(copy(mathEquationShowParentheses = it)) },
                                LabelState(
                                    R.string.allow_negative_answers,
                                    mathEquationAllowNegatives,
                                    unlockMethod == UnlockMethod.MathProblem
                                ) { updateSchedule(copy(mathEquationAllowNegatives = it)) }
                            )
                        )
                        if (
                            ValidationError.MathEquationNumOperands !in uiState.validationErrors &&
                            mathEquationMinNumber <= mathEquationMaxNumber &&
                            mathEquationMinNumberInMultiplication <= mathEquationMaxNumberInMultiplication
                        ) {
                            AnimatedVisibility(
                                visible = unlockMethod == UnlockMethod.MathProblem,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                MathProblemExample(this@with)
                            }
                        }
                    }
                }

                if (isPomodoro) {
                    DetailsCardColumn {
                        DetailsCardWithNumberField(
                            R.string.work_time,
                            pomodoroWorkTimeInMinutes,
                            R.string.minutes,
                            canBeZero = false
                        ) { updateSchedule(copy(pomodoroWorkTimeInMinutes = it)) }

                        DetailsCardWithNumberField(
                            R.string.rest_time,
                            pomodoroRestTimeInMinutes,
                            R.string.minutes,
                            canBeZero = false
                        ) { updateSchedule(copy(pomodoroRestTimeInMinutes = it)) }

                        DetailsCardWithNumberField(
                            R.string.number_of_pomodoro_sessions,
                            pomodoroSessionNumber,
                            null,
                            canBeZero = false
                        ) { updateSchedule(copy(pomodoroSessionNumber = it)) }

                        LabelDetailsCard(
                            R.string.actions_to_show_in_work_time,
                            listOf(
                                LabelState(
                                    R.string.pause_resume,
                                    showPauseInWorkTime,
                                ) { updateSchedule(copy(showPauseInWorkTime = it)) },
                                LabelState(
                                    R.string.skip,
                                    showSkipInWorkTime,
                                ) { updateSchedule(copy(showSkipInWorkTime = it)) }
                            )
                        )
                        LabelDetailsCard(
                            R.string.actions_to_show_in_rest_time,
                            listOf(
                                LabelState(
                                    R.string.pause_resume,
                                    showPauseInRestTime,
                                ) { updateSchedule(copy(showPauseInRestTime = it)) },
                                LabelState(
                                    R.string.skip,
                                    showSkipInRestTime,
                                ) { updateSchedule(copy(showSkipInRestTime = it)) }
                            )
                        )
                        LabelDetailsCard(
                            mainText = R.string.additional_options,
                            labelList = listOf(
                                LabelState(
                                    R.string.show_exit_button,
                                    showExit
                                ) { updateSchedule(copy(showExit = it)) },
                                LabelState(
                                    R.string.play_background_media,
                                    playBackgroundMedia
                                ) { updateSchedule(copy(playBackgroundMedia = it)) }
                            ),
                            showExpandedButton = true
                        )
                    }
                }

                DetailsCardColumn {
                    if (!isPomodoro) ChooseActiveSwitch(isActive) { updateSchedule(copy(isActive = it)) }
                    AnimatedVisibility(
                        visible = isActive && !isPomodoro,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(MaterialTheme.sizing.tiny)) {
                            ChooseActiveTime(
                                startTimeInMinutes,
                                { updateSchedule(copy(startTimeInMinutes = it)) },
                                endTimeInMinutes,
                                { updateSchedule(copy(endTimeInMinutes = it)) },
                                ValidationError.ActiveTime in uiState.validationErrors,
                            )
                            SelectWeekDays(weekDays) { updateSchedule(copy(weekDays = it)) }
                        }
                    }
                }

                Spacer(Modifier.height(MaterialTheme.sizing.floatingBar))
            }
        }
        //@formatter:on
    }
}