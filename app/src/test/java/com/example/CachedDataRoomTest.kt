package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.entities.CachedSchoolNotificationEntity
import com.example.data.local.entities.CachedStudentGradeEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class CachedDataRoomTest {

    private lateinit var database: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun closeDb() {
        database.close()
    }

    @Test
    fun testCachedStudentGradesInsertAndQuery() = runBlocking {
        val gradeDao = database.cachedStudentGradeDao()

        val sampleGrade = CachedStudentGradeEntity(
            id = 1L,
            studentId = 10L,
            studentName = "Keza Ines",
            registrationNumber = "RW/2026/01",
            subjectName = "Biology",
            subjectCode = "BIO",
            assessmentTitle = "Cell Biology CAT",
            assessmentType = "CAT 1",
            score = 92.0,
            maxScore = 100.0,
            percentage = 92.0,
            gradeLetter = "A",
            teacherFeedback = "Excellent performance",
            assessmentDate = "2026-03-20"
        )

        gradeDao.insertCachedGrade(sampleGrade)

        val retrievedGrades = gradeDao.getCachedGradesForStudent(10L).first()
        assertEquals(1, retrievedGrades.size)
        assertEquals("BIO", retrievedGrades[0].subjectCode)
        assertEquals(92.0, retrievedGrades[0].percentage, 0.01)
        assertEquals("A", retrievedGrades[0].gradeLetter)
        assertTrue(retrievedGrades[0].offlineAvailable)
    }

    @Test
    fun testCachedSchoolNotificationReadAndUnreadCount() = runBlocking {
        val notificationDao = database.cachedSchoolNotificationDao()

        val notif1 = CachedSchoolNotificationEntity(
            id = 1L,
            userId = 5L,
            targetRole = "STUDENT",
            title = "End of Term 2 Examination Timetable",
            message = "Exams start on March 30th. Stored locally for offline verification.",
            category = "ACADEMIC",
            publishedDate = "2026-03-22",
            isRead = false
        )

        val notif2 = CachedSchoolNotificationEntity(
            id = 2L,
            userId = null,
            targetRole = "ALL",
            title = "Campus General Assembly",
            message = "Assembly on Monday 8:00 AM.",
            category = "ANNOUNCEMENT",
            publishedDate = "2026-03-21",
            isRead = false
        )

        notificationDao.insertCachedNotifications(listOf(notif1, notif2))

        val unreadBefore = notificationDao.getUnreadOfflineCount(5L, "STUDENT").first()
        assertEquals(2, unreadBefore)

        notificationDao.markAsRead(1L)
        val unreadAfter = notificationDao.getUnreadOfflineCount(5L, "STUDENT").first()
        assertEquals(1, unreadAfter)

        val allNotifs = notificationDao.getOfflineNotifications(5L, "STUDENT").first()
        assertEquals(2, allNotifs.size)
        val readNotif = allNotifs.find { it.id == 1L }
        assertNotNull(readNotif)
        assertTrue(readNotif!!.isRead)
    }
}
