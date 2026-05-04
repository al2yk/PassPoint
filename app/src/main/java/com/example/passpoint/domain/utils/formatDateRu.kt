package com.example.passpoint.domain.utils

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDateRu(dateString: String): String {
    val inputDate = LocalDate.parse(dateString)
    val outputFormatter = DateTimeFormatter.ofPattern(
        "d MMMM yyyy", Locale("ru")
    )
    return inputDate.format(outputFormatter)
}