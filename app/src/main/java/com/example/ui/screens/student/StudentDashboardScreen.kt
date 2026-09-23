package com.example.ui.screens.student

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AssignmentEntity
import com.example.data.local.entities.CachedSchoolNotificationEntity
import com.example.data.local.entities.CachedStudentGradeEntity
import com.example.data.local.entities.LearningMaterialEntity
import com.example.data.local.entities.MaterialContentType
import com.example.data.local.entities.QuizEntity
import com.example.data.local.entities.SchoolClassEntity
import com.example.data.local.entities.StudentEntity
import com.example.data.local.entities.SubmissionEntity
import com.example.data.local.entities.TeacherFeedbackEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.data.repository.MarkWithAssessment
import com.example.data.repository.PerformanceTrend
import com.example.data.repository.SubjectPerformance
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.OfflineCacheDialog
import com.example.ui.components.OfflineCacheStatusBar
import com.example.ui.components.RwandaCurriculumTag
import com.example.ui.components.SchoolTopBar
import com.example.ui.components.ScoreProgressBar
import com.example.ui.components.StatMetricCard
import com.example.ui.components.TrendBadge
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RwandaAmber
import com.example.ui.theme.RwandaBlue
import com.example.ui.theme.RwandaGreen
import com.example.ui.theme.RwandaSunYellow

@Composable
fun StudentDashboardScreen(
    studentUser: UserEntity,
    studentRecord: StudentEntity,
    schoolClass: SchoolClassEntity?,
    overallAverage: Double,
    attendanceRate: Double,
    trend: PerformanceTrend,
    subjectPerformances: List<SubjectPerformance>,
    recentMarks: List<MarkWithAssessment>,
    assignments: List<AssignmentEntity>,
    submissions: List<SubmissionEntity>,
    materials: List<LearningMaterialEntity>,
    quizzes: List<QuizEntity>,
    feedbacks: List<TeacherFeedbackEntity>,
    unreadNotifications: Int,
    cachedGrades: List<CachedStudentGradeEntity> = emptyList(),
    offlineNotifications: List<CachedSchoolNotificationEntity> = emptyList(),
    onRefreshCache: () -> Unit = {},
    onMarkOfflineNotificationRead: (Long) -> Unit = {},
    onMarkAllOfflineNotificationsRead: () -> Unit = {},
    onTakeQuiz: (QuizEntity) -> Unit,
    onSubmitAssignment: (assignmentId: Long, content: String) -> Unit,
    onOpenChatWithTeacher: (teacherName: String) -> Unit,
    onOpenAITutor: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedNavTab by remember { mutableIntStateOf(0) } // 0=Dashboard, 1=Subjects, 2=Assignments, 3=Quizzes, 4=Feedback
    var showOfflineDialog by remember { mutableStateOf(false) }
    var offlineDialogInitialTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            SchoolTopBar(
                title = "MySchool Connect",
                subtitle = "${studentUser.fullName} • ${schoolClass?.name ?: "S4 MCB"}",
                userRole = UserRole.STUDENT,
                userName = studentUser.fullName,
                unreadNotificationCount = if (unreadNotifications > 0) unreadNotifications else offlineNotifications.count { !it.isRead },
                onNotificationsClick = {
                    offlineDialogInitialTab = 0
                    showOfflineDialog = true
                },
                onLogoutClick = onLogout
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = selectedNavTab == 0,
                    onClick = { selectedNavTab = 0 },
                    icon = { Icon(Icons.Default.School, contentDescription = "Dashboard") },
                    label = { Text("Overview") }
                )
                NavigationBarItem(
                    selected = selectedNavTab == 1,
                    onClick = { selectedNavTab = 1 },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Materials") },
                    label = { Text("Lessons") }
                )
                NavigationBarItem(
                    selected = selectedNavTab == 2,
                    onClick = { selectedNavTab = 2 },
                    icon = { Icon(Icons.Default.Assignment, contentDescription = "Tasks") },
                    label = { Text("Tasks") }
                )
                NavigationBarItem(
                    selected = selectedNavTab == 3,
                    onClick = { selectedNavTab = 3 },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = "Quizzes") },
                    label = { Text("Quizzes") }
                )
                NavigationBarItem(
                    selected = selectedNavTab == 4,
                    onClick = { selectedNavTab = 4 },
                    icon = { Icon(Icons.Default.Star, contentDescription = "Feedback") },
                    label = { Text("Feedback") }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedNavTab) {
                0 -> StudentOverviewTab(
                    studentUser = studentUser,
                    schoolClass = schoolClass,
                    overallAverage = overallAverage,
                    attendanceRate = attendanceRate,
                    trend = trend,
                    subjectPerformances = subjectPerformances,
                    recentMarks = recentMarks,
                    assignments = assignments,
                    submissions = submissions,
                    feedbacks = feedbacks,
                    cachedGrades = cachedGrades,
                    offlineNotifications = offlineNotifications,
                    onOpenOfflineManager = {
                        offlineDialogInitialTab = 1
                        showOfflineDialog = true
                    },
                    onRefreshCache = onRefreshCache,
                    onOpenAITutor = onOpenAITutor,
                    onOpenLessons = { selectedNavTab = 1 },
                    onOpenTasks = { selectedNavTab = 2 }
                )
                1 -> StudentLessonsTab(materials = materials)
                2 -> StudentTasksTab(
                    assignments = assignments,
                    submissions = submissions,
                    onSubmitAssignment = onSubmitAssignment
                )
                3 -> StudentQuizzesTab(
                    quizzes = quizzes,
                    onTakeQuiz = onTakeQuiz
                )
                4 -> StudentFeedbackTab(
                    feedbacks = feedbacks,
                    onOpenChat = onOpenChatWithTeacher
                )
            }
        }
    }

    if (showOfflineDialog) {
        OfflineCacheDialog(
            cachedGrades = cachedGrades,
            offlineNotifications = offlineNotifications,
            onDismiss = { showOfflineDialog = false },
            onRefreshCache = onRefreshCache,
            onMarkNotificationRead = onMarkOfflineNotificationRead,
            onMarkAllNotificationsRead = onMarkAllOfflineNotificationsRead,
            initialTab = offlineDialogInitialTab
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudentOverviewTab(
    studentUser: UserEntity,
    schoolClass: SchoolClassEntity?,
    overallAverage: Double,
    attendanceRate: Double,
    trend: PerformanceTrend,
    subjectPerformances: List<SubjectPerformance>,
    recentMarks: List<MarkWithAssessment>,
    assignments: List<AssignmentEntity>,
    submissions: List<SubmissionEntity>,
    feedbacks: List<TeacherFeedbackEntity>,
    cachedGrades: List<CachedStudentGradeEntity> = emptyList(),
    offlineNotifications: List<CachedSchoolNotificationEntity> = emptyList(),
    onOpenOfflineManager: () -> Unit = {},
    onRefreshCache: () -> Unit = {},
    onOpenAITutor: () -> Unit,
    onOpenLessons: () -> Unit,
    onOpenTasks: () -> Unit
) {
    val submittedAssignmentIds = submissions.map { it.assignmentId }.toSet()
    val pendingAssignmentsCount = assignments.count { it.id !in submittedAssignmentIds }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Welcome Card
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Muraho, ${studentUser.fullName}!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Class: ${schoolClass?.name ?: "Senior 4 MCB"} • Term 2 (2025-2026)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        TrendBadge(trend = trend)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    RwandaCurriculumTag()

                    Spacer(modifier = Modifier.height(14.dp))

                    // AI Tutor Action Button
                    Surface(
                        color = RwandaBlue.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenAITutor() }
                            .testTag("btn_open_ai_tutor")
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(RwandaSunYellow),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NavyDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "CBC AI Study Companion",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Get explanations & revision help for Biology, Chemistry, Math & Physics",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Send,
                                contentDescription = null,
                                tint = RwandaSunYellow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Room Offline Data Cache Banner
        item {
            OfflineCacheStatusBar(
                cachedGradesCount = cachedGrades.size,
                offlineNotificationsCount = offlineNotifications.size,
                onOpenOfflineManager = onOpenOfflineManager,
                onRefreshCache = onRefreshCache
            )
        }

        // --- Metric Cards Grid ---
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Overall Average",
                    value = "${String.format("%.1f", overallAverage)}%",
                    subtitle = "Term 2 Marks",
                    icon = Icons.Default.Assessment,
                    iconTint = RwandaGreen,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Attendance",
                    value = "${String.format("%.1f", attendanceRate)}%",
                    subtitle = "Present in class",
                    icon = Icons.Default.CheckCircle,
                    iconTint = RwandaBlue,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Pending Tasks",
                    value = "$pendingAssignmentsCount",
                    subtitle = "Assignments due",
                    icon = Icons.Default.PendingActions,
                    iconTint = RwandaAmber,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Feedback",
                    value = "${feedbacks.size}",
                    subtitle = "Teacher reviews",
                    icon = Icons.Default.Star,
                    iconTint = RwandaSunYellow,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // --- Subject Performance Overview ---
        item {
            Text(
                text = "Subject Performance",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(subjectPerformances) { perf ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = perf.subject.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${perf.assessmentCount} Assessments",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ScoreProgressBar(
                        score = perf.averageScore,
                        maxScore = 100.0,
                        label = "Subject Average"
                    )
                }
            }
        }

        // --- Recent Marks ---
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Recent Marks & Assessment Feedback",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        if (recentMarks.isEmpty()) {
            item {
                EmptyStateCard(message = "No marks published yet for this term.")
            }
        } else {
            items(recentMarks.take(4)) { markItem ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = markItem.assessment.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${markItem.subject?.name ?: "Subject"} • ${markItem.assessment.date}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Surface(
                                color = RwandaGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${markItem.mark.studentMark.toInt()} / ${markItem.assessment.maxMark.toInt()}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = RwandaGreen,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        if (markItem.mark.feedback.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Teacher Feedback: \"${markItem.mark.feedback}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StudentLessonsTab(materials: List<LearningMaterialEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Class Lessons & Learning Materials",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Rwanda CBC Secondary 4 MCB • Term 2 Notes, PDFs & Links",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (materials.isEmpty()) {
            item {
                EmptyStateCard(message = "No learning materials uploaded yet for this class.")
            }
        } else {
            items(materials) { item ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = when (item.contentType) {
                                    MaterialContentType.PDF -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                    MaterialContentType.NOTE -> RwandaBlue.copy(alpha = 0.15f)
                                    MaterialContentType.VIDEO -> RwandaAmber.copy(alpha = 0.15f)
                                    MaterialContentType.LINK -> RwandaGreen.copy(alpha = 0.15f)
                                },
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = item.contentType.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = item.topic,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Resource URL: ${item.contentUrl}",
                            style = MaterialTheme.typography.labelSmall,
                            color = RwandaBlue
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentTasksTab(
    assignments: List<AssignmentEntity>,
    submissions: List<SubmissionEntity>,
    onSubmitAssignment: (assignmentId: Long, content: String) -> Unit
) {
    val submissionMap = submissions.associateBy { it.assignmentId }
    var selectedAssignmentForSubmit by remember { mutableStateOf<AssignmentEntity?>(null) }
    var submissionText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Assignments & Coursework",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Submit homework and track grading status",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (assignments.isEmpty()) {
            item {
                EmptyStateCard(message = "No assignments assigned currently.")
            }
        } else {
            items(assignments) { task ->
                val submission = submissionMap[task.id]
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = if (submission != null) RwandaGreen.copy(alpha = 0.15f) else RwandaAmber.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (submission != null) "Submitted" else "Due ${task.dueDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (submission != null) RwandaGreen else RwandaAmber,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (submission != null) {
                            Text(
                                text = "Your Submission: \"${submission.contentText}\"",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                            if (submission.teacherRemarks.isNotBlank()) {
                                Text(
                                    text = "Remarks: ${submission.teacherRemarks}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RwandaGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    selectedAssignmentForSubmit = task
                                    submissionText = ""
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                                modifier = Modifier.testTag("btn_submit_task_${task.id}")
                            ) {
                                Text("Submit Homework")
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedAssignmentForSubmit != null) {
        val task = selectedAssignmentForSubmit!!
        AlertDialog(
            onDismissRequest = { selectedAssignmentForSubmit = null },
            title = { Text("Submit: ${task.title}") },
            text = {
                Column {
                    Text(
                        text = "Enter your work or summary to submit to your teacher:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = submissionText,
                        onValueChange = { submissionText = it },
                        label = { Text("Submission Content") },
                        minLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (submissionText.isNotBlank()) {
                            onSubmitAssignment(task.id, submissionText)
                            selectedAssignmentForSubmit = null
                        }
                    }
                ) {
                    Text("Confirm Submit")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedAssignmentForSubmit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StudentQuizzesTab(
    quizzes: List<QuizEntity>,
    onTakeQuiz: (QuizEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Interactive Quizzes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Test your knowledge on Rwanda CBC secondary topics",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (quizzes.isEmpty()) {
            item {
                EmptyStateCard(message = "No quizzes active currently.")
            }
        } else {
            items(quizzes) { quiz ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = quiz.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                color = RwandaBlue.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${quiz.durationMinutes} Mins • ${quiz.totalMarks} Marks",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RwandaBlue,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = quiz.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { onTakeQuiz(quiz) },
                            colors = ButtonDefaults.buttonColors(containerColor = RwandaGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_take_quiz_${quiz.id}")
                        ) {
                            Text("Start Quiz Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudentFeedbackTab(
    feedbacks: List<TeacherFeedbackEntity>,
    onOpenChat: (teacherName: String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Teacher Feedback & Reviews",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Continuous assessments and mentoring observations",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        if (feedbacks.isEmpty()) {
            item {
                EmptyStateCard(message = "No teacher feedback recorded yet.")
            }
        } else {
            items(feedbacks) { fb ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = RwandaAmber.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = fb.category,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = RwandaAmber,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = fb.date,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "\"${fb.feedbackText}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
