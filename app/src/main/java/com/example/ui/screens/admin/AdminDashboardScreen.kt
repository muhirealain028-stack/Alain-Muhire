package com.example.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.AnnouncementEntity
import com.example.data.local.entities.ParentEntity
import com.example.data.local.entities.SchoolClassEntity
import com.example.data.local.entities.StudentEntity
import com.example.data.local.entities.SubjectEntity
import com.example.data.local.entities.TeacherEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.ui.components.SchoolTopBar
import com.example.ui.components.StatMetricCard
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RwandaAmber
import com.example.ui.theme.RwandaBlue
import com.example.ui.theme.RwandaGreen
import com.example.ui.theme.RwandaSunYellow

@Composable
fun AdminDashboardScreen(
    adminUser: UserEntity,
    totalStudents: Int,
    totalParents: Int,
    totalTeachers: Int,
    totalClasses: Int,
    classesList: List<SchoolClassEntity>,
    subjectsList: List<SubjectEntity>,
    usersList: List<UserEntity>,
    announcements: List<AnnouncementEntity>,
    unreadNotifications: Int,
    onCreateAnnouncement: (title: String, content: String, role: String, priority: String) -> Unit,
    onAddClass: (name: String, grade: String) -> Unit,
    onAddSubject: (name: String, code: String) -> Unit,
    onLogout: () -> Unit
) {
    var selectedBottomNav by remember { mutableIntStateOf(0) } // 0=Overview, 1=Users, 2=Academics, 3=Announcements
    var showAddAnnouncementDialog by remember { mutableStateOf(false) }
    var showAddClassDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SchoolTopBar(
                title = "School Administration",
                subtitle = "${adminUser.fullName} • Headmaster",
                userRole = UserRole.ADMIN,
                userName = adminUser.fullName,
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
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Overview") },
                    label = { Text("Overview") }
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 1,
                    onClick = { selectedBottomNav = 1 },
                    icon = { Icon(Icons.Default.Groups, contentDescription = "Users") },
                    label = { Text("Directory") }
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 2,
                    onClick = { selectedBottomNav = 2 },
                    icon = { Icon(Icons.Default.Class, contentDescription = "Academics") },
                    label = { Text("Classes") }
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 3,
                    onClick = { selectedBottomNav = 3 },
                    icon = { Icon(Icons.Default.Announcement, contentDescription = "Circulars") },
                    label = { Text("Circulars") }
                )
            }
        },
        floatingActionButton = {
            if (selectedBottomNav == 3) {
                FloatingActionButton(
                    onClick = { showAddAnnouncementDialog = true },
                    containerColor = NavyPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_announcement")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Circular")
                }
            } else if (selectedBottomNav == 2) {
                FloatingActionButton(
                    onClick = { showAddClassDialog = true },
                    containerColor = RwandaBlue,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_class")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Class")
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
                0 -> AdminOverviewTab(
                    adminUser = adminUser,
                    totalStudents = totalStudents,
                    totalParents = totalParents,
                    totalTeachers = totalTeachers,
                    totalClasses = totalClasses,
                    classesList = classesList,
                    onOpenUsers = { selectedBottomNav = 1 },
                    onOpenAcademics = { selectedBottomNav = 2 },
                    onOpenCirculars = { selectedBottomNav = 3 }
                )
                1 -> AdminUsersDirectoryTab(users = usersList)
                2 -> AdminAcademicsTab(classes = classesList, subjects = subjectsList, onAddClass = { showAddClassDialog = true })
                3 -> AdminAnnouncementsTab(announcements = announcements, onAddCircular = { showAddAnnouncementDialog = true })
            }
        }
    }

    if (showAddAnnouncementDialog) {
        AddAnnouncementDialog(
            onDismiss = { showAddAnnouncementDialog = false },
            onConfirm = { title, content, target, priority ->
                onCreateAnnouncement(title, content, target, priority)
                showAddAnnouncementDialog = false
            }
        )
    }

    if (showAddClassDialog) {
        AddClassDialog(
            onDismiss = { showAddClassDialog = false },
            onConfirm = { name, grade ->
                onAddClass(name, grade)
                showAddClassDialog = false
            }
        )
    }
}

@Composable
fun AdminOverviewTab(
    adminUser: UserEntity,
    totalStudents: Int,
    totalParents: Int,
    totalTeachers: Int,
    totalClasses: Int,
    classesList: List<SchoolClassEntity>,
    onOpenUsers: () -> Unit,
    onOpenAcademics: () -> Unit,
    onOpenCirculars: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Secondary School Executive Oversight",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Academic Year 2025-2026 • Term 2 Rwanda Secondary System",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenCirculars,
                            colors = ButtonDefaults.buttonColors(containerColor = RwandaSunYellow),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("New Circular", color = NavyDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        OutlinedButton(
                            onClick = onOpenUsers,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("User Directory", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                    title = "Total Students",
                    value = "$totalStudents",
                    subtitle = "Enrolled",
                    icon = Icons.Default.School,
                    iconTint = RwandaBlue,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Linked Parents",
                    value = "$totalParents",
                    subtitle = "Active guardians",
                    icon = Icons.Default.FamilyRestroom,
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
                    title = "Faculty Teachers",
                    value = "$totalTeachers",
                    subtitle = "Assigned subjects",
                    icon = Icons.Default.Person,
                    iconTint = RwandaAmber,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Active Classes",
                    value = "$totalClasses",
                    subtitle = "O-Level & A-Level",
                    icon = Icons.Default.Class,
                    iconTint = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Academic performance & attendance oversight
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "School-Wide Performance Snapshot",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Overall School Average:", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "83.6%", fontWeight = FontWeight.Bold, color = RwandaGreen)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Student Attendance Rate:", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "96.2%", fontWeight = FontWeight.Bold, color = RwandaBlue)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Parent Portal Engagement:", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "91.8%", fontWeight = FontWeight.Bold, color = RwandaAmber)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminUsersDirectoryTab(users: List<UserEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Registered School Community",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Secure role-based directory with strict access control",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(users) { user ->
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
                            .background(
                                when (user.role) {
                                    UserRole.STUDENT -> RwandaBlue.copy(alpha = 0.15f)
                                    UserRole.PARENT -> RwandaGreen.copy(alpha = 0.15f)
                                    UserRole.TEACHER -> RwandaAmber.copy(alpha = 0.15f)
                                    UserRole.ADMIN -> Color(0xFF7C3AED).copy(alpha = 0.15f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (user.role) {
                                UserRole.STUDENT -> Icons.Default.School
                                UserRole.PARENT -> Icons.Default.FamilyRestroom
                                UserRole.TEACHER -> Icons.Default.Person
                                UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                            },
                            contentDescription = null,
                            tint = when (user.role) {
                                UserRole.STUDENT -> RwandaBlue
                                UserRole.PARENT -> RwandaGreen
                                UserRole.TEACHER -> RwandaAmber
                                UserRole.ADMIN -> Color(0xFF7C3AED)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = user.fullName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                    Surface(
                        color = NavyDark.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = user.role.name,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAcademicsTab(
    classes: List<SchoolClassEntity>,
    subjects: List<SubjectEntity>,
    onAddClass: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Classes & Secondary Combinations",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(classes) { cls ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Class, contentDescription = null, tint = RwandaBlue)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = cls.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = "Grade: ${cls.gradeLevel} • Academic Year 2025-2026", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Curriculum Subjects (${subjects.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(subjects) { sub ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, tint = RwandaGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = sub.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(text = "Subject Code: ${sub.code}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAnnouncementsTab(
    announcements: List<AnnouncementEntity>,
    onAddCircular: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Published School Circulars",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(announcements) { ann ->
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
                        Text(text = ann.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Surface(
                            color = if (ann.priority == "URGENT") Color(0xFFEF4444).copy(alpha = 0.15f) else RwandaBlue.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = ann.priority,
                                color = if (ann.priority == "URGENT") Color(0xFFEF4444) else RwandaBlue,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = ann.content, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = "Audience: ${ann.targetRole} • Date: ${ann.date}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
fun AddAnnouncementDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, target: String, priority: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var targetRole by remember { mutableStateOf("ALL") }
    var priority by remember { mutableStateOf("NORMAL") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Publish School Circular") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Circular Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Announcement Body") }, minLines = 3, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onConfirm(title, content, targetRole, priority)
                    }
                }
            ) {
                Text("Broadcast Circular")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AddClassDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, grade: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("S4") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Secondary Class") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Class Name (e.g. Senior 4 MEG)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = grade, onValueChange = { grade = it }, label = { Text("Grade Level (S1 - S6)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, grade)
                    }
                }
            ) {
                Text("Create Class")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
