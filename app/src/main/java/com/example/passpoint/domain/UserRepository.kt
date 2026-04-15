package com.example.passpoint.domain

import android.content.Context
import android.content.SharedPreferences

object UserRepository {
    private var actSystem: SharedPreferences? = null

    fun init(context: Context) {
        actSystem = context.getSharedPreferences("actSystem", Context.MODE_PRIVATE)
    }

    var act: Int
        get() = actSystem?.getInt("act", 0) ?: 0
        set(value) {
            actSystem?.edit()?.putInt("act", value)?.apply()
        }

    var ID: String
        get() = actSystem?.getString("id", "") ?: ""
        set(value) {
            actSystem?.edit()?.putString("id", value)?.apply()
        }

    var user_token: String?
        get() = actSystem?.getString("user_token", "") ?: ""
        set(value) {
            actSystem?.edit()?.putString("user_token", value)?.apply()
        }

    var email: String
        get() = actSystem?.getString("email", "") ?: ""
        set(value) {
            actSystem?.edit()?.putString("email", value)?.apply()
        }
}