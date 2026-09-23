package com.example.ui.screens.landing

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entities.UserRole
import com.example.ui.components.RwandaCurriculumTag
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RwandaAmber
import com.example.ui.theme.RwandaBlue
import com.example.ui.theme.RwandaGreen
import com.example.ui.theme.RwandaSunYellow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LandingScreen(
    onNavigateToLogin: () -> Unit,
    onQuickDemoLogin: (email: String, role: UserRole) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // --- Top Bar ---
        Surface(
            color = NavyPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(RwandaSunYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "MySchool Connect",
                            tint = NavyDark,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "MySchool Connect",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onNavigateToLogin,
                    colors = ButtonDefaults.buttonColors(containerColor = RwandaSunYellow),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("landing_signin_btn")
                ) {
                    Text(
                        text = "Sign In",
                        color = NavyDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- Hero Banner Section ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.hero_school_banner),
                contentDescription = "Students learning in Rwanda",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                NavyDark.copy(alpha = 0.5f),
                                NavyDark.copy(alpha = 0.92f)
                            )
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                RwandaCurriculumTag()
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Learn better. Stay connected. Support every student.",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Secondary school learning and parent-school communication platform designed for secondary education in Rwanda.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // --- Quick Demo Role Switcher ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Instant Demo Role Access",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Experience the real application instantly with one click as any role:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 2
            ) {
                DemoRoleCard(
                    title = "Student",
                    subtitle = "Keza Ines (S4 MCB)",
                    description = "Lessons, quizzes, marks & AI tutor",
                    color = RwandaBlue,
                    icon = Icons.Default.School,
                    modifier = Modifier.weight(1f),
                    onClick = { onQuickDemoLogin("keza.ines@student.myschool.rw", UserRole.STUDENT) }
                )
                DemoRoleCard(
                    title = "Parent",
                    subtitle = "Mukamana Claire",
                    description = "Multi-child marks, attendance & chat",
                    color = RwandaGreen,
                    icon = Icons.Default.FamilyRestroom,
                    modifier = Modifier.weight(1f),
                    onClick = { onQuickDemoLogin("claire.mukamana@parent.myschool.rw", UserRole.PARENT) }
                )
                DemoRoleCard(
                    title = "Teacher",
                    subtitle = "Murenzi J.P. (Sciences)",
                    description = "Enter marks, upload lessons & messaging",
                    color = RwandaAmber,
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f),
                    onClick = { onQuickDemoLogin("murenzi.jp@teacher.myschool.rw", UserRole.TEACHER) }
                )
                DemoRoleCard(
                    title = "Admin",
                    subtitle = "Dr. Gasana (Headmaster)",
                    description = "School oversight, classes & analytics",
                    color = Color(0xFF7C3AED),
                    icon = Icons.Default.Security,
                    modifier = Modifier.weight(1f),
                    onClick = { onQuickDemoLogin("headmaster@myschool.rw", UserRole.ADMIN) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- How It Works ---
        SectionContainer(title = "How It Works") {
            StepItem(
                step = "1",
                title = "School Enrollment & Setup",
                desc = "Administrators establish academic years, classes (O-Level & A-Level combinations), and assign qualified teachers."
            )
            StepItem(
                step = "2",
                title = "Parent-Child Secure Linking",
                desc = "Parents are verified and linked to their children, enabling real-time academic monitoring and direct teacher consultation."
            )
            StepItem(
                step = "3",
                title = "Continuous Learning & Tracking",
                desc = "Students access digital CBC lessons and submit assignments. Real-time notifications keep parents and teachers synchronized."
            )
        }

        // --- Role Features Breakdown ---
        SectionContainer(title = "Tailored Portals") {
            RoleFeatureCard(
                roleTitle = "For Students",
                color = RwandaBlue,
                items = listOf(
                    "Access Rwanda CBC notes, PDFs, and video lessons",
                    "Take interactive quizzes and get instant feedback",
                    "Submit assignments and view teacher remarks",
                    "Track subject averages and historical progress trends",
                    "Ask questions to the Rwanda Curriculum AI Tutor"
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            RoleFeatureCard(
                roleTitle = "For Parents",
                color = RwandaGreen,
                items = listOf(
                    "Multi-child switcher for tracking multiple siblings",
                    "Live notifications for newly posted test marks and homework",
                    "Direct and secure messaging with each child's teachers",
                    "Daily attendance logs (Present, Absent, Excused)",
                    "Academic trend alerts (Improving, Stable, Needs Support)"
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            RoleFeatureCard(
                roleTitle = "For Teachers",
                color = RwandaAmber,
                items = listOf(
                    "Rapid marks entry with automatic percentage calculations",
                    "Create class assignments, set deadlines & grade submissions",
                    "Upload organized materials by Year → Term → Class → Topic",
                    "Direct chat channel with parents of enrolled students",
                    "Publish announcements to assigned classes"
                )
            )
            Spacer(modifier = Modifier.height(10.dp))
            RoleFeatureCard(
                roleTitle = "For School Administrators",
                color = Color(0xFF7C3AED),
                items = listOf(
                    "Complete user directory: students, parents, teachers",
                    "Class & subject assignment management",
                    "School-wide academic performance averages & reports",
                    "Official school circulars & emergency announcements",
                    "Role-based access control and student privacy enforcement"
                )
            )
        }

        // --- Security & Privacy ---
        SectionContainer(title = "Security & Privacy") {
            Card(
                colors = CardDefaults.cardColors(containerColor = NavyDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Security",
                            tint = RwandaSunYellow,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Strict Role-Based Access Control",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Students access only their own records. Parents only view their verified linked children. Teachers access only their assigned classes. Private academic data is strictly protected.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // --- Contact / School Support ---
        SectionContainer(title = "Contact & School Support") {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    ContactRow(icon = Icons.Default.LocationOn, text = "Kigali Innovation Hub, Gasabo, Kigali, Rwanda")
                    Spacer(modifier = Modifier.height(8.dp))
                    ContactRow(icon = Icons.Default.Phone, text = "+250 788 123 456 (Rwanda Education Helpline)")
                    Spacer(modifier = Modifier.height(8.dp))
                    ContactRow(icon = Icons.Default.Email, text = "support@myschool.rw")
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun DemoRoleCard(
    title: String,
    subtitle: String,
    description: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .testTag("demo_role_${title.lowercase()}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(18.dp))
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
            Text(text = description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, maxLines = 1)
        }
    }
}

@Composable
fun SectionContainer(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(10.dp))
        content()
    }
}

@Composable
fun StepItem(step: String, title: String, desc: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(NavyPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = step, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun RoleFeatureCard(
    roleTitle: String,
    color: Color,
    items: List<String>
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = roleTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(8.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = RwandaGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = item, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun ContactRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = RwandaBlue, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = text, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
    }
}
