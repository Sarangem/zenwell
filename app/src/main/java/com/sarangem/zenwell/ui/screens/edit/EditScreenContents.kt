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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.sarangem.zenwell.R
import com.sarangem.zenwell.database.tables.Schedules
import com.sarangem.zenwell.model.MathOperators
import com.sarangem.zenwell.model.UnlockMethod
import com.sarangem.zenwell.ui.screens.edit.fields.*

@Composable
fun EditScreenContents(
    modifier: Modifier = Modifier,
    uiState: EditUiState,
    updateSchedule: (Schedules) -> Unit = {},
    updateAppNames: (List<String>) -> Unit = {},
    showTopAppBar: Boolean = true,
    saveToDatabase: () -> Unit = {},
    deleteSchedule: () -> Unit = {},
    goBack: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            if (showTopAppBar) EditScreenTopAppBar(uiState.schedule.title, goBack)
        },
        floatingActionButton = {
            SaveAndDeleteButton(
                onSave = saveToDatabase,
                onDelete = deleteSchedule,
                goBack = goBack,
                isError = uiState.validationErrors.isNotEmpty()
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        //@formatter:off

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            DetailsCardColumn {
                DetailsCardWithTextField(R.string.schedule_title, uiState.schedule.title) { updateSchedule(uiState.schedule.copy(title = it)) }
                DetailsCardWithTextField(R.string.message, uiState.schedule.message, singleLine = false) { updateSchedule(uiState.schedule.copy(message = it)) }
            }
            ChooseAppList(uiState.appNames, uiState.viewsList, updateAppNames)

            if (!uiState.schedule.isPomodoro){
                DetailsCardColumn {
                    ChooseUnlockMethod(uiState.schedule.unlockMethod) { updateSchedule(uiState.schedule.copy(unlockMethod = it)) }

                    AnimatedContent(
                        targetState = uiState.schedule.unlockMethod,
                        transitionSpec = {
                            (expandVertically() + fadeIn()) togetherWith (shrinkVertically() + fadeOut())
                        }
                    ) { unlockMethod ->
                        Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))) {
                            when (unlockMethod) {
                                UnlockMethod.Timer -> {
                                    DetailsCardWithNumberField(
                                        R.string.timer_duration,
                                        uiState.schedule.timerDurationInSeconds,
                                        R.string.seconds,
                                        canBeZero = false
                                    ) { updateSchedule(uiState.schedule.copy(timerDurationInSeconds = it)) }
                                }

                                UnlockMethod.Breathing -> {
                                    DetailsCardWithNumberField(
                                        R.string.breathing_cycle_duration,
                                        uiState.schedule.breathingCycleDurationInSeconds,
                                        R.string.seconds,
                                        canBeZero = false
                                    ) { updateSchedule(uiState.schedule.copy(breathingCycleDurationInSeconds = it)) }
                                    DetailsCardWithNumberField(
                                        R.string.number_of_breathing_cycles,
                                        uiState.schedule.breathingCycleNumber,
                                        canBeZero = false
                                    ) { updateSchedule(uiState.schedule.copy(breathingCycleNumber = it)) }
                                }

                                UnlockMethod.MathProblem -> {
                                    DetailsCardWithNumberField(
                                        R.string.number_of_operands,
                                        uiState.schedule.mathEquationNumOperands,
                                        null,
                                        ValidationError.MathEquationNumOperands in uiState.validationErrors,
                                        R.string.must_be_2_or_greater
                                    ) { updateSchedule(uiState.schedule.copy(mathEquationNumOperands = it)) }
                                    DetailsCardWithRangeNumberField(
                                        R.string.range_of_digit_of_operands,
                                        uiState.schedule.mathEquationMinNumber,
                                        uiState.schedule.mathEquationMaxNumber,
                                        { updateSchedule(uiState.schedule.copy(mathEquationMinNumber = it)) },
                                        { updateSchedule(uiState.schedule.copy(mathEquationMaxNumber = it)) }
                                    )
                                    AnimatedVisibility(MathOperators.MULTIPLICATION in uiState.schedule.allowedMathOperators) {
                                        DetailsCardWithRangeNumberField(
                                            R.string.range_of_digit_of_operands_in_multiplication,
                                            uiState.schedule.mathEquationMinNumberInMultiplication,
                                            uiState.schedule.mathEquationMaxNumberInMultiplication,
                                            { updateSchedule(uiState.schedule.copy(mathEquationMinNumberInMultiplication = it)) },
                                            { updateSchedule(uiState.schedule.copy(mathEquationMaxNumberInMultiplication = it)) }
                                        )
                                    }
                                    LabelDetailsCard(
                                        R.string.operators_to_use,
                                        MathOperators.entries.map { operator ->
                                            LabelState(
                                                title = operator.titleRes,
                                                isSelected = operator in uiState.schedule.allowedMathOperators,
                                                onSelectChange = {
                                                    if (it) {
                                                        updateSchedule(uiState.schedule.copy(allowedMathOperators = uiState.schedule.allowedMathOperators + operator))
                                                    } else if (uiState.schedule.allowedMathOperators.size > 1) {
                                                        updateSchedule(uiState.schedule.copy(allowedMathOperators = uiState.schedule.allowedMathOperators - operator))
                                                    }
                                                }
                                            )
                                        },
                                    )
                                }

                                UnlockMethod.MultiplicationTable -> {
                                    DetailsCardWithRangeNumberField(
                                        R.string.range_of_multiplication_table_number,
                                        uiState.schedule.multiplicationMinNum,
                                        uiState.schedule.multiplicationMaxNum,
                                        { updateSchedule(uiState.schedule.copy(multiplicationMinNum = it)) },
                                        { updateSchedule(uiState.schedule.copy(multiplicationMaxNum = it)) }
                                    )
                                    DetailsCardWithRangeNumberField(
                                        R.string.range_of_multiplier,
                                        uiState.schedule.multiplierMinNum,
                                        uiState.schedule.multiplierMaxNum,
                                        { updateSchedule(uiState.schedule.copy(multiplierMinNum = it)) },
                                        { updateSchedule(uiState.schedule.copy(multiplierMaxNum = it)) }
                                    )
                                }
                                else -> {}
                            }
                        }
                    }
                    AnimatedVisibility(
                        visible = uiState.schedule.unlockMethod != UnlockMethod.StrictBlock,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))) {
                            DetailsCardWithNumberField(
                                R.string.usage_session_duration,
                                uiState.schedule.usageSessionDurationInMinutes,
                                R.string.minutes,
                                canBeZero = false
                            ) { updateSchedule(uiState.schedule.copy(usageSessionDurationInMinutes = it)) }
                            DetailsCardWithNumberField(
                                R.string.notify_before_closing,
                                uiState.schedule.notificationTimeInMinutes,
                                R.string.minutes,
                                ValidationError.NotificationTime in uiState.validationErrors,
                                R.string.notification_time_invalid
                            ) { updateSchedule(uiState.schedule.copy(notificationTimeInMinutes = it)) }

                            LabelDetailsCard(
                                mainText = R.string.additional_options,
                                labelList = listOf(
                                    LabelState(
                                        R.string.require_manual_unlock,
                                        uiState.schedule.requireManualUnlock,
                                    ) { updateSchedule(uiState.schedule.copy(requireManualUnlock = it)) },
                                    LabelState(
                                        R.string.show_parentheses,
                                        uiState.schedule.mathEquationShowParentheses,
                                        uiState.schedule.unlockMethod == UnlockMethod.MathProblem
                                    )  { updateSchedule(uiState.schedule.copy(mathEquationShowParentheses = it)) },
                                    LabelState(
                                        R.string.allow_negative_answers,
                                        uiState.schedule.mathEquationAllowNegatives,
                                        uiState.schedule.unlockMethod == UnlockMethod.MathProblem
                                    ) { updateSchedule(uiState.schedule.copy(mathEquationAllowNegatives = it)) }
                                )
                            )
                        }
                    }
                    if(
                        ValidationError.MathEquationNumOperands !in uiState.validationErrors &&
                        uiState.schedule.mathEquationMinNumber <= uiState.schedule.mathEquationMaxNumber &&
                        uiState.schedule.mathEquationMinNumberInMultiplication <= uiState.schedule.mathEquationMaxNumberInMultiplication
                    ){
                        AnimatedVisibility(
                            visible = uiState.schedule.unlockMethod == UnlockMethod.MathProblem,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            MathProblemExample(uiState.schedule)
                        }
                    }
                }
            }

            if (uiState.schedule.isPomodoro){
                DetailsCardColumn {
                    DetailsCardWithNumberField(
                        R.string.work_time,
                        uiState.schedule.pomodoroWorkTimeInMinutes,
                        R.string.minutes,
                        canBeZero = false
                    ) { updateSchedule(uiState.schedule.copy(pomodoroWorkTimeInMinutes = it)) }

                    DetailsCardWithNumberField(
                        R.string.rest_time,
                        uiState.schedule.pomodoroRestTimeInMinutes,
                        R.string.minutes,
                        canBeZero = false
                    ) { updateSchedule(uiState.schedule.copy(pomodoroRestTimeInMinutes = it)) }

                    DetailsCardWithNumberField(
                        R.string.number_of_pomodoro_sessions,
                        uiState.schedule.pomodoroSessionNumber,
                        null,
                        canBeZero = false
                    ) { updateSchedule(uiState.schedule.copy(pomodoroSessionNumber = it)) }

                    LabelDetailsCard(
                        R.string.actions_to_show_in_work_time,
                        listOf(
                            LabelState(
                                R.string.pause_resume,
                                uiState.schedule.showPauseInWorkTime,
                            ) { updateSchedule(uiState.schedule.copy(showPauseInWorkTime = it)) },
                            LabelState(
                                R.string.skip,
                                uiState.schedule.showSkipInWorkTime,
                            ) { updateSchedule(uiState.schedule.copy(showSkipInWorkTime = it)) }
                        )
                    )
                    LabelDetailsCard(
                        R.string.actions_to_show_in_rest_time,
                        listOf(
                            LabelState(
                                R.string.pause_resume,
                                uiState.schedule.showPauseInRestTime,
                            ) { updateSchedule(uiState.schedule.copy(showPauseInRestTime = it)) },
                            LabelState(
                                R.string.skip,
                                uiState.schedule.showSkipInRestTime,
                            ) { updateSchedule(uiState.schedule.copy(showSkipInRestTime = it)) }
                        )
                    )
                }
            }

            DetailsCardColumn {
                ChooseActiveSwitch(uiState.schedule.isActive) { updateSchedule(uiState.schedule.copy(isActive = it)) }
                AnimatedVisibility(
                    visible = uiState.schedule.isActive && !uiState.schedule.isPomodoro,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_small))) {
                        ChooseActiveTime(
                            uiState.schedule.startTimeInMinutes,
                            { updateSchedule(uiState.schedule.copy(startTimeInMinutes = it)) },
                            uiState.schedule.endTimeInMinutes,
                            { updateSchedule(uiState.schedule.copy(endTimeInMinutes = it)) },
                            ValidationError.ActiveTime in uiState.validationErrors,
                        )
                        SelectWeekDays(uiState.schedule.weekDays) { updateSchedule(uiState.schedule.copy(weekDays = it)) }
                    }
                }
            }

            Spacer(Modifier.height(dimensionResource(R.dimen.floating_action_button_height)))
            //@formatter:on
        }
    }
}