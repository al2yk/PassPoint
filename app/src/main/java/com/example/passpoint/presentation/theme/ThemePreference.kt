package com.example.passpoint.presentation.theme

enum class AppTheme(val ordinalValue: Int) {
    SYSTEM(0),
    LIGHT(1),
    DARK(2);

    companion object {
        fun fromOrdinal(value: Int): AppTheme = entries.find { it.ordinalValue == value } ?: SYSTEM
    }
}

const val THEME_PREF_KEY = "app_theme"