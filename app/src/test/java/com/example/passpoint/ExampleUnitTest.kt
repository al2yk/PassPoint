package com.example.passpoint

import com.example.passpoint.data.dto.CourseRegistration
import com.example.passpoint.data.dto.CourseWithEnrollment
import com.example.passpoint.data.dto.Event
import com.example.passpoint.domain.model.Result
import com.example.passpoint.domain.useCase.*
import com.example.passpoint.domain.utils.formatDateRu
import com.example.passpoint.presentation.screens.main.admin.roleToString
import com.example.passpoint.presentation.viewModel.AdminMainViewModel
import com.example.passpoint.presentation.viewModel.CoursesViewModel
import com.example.passpoint.presentation.viewModel.EventsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import java.time.LocalDate

@RunWith(Enclosed::class)
class AllTests {

    @OptIn(ExperimentalCoroutinesApi::class)
    class CoursesViewModelTest {

        private val getCourseUseCase: GetCourseUseCase = mock(GetCourseUseCase::class.java)
        private val getUserCourseRegistrationsUseCase: GetUserCourseRegistrationsUseCase = mock(GetUserCourseRegistrationsUseCase::class.java)
        private val registerForCourseUseCase: RegisterForCourseUseCase = mock(RegisterForCourseUseCase::class.java)
        private val unregisterFromCourseUseCase: UnregisterFromCourseUseCase = mock(UnregisterFromCourseUseCase::class.java)
        private val deleteCourseUseCase: DeleteCourseUseCase = mock(DeleteCourseUseCase::class.java)
        private val getCuratorsUseCase: GetCuratorsUseCase = mock(GetCuratorsUseCase::class.java)

        private lateinit var viewModel: CoursesViewModel

        @Before
        fun setup() {
            Dispatchers.setMain(UnconfinedTestDispatcher())
        }

        @After
        fun tearDown() {
            Dispatchers.resetMain()
        }

        @Test
        fun `load courses success`() = runTest {
            val futureDate = LocalDate.now().plusDays(1).toString()
            val courses = listOf(
                createCourse(1, date = futureDate),
                createCourse(2, date = futureDate)
            )
            `when`(getCourseUseCase()).thenReturn(Result.Success(courses))
            `when`(getUserCourseRegistrationsUseCase(anyString())).thenReturn(Result.Success(emptyList()))
            `when`(getCuratorsUseCase()).thenReturn(Result.Success(emptyList()))

            viewModel = createViewModel()

            val state = viewModel.state
            assertFalse(state.isLoading)
            assertEquals(2, state.upcomingCourses.size)
            assertNull(state.error)
        }

        @Test
        fun `load courses failure shows error`() = runTest {
            `when`(getCourseUseCase()).thenReturn(Result.Failure(Exception("Network error")))
            `when`(getUserCourseRegistrationsUseCase(anyString())).thenReturn(Result.Success(emptyList()))
            `when`(getCuratorsUseCase()).thenReturn(Result.Success(emptyList()))

            viewModel = createViewModel()

            val state = viewModel.state
            assertNotNull(state.error)
            assertTrue(state.error!!.contains("Ошибка загрузки курсов"))
        }

        @Test
        fun `register for course success`() = runTest {
            val courseId = 1
            val registration = CourseRegistration(id = 5, user = "user", course = courseId, status = 1)
            `when`(registerForCourseUseCase(eq(courseId), anyString())).thenReturn(Result.Success(registration))
            `when`(getCourseUseCase()).thenReturn(Result.Success(emptyList()))
            `when`(getUserCourseRegistrationsUseCase(anyString())).thenReturn(Result.Success(emptyList()))
            `when`(getCuratorsUseCase()).thenReturn(Result.Success(emptyList()))

            viewModel = createViewModel()

            viewModel.showRegisterConfirm(courseId)
            viewModel.confirmAction()

            verify(registerForCourseUseCase).invoke(eq(courseId), anyString())
            verify(getCourseUseCase, atLeast(2)).invoke()
        }

        @Test
        fun `unregister from course success`() = runTest {
            val courseId = 1
            val registrationId = 10
            val registration = CourseRegistration(id = registrationId, user = "user", course = courseId, status = 1)
            `when`(getCourseUseCase()).thenReturn(Result.Success(emptyList()))
            `when`(getUserCourseRegistrationsUseCase(anyString())).thenReturn(Result.Success(listOf(registration)))
            `when`(getCuratorsUseCase()).thenReturn(Result.Success(emptyList()))

            viewModel = createViewModel()

            viewModel.showUnregisterConfirm(courseId)
            viewModel.confirmAction()

            verify(unregisterFromCourseUseCase).invoke(registrationId)
        }

        @Test
        fun `delete course success`() = runTest {
            val courseId = 1
            `when`(deleteCourseUseCase(eq(courseId))).thenReturn(Result.Success(Unit))
            `when`(getCourseUseCase()).thenReturn(Result.Success(emptyList()))
            `when`(getUserCourseRegistrationsUseCase(anyString())).thenReturn(Result.Success(emptyList()))
            `when`(getCuratorsUseCase()).thenReturn(Result.Success(emptyList()))

            viewModel = createViewModel()

            viewModel.showDeleteConfirm(courseId)
            viewModel.confirmDeleteAction()

            verify(deleteCourseUseCase).invoke(eq(courseId))
            verify(getCourseUseCase, atLeast(2)).invoke()
        }

        private fun createViewModel() = CoursesViewModel(
            getCourseUseCase,
            getUserCourseRegistrationsUseCase,
            registerForCourseUseCase,
            unregisterFromCourseUseCase,
            deleteCourseUseCase,
            getCuratorsUseCase
        )

        private fun createCourse(id: Int, date: String = "2025-12-31") = CourseWithEnrollment(
            id = id,
            created_at = "2025-01-01",
            name = "Course $id",
            description = "",
            date = date,
            place = "",
            curator = "",
            capacity = 10,
            enrolled_count = 0
        )
    }

    class RoleToStringTest {
        @Test
        fun `returns correct string for known roles`() {
            assertEquals("Участник", roleToString(1))
            assertEquals("Куратор", roleToString(2))
            assertEquals("Администратор", roleToString(3))
        }

        @Test
        fun `returns default for unknown role`() {
            assertEquals("Неизв.", roleToString(0))
            assertEquals("Неизв.", roleToString(99))
        }
    }

    class FormatDateRuTest {
        @Test
        fun `formats ISO date correctly`() {
            val result = formatDateRu("2025-05-15")
            assertEquals("15 мая 2025", result)
        }

        @Test
        fun `handles invalid input by throwing exception`() {
            try {
                formatDateRu("invalid")
                fail("Expected DateTimeParseException")
            } catch (e: java.time.format.DateTimeParseException) {
                // ожидаемое поведение
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    class AdminMainViewModelTest {
        private val getCourseUseCase: GetCourseUseCase = mock(GetCourseUseCase::class.java)
        private val getEventsUseCase: GetEventsUseCase = mock(GetEventsUseCase::class.java)
        private val getAllAttendancesUseCase: GetAllAttendancesUseCase = mock(GetAllAttendancesUseCase::class.java)
        private val getAllUsersUseCase: GetAllUsersUseCase = mock(GetAllUsersUseCase::class.java)

        @Before
        fun setup() { Dispatchers.setMain(UnconfinedTestDispatcher()) }
        @After fun tearDown() { Dispatchers.resetMain() }

        @Test
        fun `load summary success`() = runTest {
            val futureDate = java.time.LocalDate.now().plusDays(5).toString()
            val todayDate = java.time.LocalDate.now().toString()

            val courses = listOf(
                CourseWithEnrollment(1, "now", "Course1", "", futureDate, "", "", 10, 5),
                CourseWithEnrollment(2, "now", "Course2", "", todayDate, "", "", 20, 3)
            )
            val events = listOf(
                Event(1, "now", "Event1", futureDate, "Room1"),
                Event(2, "now", "Event2", todayDate, "Room2")
            )

            `when`(getCourseUseCase()).thenReturn(Result.Success(courses))
            `when`(getEventsUseCase()).thenReturn(Result.Success(events))
            `when`(getAllAttendancesUseCase()).thenReturn(Result.Success(emptyList()))
            `when`(getAllUsersUseCase()).thenReturn(Result.Success(emptyList()))

            val viewModel = AdminMainViewModel(getCourseUseCase, getEventsUseCase, getAllAttendancesUseCase, getAllUsersUseCase)
            val state = viewModel.state

            assertEquals("2", state.totalCourses)
            assertEquals("2", state.activeCourses)
            assertEquals("1", state.todayCourses)
            assertEquals("2", state.totalEvents)
            assertEquals("2", state.upcomingEvents)
            assertEquals("1", state.todayEvents)
            assertFalse(state.isLoading)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    class EventsViewModelTest {
        private val getEventsUseCase: GetEventsUseCase = mock(GetEventsUseCase::class.java)
        private val getUserRegistrationsUseCase: GetUserEventRegistrationsUseCase = mock(GetUserEventRegistrationsUseCase::class.java)
        private val registerForEventUseCase: RegisterForEventUseCase = mock(RegisterForEventUseCase::class.java)
        private val unregisterFromEventUseCase: UnregisterFromEventUseCase = mock(UnregisterFromEventUseCase::class.java)
        private val deleteEventUseCase: DeleteEventUseCase = mock(DeleteEventUseCase::class.java)

        @Before fun setup() { Dispatchers.setMain(UnconfinedTestDispatcher()) }
        @After fun tearDown() { Dispatchers.resetMain() }

        @Test
        fun `load events success splits upcoming and past`() = runTest {
            val today = java.time.LocalDate.now().toString()
            val future = java.time.LocalDate.now().plusDays(1).toString()
            val past = java.time.LocalDate.now().minusDays(1).toString()

            val events = listOf(
                Event(1, "now", "Future Event", future, "Room1"),
                Event(2, "now", "Past Event", past, "Room2"),
                Event(3, "now", "Today Event", today, "Room3")
            )

            `when`(getEventsUseCase()).thenReturn(Result.Success(events))
            `when`(getUserRegistrationsUseCase(anyString())).thenReturn(Result.Success(emptyList()))

            val viewModel = EventsViewModel(getEventsUseCase, getUserRegistrationsUseCase, registerForEventUseCase, unregisterFromEventUseCase, deleteEventUseCase)
            val state = viewModel.state

            assertEquals(2, state.upcomingEvents.size)
            assertEquals(1, state.pastEvents.size)
            assertTrue(state.upcomingEvents.all { it.date >= today })
        }
    }
}