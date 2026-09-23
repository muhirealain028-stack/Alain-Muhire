package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// --- User Roles ---
enum class UserRole {
    STUDENT,
    PARENT,
    TEACHER,
    ADMIN
}

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val email: String,
    val passwordHash: String,
    val fullName: String,
    val role: UserRole,
    val phone: String = "",
    val avatarUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val registrationNumber: String,
    val classId: Long,
    val dateOfBirth: String = "2008-04-12",
    val gender: String = "Female"
)

@Entity(tableName = "parents")
data class ParentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val occupation: String = "",
    val address: String = "Kigali, Rwanda",
    val phone: String = "+250 788 123 456"
)

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val employeeNumber: String,
    val department: String,
    val qualification: String = "B.Ed Sciences"
)

@Entity(tableName = "parent_student_relationships")
data class ParentStudentRelationshipEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val parentId: Long,
    val studentId: Long,
    val relationshipType: String = "Mother" // Mother, Father, Guardian
)

@Entity(tableName = "academic_years")
data class AcademicYearEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g. "2025-2026"
    val isCurrent: Boolean = true
)

@Entity(tableName = "terms")
data class TermEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val academicYearId: Long,
    val name: String, // "Term 1", "Term 2", "Term 3"
    val isCurrent: Boolean = true
)

@Entity(tableName = "classes")
data class SchoolClassEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // e.g. "Senior 4 MCB", "Senior 2 A"
    val gradeLevel: String = "S4",
    val academicYearId: Long = 1
)

@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String, // Biology, Chemistry, Mathematics, Physics
    val code: String, // BIO, CHE, MAT, PHY
    val iconName: String = "science"
)

@Entity(tableName = "teacher_subject_assignments")
data class TeacherSubjectAssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val teacherId: Long,
    val classId: Long,
    val subjectId: Long
)

@Entity(tableName = "enrollments")
data class EnrollmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val classId: Long,
    val academicYearId: Long = 1
)

enum class AssessmentType {
    HOMEWORK,
    CAT_1,
    CAT_2,
    MID_TERM,
    END_TERM,
    PROJECT
}

@Entity(tableName = "assessments")
data class AssessmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: AssessmentType,
    val classId: Long,
    val subjectId: Long,
    val termId: Long,
    val academicYearId: Long = 1,
    val maxMark: Double = 100.0,
    val date: String = "2026-03-15"
)

@Entity(tableName = "marks")
data class MarkEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assessmentId: Long,
    val studentId: Long,
    val studentMark: Double,
    val feedback: String = "",
    val teacherId: Long = 1,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "assignments")
data class AssignmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val classId: Long,
    val subjectId: Long,
    val teacherId: Long,
    val dueDate: String,
    val maxScore: Int = 100,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "submissions")
data class SubmissionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assignmentId: Long,
    val studentId: Long,
    val contentText: String,
    val submissionDate: Long = System.currentTimeMillis(),
    val status: String = "SUBMITTED", // PENDING, SUBMITTED, GRADED
    val obtainedScore: Int? = null,
    val teacherRemarks: String = ""
)

@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val subjectId: Long,
    val classId: Long,
    val termId: Long,
    val durationMinutes: Int = 15,
    val totalMarks: Int = 20
)

@Entity(tableName = "quiz_questions")
data class QuizQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizId: Long,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOptionIndex: Int, // 0 = A, 1 = B, 2 = C, 3 = D
    val explanation: String = ""
)

@Entity(tableName = "quiz_answers")
data class QuizAnswerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val quizId: Long,
    val studentId: Long,
    val score: Int,
    val totalQuestions: Int,
    val completedAt: Long = System.currentTimeMillis()
)

enum class MaterialContentType {
    PDF,
    NOTE,
    VIDEO,
    LINK
}

@Entity(tableName = "learning_materials")
data class LearningMaterialEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val classId: Long,
    val subjectId: Long,
    val termId: Long,
    val academicYearId: Long = 1,
    val topic: String, // e.g. "Cell Division & Genetics"
    val contentType: MaterialContentType,
    val contentUrl: String,
    val teacherId: Long,
    val createdAt: Long = System.currentTimeMillis()
)

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    EXCUSED
}

@Entity(tableName = "attendance")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val date: String, // YYYY-MM-DD
    val status: AttendanceStatus,
    val termId: Long,
    val remarks: String = ""
)

@Entity(tableName = "teacher_feedback")
data class TeacherFeedbackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val teacherId: Long,
    val subjectId: Long,
    val termId: Long,
    val feedbackText: String,
    val date: String,
    val category: String = "ACADEMIC" // ACADEMIC, BEHAVIOR, EFFORT
)

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val parentId: Long,
    val teacherId: Long,
    val subjectId: Long,
    val lastMessageAt: Long = System.currentTimeMillis(),
    val lastMessagePreview: String = ""
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val senderUserId: Long,
    val receiverUserId: Long,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

enum class NotificationType {
    MARKS,
    ASSIGNMENT,
    MESSAGE,
    FEEDBACK,
    ANNOUNCEMENT
}

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val targetRole: String = "ALL", // ALL, STUDENTS, PARENTS, TEACHERS, CLASS
    val classId: Long? = null,
    val authorName: String = "School Administration",
    val date: String = "2026-03-20",
    val priority: String = "NORMAL" // NORMAL, URGENT
)
