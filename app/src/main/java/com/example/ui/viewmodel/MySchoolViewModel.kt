package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entities.AnnouncementEntity
import com.example.data.local.entities.AssessmentEntity
import com.example.data.local.entities.AssignmentEntity
import com.example.data.local.entities.AttendanceStatus
import com.example.data.local.entities.CachedSchoolNotificationEntity
import com.example.data.local.entities.CachedStudentGradeEntity
import com.example.data.local.entities.ConversationEntity
import com.example.data.local.entities.LearningMaterialEntity
import com.example.data.local.entities.MaterialContentType
import com.example.data.local.entities.MessageEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.ParentEntity
import com.example.data.local.entities.QuizEntity
import com.example.data.local.entities.QuizQuestionEntity
import com.example.data.local.entities.SchoolClassEntity
import com.example.data.local.entities.StudentEntity
import com.example.data.local.entities.SubjectEntity
import com.example.data.local.entities.SubmissionEntity
import com.example.data.local.entities.TeacherEntity
import com.example.data.local.entities.TeacherFeedbackEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.data.repository.ChildOverview
import com.example.data.repository.MarkWithAssessment
import com.example.data.repository.MySchoolRepository
import com.example.data.repository.PerformanceTrend
import com.example.data.repository.SubjectPerformance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenState {
    object Landing : ScreenState()
    object Auth : ScreenState()
    object StudentPortal : ScreenState()
    object ParentPortal : ScreenState()
    object TeacherPortal : ScreenState()
    object AdminPortal : ScreenState()
    data class Chat(val conversationId: Long, val otherUserName: String, val otherUserRole: String) : ScreenState()
    data class QuizPlay(val quiz: QuizEntity) : ScreenState()
    object AITutor : ScreenState()
}

class MySchoolViewModel(val repository: MySchoolRepository) : ViewModel() {

    private val _screen = MutableStateFlow<ScreenState>(ScreenState.Landing)
    val screen: StateFlow<ScreenState> = _screen.asStateFlow()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // --- Student State ---
    private val _studentRecord = MutableStateFlow<StudentEntity?>(null)
    val studentRecord: StateFlow<StudentEntity?> = _studentRecord.asStateFlow()

    private val _studentClass = MutableStateFlow<SchoolClassEntity?>(null)
    val studentClass: StateFlow<SchoolClassEntity?> = _studentClass.asStateFlow()

    private val _studentOverallAverage = MutableStateFlow(0.0)
    val studentOverallAverage: StateFlow<Double> = _studentOverallAverage.asStateFlow()

    private val _studentAttendance = MutableStateFlow(100.0)
    val studentAttendance: StateFlow<Double> = _studentAttendance.asStateFlow()

    private val _studentTrend = MutableStateFlow(PerformanceTrend.STABLE)
    val studentTrend: StateFlow<PerformanceTrend> = _studentTrend.asStateFlow()

    private val _studentSubjectPerformances = MutableStateFlow<List<SubjectPerformance>>(emptyList())
    val studentSubjectPerformances: StateFlow<List<SubjectPerformance>> = _studentSubjectPerformances.asStateFlow()

    private val _studentRecentMarks = MutableStateFlow<List<MarkWithAssessment>>(emptyList())
    val studentRecentMarks: StateFlow<List<MarkWithAssessment>> = _studentRecentMarks.asStateFlow()

    private val _studentAssignments = MutableStateFlow<List<AssignmentEntity>>(emptyList())
    val studentAssignments: StateFlow<List<AssignmentEntity>> = _studentAssignments.asStateFlow()

    private val _studentSubmissions = MutableStateFlow<List<SubmissionEntity>>(emptyList())
    val studentSubmissions: StateFlow<List<SubmissionEntity>> = _studentSubmissions.asStateFlow()

    private val _studentMaterials = MutableStateFlow<List<LearningMaterialEntity>>(emptyList())
    val studentMaterials: StateFlow<List<LearningMaterialEntity>> = _studentMaterials.asStateFlow()

    private val _studentQuizzes = MutableStateFlow<List<QuizEntity>>(emptyList())
    val studentQuizzes: StateFlow<List<QuizEntity>> = _studentQuizzes.asStateFlow()

    private val _studentFeedbacks = MutableStateFlow<List<TeacherFeedbackEntity>>(emptyList())
    val studentFeedbacks: StateFlow<List<TeacherFeedbackEntity>> = _studentFeedbacks.asStateFlow()

    // --- Parent State ---
    private val _parentRecord = MutableStateFlow<ParentEntity?>(null)
    val parentRecord: StateFlow<ParentEntity?> = _parentRecord.asStateFlow()

    private val _parentChildren = MutableStateFlow<List<ChildOverview>>(emptyList())
    val parentChildren: StateFlow<List<ChildOverview>> = _parentChildren.asStateFlow()

    private val _selectedChildIndex = MutableStateFlow(0)
    val selectedChildIndex: StateFlow<Int> = _selectedChildIndex.asStateFlow()

    private val _parentChildPerformances = MutableStateFlow<List<SubjectPerformance>>(emptyList())
    val parentChildPerformances: StateFlow<List<SubjectPerformance>> = _parentChildPerformances.asStateFlow()

    private val _parentChildRecentMarks = MutableStateFlow<List<MarkWithAssessment>>(emptyList())
    val parentChildRecentMarks: StateFlow<List<MarkWithAssessment>> = _parentChildRecentMarks.asStateFlow()

    private val _parentChildAssignments = MutableStateFlow<List<AssignmentEntity>>(emptyList())
    val parentChildAssignments: StateFlow<List<AssignmentEntity>> = _parentChildAssignments.asStateFlow()

    private val _parentChildFeedbacks = MutableStateFlow<List<TeacherFeedbackEntity>>(emptyList())
    val parentChildFeedbacks: StateFlow<List<TeacherFeedbackEntity>> = _parentChildFeedbacks.asStateFlow()

    private val _parentConversations = MutableStateFlow<List<ConversationEntity>>(emptyList())
    val parentConversations: StateFlow<List<ConversationEntity>> = _parentConversations.asStateFlow()

    // --- Teacher State ---
    private val _teacherRecord = MutableStateFlow<TeacherEntity?>(null)
    val teacherRecord: StateFlow<TeacherEntity?> = _teacherRecord.asStateFlow()

    private val _teacherAssignedClasses = MutableStateFlow<List<SchoolClassEntity>>(emptyList())
    val teacherAssignedClasses: StateFlow<List<SchoolClassEntity>> = _teacherAssignedClasses.asStateFlow()

    private val _teacherStudents = MutableStateFlow<List<Pair<StudentEntity, UserEntity>>>(emptyList())
    val teacherStudents: StateFlow<List<Pair<StudentEntity, UserEntity>>> = _teacherStudents.asStateFlow()

    private val _teacherRecentAssessments = MutableStateFlow<List<AssessmentEntity>>(emptyList())
    val teacherRecentAssessments: StateFlow<List<AssessmentEntity>> = _teacherRecentAssessments.asStateFlow()

    private val _teacherSubmissions = MutableStateFlow<List<SubmissionEntity>>(emptyList())
    val teacherSubmissions: StateFlow<List<SubmissionEntity>> = _teacherSubmissions.asStateFlow()

    private val _teacherMaterials = MutableStateFlow<List<LearningMaterialEntity>>(emptyList())
    val teacherMaterials: StateFlow<List<LearningMaterialEntity>> = _teacherMaterials.asStateFlow()

    private val _teacherConversations = MutableStateFlow<List<ConversationEntity>>(emptyList())
    val teacherConversations: StateFlow<List<ConversationEntity>> = _teacherConversations.asStateFlow()

    // --- Admin State ---
    val allClasses = repository.getAllClasses().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allSubjects = repository.getAllSubjects().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allUsers = repository.userDao.getAllUsers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allStudents = repository.getAllStudents().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allParents = repository.getAllParents().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val allTeachers = repository.getAllTeachers().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // --- Announcements & Notifications ---
    val announcements = repository.getAllAnnouncements().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _userNotifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val userNotifications: StateFlow<List<NotificationEntity>> = _userNotifications.asStateFlow()

    // --- Offline Caching: Grades & Notifications ---
    private val _cachedStudentGrades = MutableStateFlow<List<CachedStudentGradeEntity>>(emptyList())
    val cachedStudentGrades: StateFlow<List<CachedStudentGradeEntity>> = _cachedStudentGrades.asStateFlow()

    private val _offlineSchoolNotifications = MutableStateFlow<List<CachedSchoolNotificationEntity>>(emptyList())
    val offlineSchoolNotifications: StateFlow<List<CachedSchoolNotificationEntity>> = _offlineSchoolNotifications.asStateFlow()

    private val _unreadOfflineNotificationsCount = MutableStateFlow(0)
    val unreadOfflineNotificationsCount: StateFlow<Int> = _unreadOfflineNotificationsCount.asStateFlow()

    // --- Chat Specific ---
    private val _chatMessages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val chatMessages: StateFlow<List<MessageEntity>> = _chatMessages.asStateFlow()

    // --- Quiz Specific ---
    private val _activeQuizQuestions = MutableStateFlow<List<QuizQuestionEntity>>(emptyList())
    val activeQuizQuestions: StateFlow<List<QuizQuestionEntity>> = _activeQuizQuestions.asStateFlow()

    // --- Navigation Functions ---
    fun navigateTo(screenState: ScreenState) {
        _screen.value = screenState
    }

    fun logout() {
        _currentUser.value = null
        _screen.value = ScreenState.Landing
    }

    // --- Authentication Actions ---
    suspend fun login(email: String, pass: String): Pair<Boolean, String?> {
        val user = repository.authenticate(email, pass)
        return if (user != null) {
            setupUserSession(user)
            Pair(true, null)
        } else {
            Pair(false, "Invalid credentials. Please verify your email and password.")
        }
    }

    suspend fun quickDemoLogin(email: String, role: UserRole) {
        val user = repository.userDao.getByEmail(email) ?: repository.registerUser(
            fullName = if (role == UserRole.STUDENT) "Keza Ines" else "User",
            email = email,
            passwordHash = "password123",
            role = role,
            phone = "+250 788 000 000"
        )
        setupUserSession(user)
    }

    suspend fun register(name: String, email: String, pass: String, role: UserRole, phone: String): Pair<Boolean, String?> {
        val existing = repository.userDao.getByEmail(email)
        if (existing != null) {
            return Pair(false, "An account with this email already exists.")
        }
        val newUser = repository.registerUser(name, email, pass, role, phone)
        // Set up role profile
        when (role) {
            UserRole.STUDENT -> {
                val firstClass = repository.academicDao.getAllClasses().first().firstOrNull()
                repository.studentDao.insert(
                    StudentEntity(
                        userId = newUser.id,
                        registrationNumber = "RW/2026/S4/${(100..999).random()}",
                        classId = firstClass?.id ?: 1L,
                        gender = "Student"
                    )
                )
            }
            UserRole.PARENT -> {
                repository.parentDao.insert(
                    ParentEntity(
                        userId = newUser.id,
                        occupation = "Parent Guardian",
                        address = "Kigali, Rwanda"
                    )
                )
            }
            UserRole.TEACHER -> {
                repository.teacherDao.insert(
                    TeacherEntity(
                        userId = newUser.id,
                        employeeNumber = "EMP-${(100..999).random()}",
                        department = "Secondary Sciences"
                    )
                )
            }
            UserRole.ADMIN -> {}
        }
        setupUserSession(newUser)
        return Pair(true, null)
    }

    private fun setupUserSession(user: UserEntity) {
        _currentUser.value = user
        viewModelScope.launch {
            // Load Notifications
            repository.getNotificationsForUser(user.id).collectLatest {
                _userNotifications.value = it
            }
        }
        viewModelScope.launch {
            // Load Offline Cached Notifications
            repository.getOfflineNotifications(user.id, user.role.name).collectLatest {
                _offlineSchoolNotifications.value = it
            }
        }
        viewModelScope.launch {
            repository.getUnreadOfflineNotificationCount(user.id, user.role.name).collectLatest {
                _unreadOfflineNotificationsCount.value = it
            }
        }

        when (user.role) {
            UserRole.STUDENT -> loadStudentData(user)
            UserRole.PARENT -> loadParentData(user)
            UserRole.TEACHER -> loadTeacherData(user)
            UserRole.ADMIN -> _screen.value = ScreenState.AdminPortal
        }
    }

    private fun loadStudentData(user: UserEntity) {
        viewModelScope.launch {
            val student = repository.getStudentByUserId(user.id)
            _studentRecord.value = student
            if (student != null) {
                val cls = repository.getClassById(student.classId)
                _studentClass.value = cls

                _studentOverallAverage.value = repository.calculateStudentOverallAverage(student.id)
                _studentAttendance.value = repository.calculateAttendancePercentage(student.id)
                _studentTrend.value = repository.calculatePerformanceTrend(student.id)

                // Sync grades to local cache
                launch {
                    repository.syncGradesToCache(student.id)
                }

                // Collect local cached grades Flow
                launch {
                    repository.getCachedGradesForStudent(student.id).collectLatest {
                        _cachedStudentGrades.value = it
                    }
                }

                // Flows
                launch {
                    repository.getSubjectPerformances(student.id, student.classId).collectLatest {
                        _studentSubjectPerformances.value = it
                    }
                }
                launch {
                    repository.getStudentMarksWithDetails(student.id).collectLatest {
                        _studentRecentMarks.value = it
                    }
                }
                launch {
                    repository.learningDao.getAssignmentsByClass(student.classId).collectLatest {
                        _studentAssignments.value = it
                    }
                }
                launch {
                    repository.learningDao.getSubmissionsForStudent(student.id).collectLatest {
                        _studentSubmissions.value = it
                    }
                }
                launch {
                    repository.learningDao.getMaterialsByClass(student.classId).collectLatest {
                        _studentMaterials.value = it
                    }
                }
                launch {
                    repository.learningDao.getQuizzesByClass(student.classId).collectLatest {
                        _studentQuizzes.value = it
                    }
                }
                launch {
                    repository.feedbackDao.getFeedbackForStudent(student.id).collectLatest {
                        _studentFeedbacks.value = it
                    }
                }
            }
            _screen.value = ScreenState.StudentPortal
        }
    }

    private fun loadParentData(user: UserEntity) {
        viewModelScope.launch {
            val parent = repository.getParentByUserId(user.id)
            _parentRecord.value = parent
            if (parent != null) {
                launch {
                    repository.getLinkedChildrenOverview(parent.id).collectLatest { children ->
                        _parentChildren.value = children
                        if (children.isNotEmpty()) {
                            selectChild(0)
                        }
                    }
                }
                launch {
                    repository.getConversationsForParent(parent.id).collectLatest {
                        _parentConversations.value = it
                    }
                }
            }
            _screen.value = ScreenState.ParentPortal
        }
    }

    fun selectChild(index: Int) {
        _selectedChildIndex.value = index
        val child = _parentChildren.value.getOrNull(index) ?: return
        viewModelScope.launch {
            launch {
                repository.getSubjectPerformances(child.student.id, child.student.classId).collectLatest {
                    _parentChildPerformances.value = it
                }
            }
            launch {
                repository.getStudentMarksWithDetails(child.student.id).collectLatest {
                    _parentChildRecentMarks.value = it
                }
            }
            launch {
                repository.syncGradesToCache(child.student.id)
                repository.getCachedGradesForStudent(child.student.id).collectLatest {
                    _cachedStudentGrades.value = it
                }
            }
            launch {
                repository.learningDao.getAssignmentsByClass(child.student.classId).collectLatest {
                    _parentChildAssignments.value = it
                }
            }
            launch {
                repository.feedbackDao.getFeedbackForStudent(child.student.id).collectLatest {
                    _parentChildFeedbacks.value = it
                }
            }
        }
    }

    private fun loadTeacherData(user: UserEntity) {
        viewModelScope.launch {
            val teacher = repository.getTeacherByUserId(user.id)
            _teacherRecord.value = teacher
            if (teacher != null) {
                val classes = repository.getAllClasses().first()
                _teacherAssignedClasses.value = classes

                // Load students
                val allStudents = repository.getAllStudents().first()
                val userList = repository.userDao.getAllUsers().first().associateBy { it.id }
                _teacherStudents.value = allStudents.mapNotNull { st ->
                    val u = userList[st.userId] ?: return@mapNotNull null
                    Pair(st, u)
                }

                launch {
                    repository.marksDao.getAllAssessments().collectLatest {
                        _teacherRecentAssessments.value = it
                    }
                }
                launch {
                    repository.learningDao.getAllMaterials().collectLatest {
                        _teacherMaterials.value = it
                    }
                }
                launch {
                    repository.getConversationsForTeacher(teacher.id).collectLatest {
                        _teacherConversations.value = it
                    }
                }
            }
            _screen.value = ScreenState.TeacherPortal
        }
    }

    // --- Student Actions ---
    fun submitAssignment(assignmentId: Long, text: String) {
        val student = _studentRecord.value ?: return
        viewModelScope.launch {
            repository.submitAssignment(assignmentId, student.id, text)
        }
    }

    fun startQuiz(quiz: QuizEntity) {
        viewModelScope.launch {
            val questions = repository.learningDao.getQuestionsForQuiz(quiz.id)
            _activeQuizQuestions.value = questions
            _screen.value = ScreenState.QuizPlay(quiz)
        }
    }

    fun completeQuiz(quizId: Long, score: Int, total: Int) {
        val student = _studentRecord.value ?: return
        viewModelScope.launch {
            repository.learningDao.insertQuizAnswer(
                com.example.data.local.entities.QuizAnswerEntity(
                    quizId = quizId,
                    studentId = student.id,
                    score = score,
                    totalQuestions = total
                )
            )
        }
    }

    // --- Teacher Actions ---
    fun enterStudentMark(assessmentId: Long, studentId: Long, markVal: Double, feedback: String) {
        val teacher = _teacherRecord.value ?: return
        viewModelScope.launch {
            repository.enterOrUpdateMark(assessmentId, studentId, markVal, feedback, teacher.id)
        }
    }

    fun createAssignment(title: String, desc: String, classId: Long, dueDate: String, maxScore: Int) {
        val teacher = _teacherRecord.value ?: return
        viewModelScope.launch {
            repository.createAssignment(title, desc, classId, 1L, teacher.id, dueDate, maxScore)
        }
    }

    fun uploadMaterial(title: String, desc: String, classId: Long, topic: String, type: MaterialContentType, url: String) {
        val teacher = _teacherRecord.value ?: return
        viewModelScope.launch {
            repository.uploadMaterial(title, desc, classId, 1L, 2L, topic, type, url, teacher.id)
        }
    }

    // --- Admin Actions ---
    fun createAnnouncement(title: String, content: String, targetRole: String, priority: String) {
        viewModelScope.launch {
            repository.createAnnouncement(title, content, targetRole, "School Administration", priority)
        }
    }

    fun addClass(name: String, grade: String) {
        viewModelScope.launch {
            repository.academicDao.insertClass(
                SchoolClassEntity(name = name, gradeLevel = grade, academicYearId = 1L)
            )
        }
    }

    fun addSubject(name: String, code: String) {
        viewModelScope.launch {
            repository.academicDao.insertSubject(
                SubjectEntity(name = name, code = code)
            )
        }
    }

    // --- Chat Actions ---
    fun openChat(conversationId: Long, otherName: String, otherRole: String) {
        viewModelScope.launch {
            repository.getMessagesForConversation(conversationId).collectLatest {
                _chatMessages.value = it
            }
        }
        _screen.value = ScreenState.Chat(conversationId, otherName, otherRole)
    }

    fun sendMessage(content: String) {
        val user = _currentUser.value ?: return
        val currentScreen = _screen.value as? ScreenState.Chat ?: return
        viewModelScope.launch {
            repository.sendMessage(
                convId = currentScreen.conversationId,
                senderUserId = user.id,
                receiverUserId = if (user.role == UserRole.PARENT) 4L else 3L,
                content = content
            )
        }
    }

    // --- Offline Cache Management Actions ---
    fun markOfflineNotificationRead(notificationId: Long) {
        viewModelScope.launch {
            repository.markOfflineNotificationRead(notificationId)
        }
    }

    fun markAllOfflineNotificationsRead() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.markAllOfflineNotificationsRead(user.id)
        }
    }

    fun refreshOfflineGradesCache(studentId: Long) {
        viewModelScope.launch {
            repository.syncGradesToCache(studentId)
        }
    }

    fun saveGradeToOfflineCache(grade: CachedStudentGradeEntity) {
        viewModelScope.launch {
            repository.cacheStudentGrade(grade)
        }
    }

    fun saveNotificationToOfflineCache(notification: CachedSchoolNotificationEntity) {
        viewModelScope.launch {
            repository.cacheNotification(notification)
        }
    }
}

class MySchoolViewModelFactory(private val repository: MySchoolRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MySchoolViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MySchoolViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
