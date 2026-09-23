package com.example.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AssessmentEntity
import com.example.data.local.entities.AssignmentEntity
import com.example.data.local.entities.ConversationEntity
import com.example.data.local.entities.LearningMaterialEntity
import com.example.data.local.entities.MaterialContentType
import com.example.data.local.entities.SchoolClassEntity
import com.example.data.local.entities.StudentEntity
import com.example.data.local.entities.SubmissionEntity
import com.example.data.local.entities.TeacherEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.data.repository.MarkWithAssessment
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.SchoolTopBar
import com.example.ui.components.StatMetricCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RwandaAmber
import com.example.ui.theme.RwandaBlue
import com.example.ui.theme.RwandaGreen
import com.example.ui.theme.RwandaSunYellow

@Composable
fun TeacherDashboardScreen(
    teacherUser: UserEntity,
    teacherRecord: TeacherEntity,
    assignedClasses: List<SchoolClassEntity>,
    studentsInClasses: List<Pair<StudentEntity, UserEntity>>,
    recentAssessments: List<AssessmentEntity>,
    recentSubmissions: List<SubmissionEntity>,
    materials: List<LearningMaterialEntity>,
    conversations: List<ConversationEntity>,
    unreadNotifications: Int,
    onEnterMark: (assessmentId: Long, studentId: Long, mark: Double, feedback: String) -> Unit,
    onCreateAssignment: (title: String, desc: String, classId: Long, dueDate: String, maxScore: Int) -> Unit,
    onUploadMaterial: (title: String, desc: String, classId: Long, topic: String, type: MaterialContentType, url: String) -> Unit,
    onOpenChat: (convId: Long?) -> Unit,
    onLogout: () -> Unit
) {
    var selectedBottomNav by remember { mutableIntStateOf(0) } // 0=Overview, 1=Marks Entry, 2=Materials & Tasks, 3=Messages
    var showEnterMarkDialog by remember { mutableStateOf(false) }
    var showCreateAssignmentDialog by remember { mutableStateOf(false) }
    var showUploadMaterialDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SchoolTopBar(
                title = "Teacher Portal",
                subtitle = "${teacherUser.fullName} • ${teacherRecord.department}",
                userRole = UserRole.TEACHER,
                userName = teacherUser.fullName,
                unreadNotificationCount = unreadNotifications,
                onLogoutClick = onLogout
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = selectedBottomNav == 0,
                    onClick = { selectedBottomNav = 0 },
                    icon = { Icon(Icons.Default.Class, contentDescription = "Overview") },
                    label = { Text("Overview") }
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 1,
                    onClick = { selectedBottomNav = 1 },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Enter Marks") },
                    label = { Text("Enter Marks") }
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 2,
                    onClick = { selectedBottomNav = 2 },
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = "Materials") },
                    label = { Text("Curriculum") }
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 3,
                    onClick = { selectedBottomNav = 3 },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Parents") },
                    label = { Text("Parents") }
                )
            }
        },
        floatingActionButton = {
            if (selectedBottomNav == 1) {
                FloatingActionButton(
                    onClick = { showEnterMarkDialog = true },
                    containerColor = RwandaGreen,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_enter_mark")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Enter Mark")
                }
            } else if (selectedBottomNav == 2) {
                FloatingActionButton(
                    onClick = { showUploadMaterialDialog = true },
                    containerColor = RwandaBlue,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_upload_material")
                ) {
                    Icon(Icons.Default.Upload, contentDescription = "Upload Lesson")
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (selectedBottomNav) {
                0 -> TeacherOverviewTab(
                    teacherUser = teacherUser,
                    assignedClasses = assignedClasses,
                    studentsCount = studentsInClasses.size,
                    assessmentsCount = recentAssessments.size,
                    submissionsCount = recentSubmissions.size,
                    onOpenMarksEntry = { selectedBottomNav = 1 },
                    onCreateAssignment = { showCreateAssignmentDialog = true },
                    onUploadMaterial = { showUploadMaterialDialog = true }
                )
                1 -> TeacherMarksEntryTab(
                    assessments = recentAssessments,
                    students = studentsInClasses,
                    onOpenDialog = { showEnterMarkDialog = true }
                )
                2 -> TeacherCurriculumTab(
                    materials = materials,
                    onCreateTask = { showCreateAssignmentDialog = true },
                    onUploadDoc = { showUploadMaterialDialog = true }
                )
                3 -> TeacherMessagesTab(
                    conversations = conversations,
                    onOpenChat = onOpenChat
                )
            }
        }
    }

    // --- Dialogs ---
    if (showEnterMarkDialog) {
        EnterMarkDialog(
            assessments = recentAssessments,
            students = studentsInClasses,
            onDismiss = { showEnterMarkDialog = false },
            onConfirm = { assId, studId, mark, feedback ->
                onEnterMark(assId, studId, mark, feedback)
                showEnterMarkDialog = false
            }
        )
    }

    if (showCreateAssignmentDialog) {
        CreateAssignmentDialog(
            classes = assignedClasses,
            onDismiss = { showCreateAssignmentDialog = false },
            onConfirm = { title, desc, clsId, date, maxMark ->
                onCreateAssignment(title, desc, clsId, date, maxMark)
                showCreateAssignmentDialog = false
            }
        )
    }

    if (showUploadMaterialDialog) {
        UploadMaterialDialog(
            classes = assignedClasses,
            onDismiss = { showUploadMaterialDialog = false },
            onConfirm = { title, desc, clsId, topic, type, url ->
                onUploadMaterial(title, desc, clsId, topic, type, url)
                showUploadMaterialDialog = false
            }
        )
    }
}

@Composable
fun TeacherOverviewTab(
    teacherUser: UserEntity,
    assignedClasses: List<SchoolClassEntity>,
    studentsCount: Int,
    assessmentsCount: Int,
    submissionsCount: Int,
    onOpenMarksEntry: () -> Unit,
    onCreateAssignment: () -> Unit,
    onUploadMaterial: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Mwiriwe, Teacher ${teacherUser.fullName}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Secondary Academic Management • Term 2 (2025-2026)",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenMarksEntry,
                            colors = ButtonDefaults.buttonColors(containerColor = RwandaSunYellow),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_quick_enter_marks")
                        ) {
                            Text("Enter Marks", color = NavyDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        OutlinedButton(
                            onClick = onCreateAssignment,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("New Task", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Assigned Classes",
                    value = "${assignedClasses.size}",
                    subtitle = "S4 MCB, S5 PCB",
                    icon = Icons.Default.Class,
                    iconTint = RwandaBlue,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Total Students",
                    value = "$studentsCount",
                    subtitle = "Across classes",
                    icon = Icons.Default.Groups,
                    iconTint = RwandaGreen,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Assessments",
                    value = "$assessmentsCount",
                    subtitle = "Active this term",
                    icon = Icons.Default.Assessment,
                    iconTint = RwandaAmber,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Submissions",
                    value = "$submissionsCount",
                    subtitle = "Pending grading",
                    icon = Icons.Default.Assignment,
                    iconTint = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Assigned Classes List
        item {
            Text(
                text = "My Assigned Classes & Cohorts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(assignedClasses) { cls ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(RwandaBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = RwandaBlue)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = cls.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = "Grade Level: ${cls.gradeLevel} • Academic Year 2025-2026", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@Composable
fun TeacherMarksEntryTab(
    assessments: List<AssessmentEntity>,
    students: List<Pair<StudentEntity, UserEntity>>,
    onOpenDialog: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Marks Entry & Gradebook",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Continuous assessments and exam scoring",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Button(
                    onClick = onOpenDialog,
                    colors = ButtonDefaults.buttonColors(containerColor = RwandaGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_record_marks")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Score Mark")
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(assessments) { ass ->
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
                        Text(text = ass.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Surface(
                            color = RwandaAmber.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Max: ${ass.maxMark.toInt()} Pts",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = RwandaAmber,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Assessment Type: ${ass.type.name} • Date: ${ass.date}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
fun TeacherCurriculumTab(
    materials: List<LearningMaterialEntity>,
    onCreateTask: () -> Unit,
    onUploadDoc: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Curriculum Materials & Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Organized by Year → Term → Class → Topic",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onUploadDoc,
                        colors = ButtonDefaults.buttonColors(containerColor = RwandaBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Upload", fontSize = 12.sp)
                    }
                    Button(
                        onClick = onCreateTask,
                        colors = ButtonDefaults.buttonColors(containerColor = RwandaAmber),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Task", fontSize = 12.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(materials) { mat ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = mat.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(text = "Topic: ${mat.topic} • Format: ${mat.contentType.name}", style = MaterialTheme.typography.labelSmall, color = RwandaBlue)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = mat.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun TeacherMessagesTab(
    conversations: List<ConversationEntity>,
    onOpenChat: (convId: Long?) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Parent Communications",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Inquiries from parents of students in your assigned subjects",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (conversations.isEmpty()) {
            item {
                EmptyStateCard(message = "No parent messages yet.")
            }
        } else {
            items(conversations) { conv ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenChat(conv.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(RwandaGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = RwandaGreen)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Parent Inquiry: Mukamana Claire", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(text = conv.lastMessagePreview, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                        }
                    }
                }
            }
        }
    }
}

// --- Dialog Components ---
@Composable
fun EnterMarkDialog(
    assessments: List<AssessmentEntity>,
    students: List<Pair<StudentEntity, UserEntity>>,
    onDismiss: () -> Unit,
    onConfirm: (assessmentId: Long, studentId: Long, mark: Double, feedback: String) -> Unit
) {
    var selectedAssessmentId by remember { mutableStateOf(assessments.firstOrNull()?.id ?: 1L) }
    var selectedStudentId by remember { mutableStateOf(students.firstOrNull()?.first?.id ?: 1L) }
    var markText by remember { mutableStateOf("") }
    var feedbackText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Student Mark") },
        text = {
            Column {
                Text("Select Student:", style = MaterialTheme.typography.labelSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    students.forEach { (st, usr) ->
                        FilterChip(
                            selected = selectedStudentId == st.id,
                            onClick = { selectedStudentId = st.id },
                            label = { Text(usr.fullName) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = markText,
                    onValueChange = { markText = it },
                    label = { Text("Score (e.g. 88.0)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = { Text("Teacher Feedback / Remarks") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val markVal = markText.toDoubleOrNull() ?: 0.0
                    onConfirm(selectedAssessmentId, selectedStudentId, markVal, feedbackText)
                }
            ) {
                Text("Save Grade")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun CreateAssignmentDialog(
    classes: List<SchoolClassEntity>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, classId: Long, dueDate: String, maxScore: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("2026-04-05") }
    var maxScoreText by remember { mutableStateOf("100") }
    var selectedClassId by remember { mutableStateOf(classes.firstOrNull()?.id ?: 1L) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Assignment") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Assignment Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Instructions") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = dueDate, onValueChange = { dueDate = it }, label = { Text("Due Date (YYYY-MM-DD)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description, selectedClassId, dueDate, maxScoreText.toIntOrNull() ?: 100)
                    }
                }
            ) {
                Text("Publish Task")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun UploadMaterialDialog(
    classes: List<SchoolClassEntity>,
    onDismiss: () -> Unit,
    onConfirm: (title: String, desc: String, classId: Long, topic: String, type: MaterialContentType, url: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var topic by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(MaterialContentType.PDF) }
    var url by remember { mutableStateOf("") }
    var selectedClassId by remember { mutableStateOf(classes.firstOrNull()?.id ?: 1L) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Upload Learning Material") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Material Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = topic, onValueChange = { topic = it }, label = { Text("Curriculum Topic") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") }, minLines = 2, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = url, onValueChange = { url = it }, label = { Text("Document / Resource URL") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(title, description, selectedClassId, topic, selectedType, url)
                    }
                }
            ) {
                Text("Upload Resource")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
