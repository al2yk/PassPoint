package com.example.passpoint.presentation.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class ThemeManager @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore
    private val themeKey = intPreferencesKey(THEME_PREF_KEY)

    // Поток с выбранной темой
    val themeFlow: Flow<AppTheme> = dataStore.data
        .map { preferences ->
            val ordinal = preferences[themeKey] ?: AppTheme.SYSTEM.ordinalValue
            AppTheme.fromOrdinal(ordinal)
        }

    // Сохранение темы
    suspend fun saveTheme(theme: AppTheme) {
        dataStore.edit { preferences ->
            preferences[themeKey] = theme.ordinalValue
        }
    }
}