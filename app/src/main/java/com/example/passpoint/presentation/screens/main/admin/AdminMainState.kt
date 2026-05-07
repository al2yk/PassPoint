package com.example.passpoint.presentation.screens.main.admin

data class AdminMainState(
    val totalCourses: String = "0",
    val activeCourses: String = "0",
    val todayCourses: String = "0",
    val totalEvents: String = "0",
    val upcomingEvents: String = "0",
    val todayEvents: String = "0"
)