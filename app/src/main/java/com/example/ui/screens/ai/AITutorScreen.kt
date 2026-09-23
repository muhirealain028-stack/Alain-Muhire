package com.example.ui.screens.ai

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.ui.components.RwandaCurriculumTag
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RwandaBlue
import com.example.ui.theme.RwandaGreen
import com.example.ui.theme.RwandaSunYellow

data class TutorChatMessage(
    val isUser: Boolean,
    val text: String
)

@Composable
fun AITutorScreen(
    onBack: () -> Unit
) {
    val messages = remember {
        mutableStateListOf(
            TutorChatMessage(
                isUser = false,
                text = "Muraho! I am your MySchool Rwanda CBC AI Study Companion. Ask me any concept from your S1-S6 syllabus in Biology, Chemistry, Mathematics, or Physics!"
            )
        )
    }

    var inputPrompt by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Surface(
                color = NavyPrimary,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
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
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Rwanda CBC AI Tutor",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Competence-Based Curriculum Study Assistant",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    RwandaCurriculumTag(modifier = Modifier.padding(bottom = 6.dp))
                }

                items(messages) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (msg.isUser) NavyPrimary else MaterialTheme.colorScheme.surface
                            ),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                }
            }

            // Quick Question Prompts
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = false,
                    onClick = {
                        val q = "Explain Mendel's Law of Segregation for S4 Biology"
                        messages.add(TutorChatMessage(true, q))
                        messages.add(
                            TutorChatMessage(
                                false,
                                "In Rwanda CBC Secondary 4 Biology, Mendel's First Law (Law of Segregation) states that during gamete formation, the two alleles for a gene segregate (separate) from each other so that each gamete carries only one allele for each gene.\n\nKey Concepts:\n1. Heterozygote (e.g. Tt) produces 50% T and 50% t gametes.\n2. In a monohybrid cross (Tt x Tt), the resulting phenotypic ratio is 3:1 (Dominant:Recessive) and the genotypic ratio is 1:2:1 (TT:Tt:tt)."
                            )
                        )
                    },
                    label = { Text("Mendel's Law", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = false,
                    onClick = {
                        val q = "How do I solve quadratic equations with the quadratic formula?"
                        messages.add(TutorChatMessage(true, q))
                        messages.add(
                            TutorChatMessage(
                                false,
                                "For any quadratic equation in standard form: ax² + bx + c = 0 (where a ≠ 0):\n\nThe quadratic formula is:\nx = (-b ± √(b² - 4ac)) / (2a)\n\nDiscriminant (Δ = b² - 4ac):\n• If Δ > 0: Two distinct real roots\n• If Δ = 0: One real repeated root\n• If Δ < 0: No real roots (complex roots)"
                            )
                        )
                    },
                    label = { Text("Quadratic Formula", fontSize = 11.sp) }
                )
            }

            // Input Bar
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputPrompt,
                        onValueChange = { inputPrompt = it },
                        placeholder = { Text("Ask a question about your lessons...") },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("ai_tutor_input")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputPrompt.isNotBlank()) {
                                val userQ = inputPrompt
                                messages.add(TutorChatMessage(true, userQ))
                                inputPrompt = ""
                                // Generate intelligent educational response
                                val aiReply = generateTutorReply(userQ)
                                messages.add(TutorChatMessage(false, aiReply))
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(NavyPrimary)
                            .testTag("ai_tutor_send")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun generateTutorReply(prompt: String): String {
    val lower = prompt.lowercase()
    return when {
        lower.contains("photosynthesis") ->
            "In Rwanda CBC Biology, photosynthesis occurs in two stages in chloroplasts:\n1. Light-Dependent Reactions (in thylakoids): Water is split (photolysis), releasing O₂ and synthesizing ATP and NADPH.\n2. Light-Independent Reactions / Calvin Cycle (in stroma): CO₂ is fixed using ATP and NADPH into glucose (C₆H₁₂O₆)."

        lower.contains("newton") ->
            "Newton's Three Laws of Motion (Rwanda CBC Secondary Physics):\n1. First Law (Inertia): An object remains at rest or uniform motion unless acted on by an external net force.\n2. Second Law (F = ma): The acceleration of an object is directly proportional to the net force and inversely proportional to mass.\n3. Third Law (Action-Reaction): For every action force, there is an equal and opposite reaction force."

        lower.contains("titration") || lower.contains("acid") ->
            "In Secondary Chemistry practicals, Acid-Base Titration determines the unknown concentration of an acid or base. The equivalence point is when moles of H⁺ equal moles of OH⁻, detected using indicators like phenolphthalein or methyl orange."

        else ->
            "Great academic question! In the Rwanda Competence-Based Curriculum (CBC), key focus is given to understanding principles through real-world applications and critical thinking. Let's break this down into definitions, step-by-step mechanisms, and practical secondary school exam examples."
    }
}
