package com.example.passpoint.presentation.screens.main.admin

data class AdminMainState(
    val totalCourses: String = "0",
    val activeCourses: String = "0",
    val todayCourses: String = "0",
    val totalEvents: String = "0",
    val upcomingEvents: String = "0",
    val todayEvents: String = "0",
    val totalAttendances: Int = 0,
    val attended: Int = 0,
    val missed: Int = 0,
    val total: Int = 0,
    val totalUsers: Int = 0,
    val participants: Int = 0,
    val curators: Int = 0,
    val admins: Int = 0,
    val isLoading: Boolean = true
)