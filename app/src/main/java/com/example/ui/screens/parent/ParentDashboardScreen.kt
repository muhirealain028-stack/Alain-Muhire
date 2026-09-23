package com.example.ui.screens.parent

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Announcement
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
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
import com.example.data.local.entities.AssignmentEntity
import com.example.data.local.entities.CachedSchoolNotificationEntity
import com.example.data.local.entities.CachedStudentGradeEntity
import com.example.data.local.entities.ConversationEntity
import com.example.data.local.entities.ParentEntity
import com.example.data.local.entities.TeacherFeedbackEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import com.example.data.repository.ChildOverview
import com.example.data.repository.MarkWithAssessment
import com.example.data.repository.SubjectPerformance
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.OfflineCacheDialog
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
fun ParentDashboardScreen(
    parentUser: UserEntity,
    parentRecord: ParentEntity,
    children: List<ChildOverview>,
    selectedChildIndex: Int,
    onSelectChild: (Int) -> Unit,
    childSubjectPerformances: List<SubjectPerformance>,
    childRecentMarks: List<MarkWithAssessment>,
    childAssignments: List<AssignmentEntity>,
    childFeedbacks: List<TeacherFeedbackEntity>,
    conversations: List<ConversationEntity>,
    announcements: List<AnnouncementEntity>,
    unreadNotifications: Int,
    cachedGrades: List<CachedStudentGradeEntity> = emptyList(),
    offlineNotifications: List<CachedSchoolNotificationEntity> = emptyList(),
    onRefreshCache: () -> Unit = {},
    onMarkOfflineNotificationRead: (Long) -> Unit = {},
    onMarkAllOfflineNotificationsRead: () -> Unit = {},
    onOpenChat: (convId: Long?) -> Unit,
    onStartNewChatWithTeacher: () -> Unit,
    onLogout: () -> Unit
) {
    var selectedBottomNav by remember { mutableIntStateOf(0) } // 0=Overview, 1=Marks, 2=Messages, 3=Announcements
    var showOfflineDialog by remember { mutableStateOf(false) }

    val currentChild = children.getOrNull(selectedChildIndex)

    Scaffold(
        topBar = {
            SchoolTopBar(
                title = "Parent Portal",
                subtitle = parentUser.fullName,
                userRole = UserRole.PARENT,
                userName = parentUser.fullName,
                unreadNotificationCount = if (unreadNotifications > 0) unreadNotifications else offlineNotifications.count { !it.isRead },
                onNotificationsClick = { showOfflineDialog = true },
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
                    icon = { Icon(Icons.Default.FamilyRestroom, contentDescription = "Overview") },
                    label = { Text("Overview") }
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 1,
                    onClick = { selectedBottomNav = 1 },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Marks") },
                    label = { Text("Marks") }
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 2,
                    onClick = { selectedBottomNav = 2 },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Messages") },
                    label = { Text("Messages") }
                )
                NavigationBarItem(
                    selected = selectedBottomNav == 3,
                    onClick = { selectedBottomNav = 3 },
                    icon = { Icon(Icons.Default.Announcement, contentDescription = "Announcements") },
                    label = { Text("School News") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Child Selector Pills / Tabs
            if (children.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                        Text(
                            text = "Linked Children (${children.size})",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            children.forEachIndexed { index, child ->
                                FilterChip(
                                    selected = index == selectedChildIndex,
                                    onClick = { onSelectChild(index) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Face,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = "${child.user.fullName} (${child.schoolClass?.name ?: "Class"})",
                                            fontWeight = if (index == selectedChildIndex) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    modifier = Modifier.testTag("child_chip_$index")
                                )
                            }
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedBottomNav) {
                    0 -> ParentOverviewTab(
                        child = currentChild,
                        childSubjectPerformances = childSubjectPerformances,
                        childRecentMarks = childRecentMarks,
                        childAssignments = childAssignments,
                        childFeedbacks = childFeedbacks,
                        onOpenChat = onOpenChat
                    )
                    1 -> ParentMarksDetailTab(
                        child = currentChild,
                        performances = childSubjectPerformances,
                        recentMarks = childRecentMarks
                    )
                    2 -> ParentMessagesTab(
                        conversations = conversations,
                        onOpenChat = onOpenChat,
                        onNewChat = onStartNewChatWithTeacher
                    )
                    3 -> ParentAnnouncementsTab(announcements = announcements)
                }
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
            initialTab = 0
        )
    }
}

@Composable
fun ParentOverviewTab(
    child: ChildOverview?,
    childSubjectPerformances: List<SubjectPerformance>,
    childRecentMarks: List<MarkWithAssessment>,
    childAssignments: List<AssignmentEntity>,
    childFeedbacks: List<TeacherFeedbackEntity>,
    onOpenChat: (convId: Long?) -> Unit
) {
    if (child == null) {
        EmptyStateCard(message = "No linked children found for this account.")
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Child Summary Card
        item {
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
                                text = child.user.fullName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Reg: ${child.student.registrationNumber} • ${child.schoolClass?.name ?: "S4 MCB"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        TrendBadge(trend = child.trend)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onOpenChat(1L) },
                            colors = ButtonDefaults.buttonColors(containerColor = RwandaSunYellow),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_contact_child_teacher")
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = null, tint = NavyDark, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Chat with Teachers", color = NavyDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Metrics Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard(
                    title = "Overall Average",
                    value = "${String.format("%.1f", child.overallAverage)}%",
                    subtitle = "Term 2 Marks",
                    icon = Icons.Default.Assessment,
                    iconTint = RwandaGreen,
                    modifier = Modifier.weight(1f)
                )
                StatMetricCard(
                    title = "Attendance",
                    value = "${String.format("%.1f", child.attendancePercentage)}%",
                    subtitle = "Present in school",
                    icon = Icons.Default.CheckCircle,
                    iconTint = RwandaBlue,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Subject Highlights
        item {
            Text(
                text = "Subject Highlights",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(childSubjectPerformances) { perf ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    ScoreProgressBar(
                        score = perf.averageScore,
                        maxScore = 100.0,
                        label = perf.subject.name
                    )
                }
            }
        }

        // Teacher Feedback Highlights
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Recent Teacher Remarks",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (childFeedbacks.isEmpty()) {
            item {
                EmptyStateCard(message = "No teacher feedback recorded yet for ${child.user.fullName}.")
            }
        } else {
            items(childFeedbacks) { fb ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Category: ${fb.category}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = RwandaAmber
                            )
                            Text(
                                text = fb.date,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "\"${fb.feedbackText}\"",
                            style = MaterialTheme.typography.bodySmall
                        )
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
fun ParentMarksDetailTab(
    child: ChildOverview?,
    performances: List<SubjectPerformance>,
    recentMarks: List<MarkWithAssessment>
) {
    if (child == null) return

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Academic Marks Breakdown",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Showing continuous assessments (CATs) and exam marks for ${child.user.fullName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(recentMarks) { item ->
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
                                text = item.assessment.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${item.subject?.name ?: "Subject"} • ${item.assessment.date}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Surface(
                            color = RwandaGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "${item.mark.studentMark.toInt()} / ${item.assessment.maxMark.toInt()}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = RwandaGreen,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                    if (item.mark.feedback.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Teacher note: \"${item.mark.feedback}\"",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParentMessagesTab(
    conversations: List<ConversationEntity>,
    onOpenChat: (convId: Long?) -> Unit,
    onNewChat: () -> Unit
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
                        text = "Teacher Communications",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Direct, secure messaging with subject teachers",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                Button(
                    onClick = onNewChat,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("New Message", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
        }

        if (conversations.isEmpty()) {
            item {
                EmptyStateCard(
                    message = "No active conversations with teachers yet.",
                    actionText = "Start a conversation with a teacher",
                    onAction = onNewChat
                )
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
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(RwandaAmber.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = null,
                                tint = RwandaAmber,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Teacher Consultation (Biology & Chemistry)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = conv.lastMessagePreview,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ParentAnnouncementsTab(announcements: List<AnnouncementEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Official School Announcements",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Circulated by secondary school administration",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(announcements) { news ->
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
                            text = news.title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (news.priority == "URGENT") {
                            Surface(
                                color = Color(0xFFEF4444).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "URGENT",
                                    color = Color(0xFFEF4444),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = news.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "From: ${news.authorName} • ${news.date}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}
