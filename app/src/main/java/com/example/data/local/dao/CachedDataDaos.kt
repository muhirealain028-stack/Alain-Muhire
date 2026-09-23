package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.CachedSchoolNotificationEntity
import com.example.data.local.entities.CachedStudentGradeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CachedStudentGradeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedGrades(grades: List<CachedStudentGradeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedGrade(grade: CachedStudentGradeEntity): Long

    @Update
    suspend fun updateCachedGrade(grade: CachedStudentGradeEntity)

    @Query("SELECT * FROM cached_student_grades WHERE studentId = :studentId ORDER BY cachedAt DESC")
    fun getCachedGradesForStudent(studentId: Long): Flow<List<CachedStudentGradeEntity>>

    @Query("SELECT * FROM cached_student_grades WHERE studentId = :studentId AND subjectCode = :subjectCode ORDER BY cachedAt DESC")
    fun getCachedGradesBySubject(studentId: Long, subjectCode: String): Flow<List<CachedStudentGradeEntity>>

    @Query("SELECT * FROM cached_student_grades ORDER BY cachedAt DESC")
    fun getAllCachedGrades(): Flow<List<CachedStudentGradeEntity>>

    @Query("SELECT * FROM cached_student_grades WHERE studentId = :studentId ORDER BY cachedAt DESC LIMIT :limit")
    fun getRecentCachedGrades(studentId: Long, limit: Int = 5): Flow<List<CachedStudentGradeEntity>>

    @Query("SELECT COUNT(*) FROM cached_student_grades WHERE studentId = :studentId")
    suspend fun getCachedGradesCount(studentId: Long): Int

    @Query("DELETE FROM cached_student_grades WHERE studentId = :studentId")
    suspend fun clearCachedGradesForStudent(studentId: Long)

    @Query("DELETE FROM cached_student_grades")
    suspend fun clearAllCachedGrades()
}

@Dao
interface CachedSchoolNotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedNotifications(notifications: List<CachedSchoolNotificationEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCachedNotification(notification: CachedSchoolNotificationEntity): Long

    @Update
    suspend fun updateCachedNotification(notification: CachedSchoolNotificationEntity)

    @Query("""
        SELECT * FROM cached_school_notifications 
        WHERE (userId = :userId OR targetRole = 'ALL' OR targetRole = :role) 
        ORDER BY cachedAt DESC
    """)
    fun getOfflineNotifications(userId: Long, role: String): Flow<List<CachedSchoolNotificationEntity>>

    @Query("SELECT * FROM cached_school_notifications ORDER BY cachedAt DESC")
    fun getAllOfflineNotifications(): Flow<List<CachedSchoolNotificationEntity>>

    @Query("""
        SELECT COUNT(*) FROM cached_school_notifications 
        WHERE isRead = 0 AND (userId = :userId OR targetRole = 'ALL' OR targetRole = :role)
    """)
    fun getUnreadOfflineCount(userId: Long, role: String): Flow<Int>

    @Query("UPDATE cached_school_notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE cached_school_notifications SET isRead = 1 WHERE userId = :userId OR targetRole = 'ALL'")
    suspend fun markAllAsRead(userId: Long)

    @Query("DELETE FROM cached_school_notifications WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM cached_school_notifications")
    suspend fun clearAll()
}
