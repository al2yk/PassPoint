package com.example.passpoint.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.passpoint.presentation.ui.theme.Black900
import com.example.passpoint.presentation.ui.theme.Gray450
import com.example.passpoint.presentation.ui.theme.Gray600
import com.example.passpoint.presentation.ui.theme.Red300
import com.example.passpoint.presentation.ui.theme.White

@Composable
fun InputView(
    value: String,
    isError: Boolean,
    focusRequester: FocusRequester,
    onBackspaceWhenEmpty: (() -> Unit)? = null,
    onValue: (String) -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .width(56.dp)
            .height(99.dp)
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                val digitsOnly = newValue.filter { it.isDigit() }
                if (digitsOnly.length <= 1) {
                    onValue(digitsOnly)
                }
            },
            singleLine = true,
            maxLines = 1,
            isError = isError,
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .width(46.dp)
                .height(99.dp)
                .focusRequester(focusRequester)
                .onKeyEvent { keyEvent ->
                    if (enabled && keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Backspace) {
                        if (value.isEmpty()) {
                            onBackspaceWhenEmpty?.invoke()
                            true
                        } else {
                            false
                        }
                    } else {
                        false
                    }
                },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Gray450,
                unfocusedContainerColor = White,
                unfocusedTextColor = Black900,
                focusedContainerColor = White,
                focusedTextColor = Black900,
                focusedBorderColor = Gray600,
                errorBorderColor = Red300,
            ),
            textStyle = TextStyle.Default.copy(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Black900
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = enabled
        )
    }
}