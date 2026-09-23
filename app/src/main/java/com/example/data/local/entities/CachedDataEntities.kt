package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Local cached entity for student grades/marks.
 * Enables offline access and fast retrieval of academic results
 * even when internet connection is intermittent or unavailable.
 */
@Entity(
    tableName = "cached_student_grades",
    indices = [
        Index(value = ["studentId"]),
        Index(value = ["studentId", "subjectCode"]),
        Index(value = ["cachedAt"])
    ]
)
data class CachedStudentGradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val studentId: Long,
    val studentName: String,
    val registrationNumber: String,
    val subjectName: String,
    val subjectCode: String,
    val assessmentTitle: String,
    val assessmentType: String, // e.g. "CAT 1", "MID TERM", "ASSIGNMENT", "PRACTICAL"
    val score: Double,
    val maxScore: Double = 100.0,
    val percentage: Double,
    val gradeLetter: String, // "A", "B+", "B", "C", "D", "E"
    val teacherFeedback: String = "",
    val teacherName: String = "",
    val termName: String = "Term 2",
    val academicYear: String = "2025-2026",
    val assessmentDate: String,
    val cachedAt: Long = System.currentTimeMillis(),
    val offlineAvailable: Boolean = true,
    val syncStatus: String = "SYNCED" // "SYNCED", "PENDING_SYNC", "OFFLINE_ONLY"
)

/**
 * Local cached entity for school announcements and notifications.
 * Stores school-wide broadcasts, class updates, academic notices,
 * and exam schedules for reliable offline viewing.
 */
@Entity(
    tableName = "cached_school_notifications",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["targetRole"]),
        Index(value = ["category"]),
        Index(value = ["cachedAt"])
    ]
)
data class CachedSchoolNotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val remoteNotificationId: Long? = null,
    val userId: Long? = null, // Specific recipient or null for broadcast
    val targetRole: String = "ALL", // "ALL", "STUDENT", "PARENT", "TEACHER"
    val title: String,
    val message: String,
    val category: String = "ACADEMIC", // "ACADEMIC", "ANNOUNCEMENT", "ATTENDANCE", "EXAM", "EMERGENCY"
    val priority: String = "NORMAL", // "LOW", "NORMAL", "URGENT"
    val publishedDate: String, // e.g. "2026-03-22"
    val authorName: String = "School Administration",
    val isRead: Boolean = false,
    val cachedAt: Long = System.currentTimeMillis(),
    val offlineAvailable: Boolean = true,
    val downloadedForOffline: Boolean = true
)
