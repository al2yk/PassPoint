package com.example.passpoint.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester

@Composable
fun InputCode(
    isError: Boolean,
    onCodeEntered: (String) -> Unit = {},
    onCodeChanged: (String) -> Unit = {},
    submitOnComplete: Boolean = true,
    resetTrigger: Int,
    initialFocusRequester: FocusRequester? = null
) {
    val codeLength = 6
    val code = remember { mutableStateListOf(*Array(codeLength) { "" }) }
    val focusRequesters = remember { List(codeLength) { FocusRequester() } }

    LaunchedEffect(resetTrigger) {
        for (i in 0 until codeLength) {
            code[i] = ""
        }
        // Фокус на первый input после сброса
        (initialFocusRequester ?: focusRequesters.firstOrNull())?.requestFocus()
    }

    LaunchedEffect(Unit) {
        if (initialFocusRequester != null) {
            initialFocusRequester.requestFocus()
        }
    }

    Row(modifier = Modifier) {
        for (i in 0 until codeLength) {
            InputView(
                isError = isError,
                value = code[i],
                focusRequester = focusRequesters[i],
                onBackspaceWhenEmpty = {
                    if (i > 0) {
                        code[i - 1] = ""
                        focusRequesters[i - 1].requestFocus()
                        onCodeChanged(code.joinToString(""))
                    }
                },
                onValue = { newValue ->
                    if (newValue.length <= 1) {
                        code[i] = newValue
                        onCodeChanged(code.joinToString(""))
                        when {
                            newValue.isNotEmpty() && i < codeLength - 1 ->
                                focusRequesters[i + 1].requestFocus()
                            newValue.isEmpty() && i > 0 ->
                                focusRequesters[i - 1].requestFocus()
                        }
                    }
                    if (submitOnComplete && code.all { it.isNotEmpty() }) {
                        onCodeEntered(code.joinToString(""))
                    }
                },
                enabled = !isError
            )
        }
    }
}