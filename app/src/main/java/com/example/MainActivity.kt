package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.data.local.AppDatabase
import com.example.data.repository.MySchoolRepository
import com.example.ui.screens.admin.AdminDashboardScreen
import com.example.ui.screens.ai.AITutorScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.landing.LandingScreen
import com.example.ui.screens.messaging.ChatScreen
import com.example.ui.screens.parent.ParentDashboardScreen
import com.example.ui.screens.quiz.QuizPlayScreen
import com.example.ui.screens.student.StudentDashboardScreen
import com.example.ui.screens.teacher.TeacherDashboardScreen
import com.example.ui.theme.MySchoolConnectTheme
import com.example.ui.viewmodel.MySchoolViewModel
import com.example.ui.viewmodel.MySchoolViewModelFactory
import com.example.ui.viewmodel.ScreenState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MySchoolViewModel by viewModels {
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = MySchoolRepository(db)
        MySchoolViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MySchoolConnectTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MySchoolApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MySchoolApp(viewModel: MySchoolViewModel) {
    val screenState by viewModel.screen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    when (val state = screenState) {
        is ScreenState.Landing -> {
            LandingScreen(
                onNavigateToLogin = { viewModel.navigateTo(ScreenState.Auth) },
                onQuickDemoLogin = { email, role ->
                    coroutineScope.launch {
                        viewModel.quickDemoLogin(email, role)
                    }
                }
            )
        }

        is ScreenState.Auth -> {
            AuthScreen(
                onLoginSuccess = { email, role ->
                    coroutineScope.launch {
                        viewModel.quickDemoLogin(email, role)
                    }
                },
                onBackToLanding = { viewModel.navigateTo(ScreenState.Landing) },
                onPerformLogin = { email, pass ->
                    viewModel.login(email, pass)
                },
                onPerformRegister = { name, email, pass, role, phone ->
                    viewModel.register(name, email, pass, role, phone)
                }
            )
        }

        is ScreenState.StudentPortal -> {
            val user = currentUser ?: return
            val studentRecord by viewModel.studentRecord.collectAsState()
            val schoolClass by viewModel.studentClass.collectAsState()
            val overallAverage by viewModel.studentOverallAverage.collectAsState()
            val attendance by viewModel.studentAttendance.collectAsState()
            val trend by viewModel.studentTrend.collectAsState()
            val subjectPerformances by viewModel.studentSubjectPerformances.collectAsState()
            val recentMarks by viewModel.studentRecentMarks.collectAsState()
            val assignments by viewModel.studentAssignments.collectAsState()
            val submissions by viewModel.studentSubmissions.collectAsState()
            val materials by viewModel.studentMaterials.collectAsState()
            val quizzes by viewModel.studentQuizzes.collectAsState()
            val feedbacks by viewModel.studentFeedbacks.collectAsState()
            val notifications by viewModel.userNotifications.collectAsState()
            val cachedGrades by viewModel.cachedStudentGrades.collectAsState()
            val offlineNotifications by viewModel.offlineSchoolNotifications.collectAsState()

            if (studentRecord != null) {
                StudentDashboardScreen(
                    studentUser = user,
                    studentRecord = studentRecord!!,
                    schoolClass = schoolClass,
                    overallAverage = overallAverage,
                    attendanceRate = attendance,
                    trend = trend,
                    subjectPerformances = subjectPerformances,
                    recentMarks = recentMarks,
                    assignments = assignments,
                    submissions = submissions,
                    materials = materials,
                    quizzes = quizzes,
                    feedbacks = feedbacks,
                    unreadNotifications = notifications.count { !it.isRead },
                    cachedGrades = cachedGrades,
                    offlineNotifications = offlineNotifications,
                    onRefreshCache = { viewModel.refreshOfflineGradesCache(studentRecord!!.id) },
                    onMarkOfflineNotificationRead = { viewModel.markOfflineNotificationRead(it) },
                    onMarkAllOfflineNotificationsRead = { viewModel.markAllOfflineNotificationsRead() },
                    onTakeQuiz = { quiz -> viewModel.startQuiz(quiz) },
                    onSubmitAssignment = { assId, content -> viewModel.submitAssignment(assId, content) },
                    onOpenChatWithTeacher = { teacherName ->
                        viewModel.openChat(1L, teacherName, "Subject Teacher")
                    },
                    onOpenAITutor = { viewModel.navigateTo(ScreenState.AITutor) },
                    onLogout = { viewModel.logout() }
                )
            }
        }

        is ScreenState.ParentPortal -> {
            val user = currentUser ?: return
            val parentRecord by viewModel.parentRecord.collectAsState()
            val children by viewModel.parentChildren.collectAsState()
            val selectedChildIndex by viewModel.selectedChildIndex.collectAsState()
            val childSubjectPerformances by viewModel.parentChildPerformances.collectAsState()
            val childRecentMarks by viewModel.parentChildRecentMarks.collectAsState()
            val childAssignments by viewModel.parentChildAssignments.collectAsState()
            val childFeedbacks by viewModel.parentChildFeedbacks.collectAsState()
            val conversations by viewModel.parentConversations.collectAsState()
            val announcements by viewModel.announcements.collectAsState()
            val notifications by viewModel.userNotifications.collectAsState()
            val cachedGrades by viewModel.cachedStudentGrades.collectAsState()
            val offlineNotifications by viewModel.offlineSchoolNotifications.collectAsState()

            if (parentRecord != null) {
                ParentDashboardScreen(
                    parentUser = user,
                    parentRecord = parentRecord!!,
                    children = children,
                    selectedChildIndex = selectedChildIndex,
                    onSelectChild = { viewModel.selectChild(it) },
                    childSubjectPerformances = childSubjectPerformances,
                    childRecentMarks = childRecentMarks,
                    childAssignments = childAssignments,
                    childFeedbacks = childFeedbacks,
                    conversations = conversations,
                    announcements = announcements,
                    unreadNotifications = notifications.count { !it.isRead },
                    cachedGrades = cachedGrades,
                    offlineNotifications = offlineNotifications,
                    onRefreshCache = {
                        val activeChild = children.getOrNull(selectedChildIndex)
                        if (activeChild != null) {
                            viewModel.refreshOfflineGradesCache(activeChild.student.id)
                        }
                    },
                    onMarkOfflineNotificationRead = { viewModel.markOfflineNotificationRead(it) },
                    onMarkAllOfflineNotificationsRead = { viewModel.markAllOfflineNotificationsRead() },
                    onOpenChat = { convId ->
                        viewModel.openChat(convId ?: 1L, "Teacher Murenzi (Biology)", "Teacher")
                    },
                    onStartNewChatWithTeacher = {
                        viewModel.openChat(1L, "Teacher Murenzi (Biology)", "Teacher")
                    },
                    onLogout = { viewModel.logout() }
                )
            }
        }

        is ScreenState.TeacherPortal -> {
            val user = currentUser ?: return
            val teacherRecord by viewModel.teacherRecord.collectAsState()
            val assignedClasses by viewModel.teacherAssignedClasses.collectAsState()
            val studentsInClasses by viewModel.teacherStudents.collectAsState()
            val recentAssessments by viewModel.teacherRecentAssessments.collectAsState()
            val recentSubmissions by viewModel.teacherSubmissions.collectAsState()
            val materials by viewModel.teacherMaterials.collectAsState()
            val conversations by viewModel.teacherConversations.collectAsState()
            val notifications by viewModel.userNotifications.collectAsState()

            if (teacherRecord != null) {
                TeacherDashboardScreen(
                    teacherUser = user,
                    teacherRecord = teacherRecord!!,
                    assignedClasses = assignedClasses,
                    studentsInClasses = studentsInClasses,
                    recentAssessments = recentAssessments,
                    recentSubmissions = recentSubmissions,
                    materials = materials,
                    conversations = conversations,
                    unreadNotifications = notifications.count { !it.isRead },
                    onEnterMark = { assId, studId, mark, fb ->
                        viewModel.enterStudentMark(assId, studId, mark, fb)
                    },
                    onCreateAssignment = { title, desc, clsId, date, maxScore ->
                        viewModel.createAssignment(title, desc, clsId, date, maxScore)
                    },
                    onUploadMaterial = { title, desc, clsId, topic, type, url ->
                        viewModel.uploadMaterial(title, desc, clsId, topic, type, url)
                    },
                    onOpenChat = { convId ->
                        viewModel.openChat(convId ?: 1L, "Mukamana Claire (Parent)", "Parent")
                    },
                    onLogout = { viewModel.logout() }
                )
            }
        }

        is ScreenState.AdminPortal -> {
            val user = currentUser ?: return
            val students by viewModel.allStudents.collectAsState()
            val parents by viewModel.allParents.collectAsState()
            val teachers by viewModel.allTeachers.collectAsState()
            val classes by viewModel.allClasses.collectAsState()
            val subjects by viewModel.allSubjects.collectAsState()
            val users by viewModel.allUsers.collectAsState()
            val announcements by viewModel.announcements.collectAsState()
            val notifications by viewModel.userNotifications.collectAsState()

            AdminDashboardScreen(
                adminUser = user,
                totalStudents = students.size,
                totalParents = parents.size,
                totalTeachers = teachers.size,
                totalClasses = classes.size,
                classesList = classes,
                subjectsList = subjects,
                usersList = users,
                announcements = announcements,
                unreadNotifications = notifications.count { !it.isRead },
                onCreateAnnouncement = { title, content, target, priority ->
                    viewModel.createAnnouncement(title, content, target, priority)
                },
                onAddClass = { name, grade -> viewModel.addClass(name, grade) },
                onAddSubject = { name, code -> viewModel.addSubject(name, code) },
                onLogout = { viewModel.logout() }
            )
        }

        is ScreenState.Chat -> {
            val user = currentUser ?: return
            val messages by viewModel.chatMessages.collectAsState()

            ChatScreen(
                currentUserId = user.id,
                otherUserName = state.otherUserName,
                otherUserRole = state.otherUserRole,
                messages = messages,
                onSendMessage = { viewModel.sendMessage(it) },
                onBack = {
                    when (user.role) {
                        com.example.data.local.entities.UserRole.STUDENT -> viewModel.navigateTo(ScreenState.StudentPortal)
                        com.example.data.local.entities.UserRole.PARENT -> viewModel.navigateTo(ScreenState.ParentPortal)
                        com.example.data.local.entities.UserRole.TEACHER -> viewModel.navigateTo(ScreenState.TeacherPortal)
                        com.example.data.local.entities.UserRole.ADMIN -> viewModel.navigateTo(ScreenState.AdminPortal)
                    }
                }
            )
        }

        is ScreenState.QuizPlay -> {
            val questions by viewModel.activeQuizQuestions.collectAsState()

            QuizPlayScreen(
                quiz = state.quiz,
                questions = questions,
                onCompleteQuiz = { score, total ->
                    viewModel.completeQuiz(state.quiz.id, score, total)
                },
                onBack = { viewModel.navigateTo(ScreenState.StudentPortal) }
            )
        }

        is ScreenState.AITutor -> {
            AITutorScreen(
                onBack = { viewModel.navigateTo(ScreenState.StudentPortal) }
            )
        }
    }
}
