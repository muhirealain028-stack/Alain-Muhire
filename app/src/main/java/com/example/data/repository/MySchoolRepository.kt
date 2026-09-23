package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entities.AcademicYearEntity
import com.example.data.local.entities.AnnouncementEntity
import com.example.data.local.entities.AssessmentEntity
import com.example.data.local.entities.AssessmentType
import com.example.data.local.entities.AssignmentEntity
import com.example.data.local.entities.AttendanceEntity
import com.example.data.local.entities.AttendanceStatus
import com.example.data.local.entities.CachedSchoolNotificationEntity
import com.example.data.local.entities.CachedStudentGradeEntity
import com.example.data.local.entities.ConversationEntity
import com.example.data.local.entities.LearningMaterialEntity
import com.example.data.local.entities.MarkEntity
import com.example.data.local.entities.MaterialContentType
import com.example.data.local.entities.MessageEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.NotificationType
import com.example.data.local.entities.ParentEntity
import com.example.data.local.entities.ParentStudentRelationshipEntity
import com.example.data.local.entities.QuizAnswerEntity
import com.example.data.local.entities.QuizEntity
import com.example.data.local.entities.QuizQuestionEntity
import com.example.data.local.entities.SchoolClassEntity
import com.example.data.local.entities.StudentEntity
import com.example.data.local.entities.SubjectEntity
import com.example.data.local.entities.SubmissionEntity
import com.example.data.local.entities.TeacherEntity
import com.example.data.local.entities.TeacherFeedbackEntity
import com.example.data.local.entities.TeacherSubjectAssignmentEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

enum class PerformanceTrend {
    IMPROVING,
    STABLE,
    DECLINING
}

data class SubjectPerformance(
    val subject: SubjectEntity,
    val averageScore: Double,
    val assessmentCount: Int,
    val recentMark: Double?,
    val teacherName: String
)

data class ChildOverview(
    val student: StudentEntity,
    val user: UserEntity,
    val schoolClass: SchoolClassEntity?,
    val overallAverage: Double,
    val attendancePercentage: Double,
    val pendingAssignmentsCount: Int,
    val trend: PerformanceTrend
)

data class MarkWithAssessment(
    val mark: MarkEntity,
    val assessment: AssessmentEntity,
    val subject: SubjectEntity?
)

class MySchoolRepository(private val db: AppDatabase) {
    val userDao = db.userDao()
    val studentDao = db.studentDao()
    val parentDao = db.parentDao()
    val teacherDao = db.teacherDao()
    val academicDao = db.academicDao()
    val marksDao = db.marksDao()
    val learningDao = db.learningDao()
    val attendanceDao = db.attendanceDao()
    val feedbackDao = db.feedbackDao()
    val messagingDao = db.messagingDao()
    val notificationDao = db.notificationDao()
    val announcementDao = db.announcementDao()
    val cachedStudentGradeDao = db.cachedStudentGradeDao()
    val cachedSchoolNotificationDao = db.cachedSchoolNotificationDao()

    // --- Authentication ---
    suspend fun authenticate(email: String, passwordHash: String): UserEntity? {
        val user = userDao.getByEmail(email.trim()) ?: return null
        return if (user.passwordHash == passwordHash.trim()) user else null
    }

    suspend fun getUserById(userId: Long): UserEntity? = userDao.getById(userId)

    suspend fun registerUser(
        fullName: String,
        email: String,
        passwordHash: String,
        role: UserRole,
        phone: String
    ): UserEntity {
        val id = userDao.insert(
            UserEntity(
                email = email.trim(),
                passwordHash = passwordHash.trim(),
                fullName = fullName.trim(),
                role = role,
                phone = phone.trim()
            )
        )
        return userDao.getById(id)!!
    }

    // --- Students & Classes ---
    fun getAllStudents(): Flow<List<StudentEntity>> = studentDao.getAllStudents()
    fun getAllClasses(): Flow<List<SchoolClassEntity>> = academicDao.getAllClasses()
    fun getAllSubjects(): Flow<List<SubjectEntity>> = academicDao.getAllSubjects()
    fun getAllTeachers(): Flow<List<TeacherEntity>> = teacherDao.getAllTeachers()
    fun getAllParents(): Flow<List<ParentEntity>> = parentDao.getAllParents()

    suspend fun getStudentByUserId(userId: Long): StudentEntity? = studentDao.getByUserId(userId)
    suspend fun getParentByUserId(userId: Long): ParentEntity? = parentDao.getByUserId(userId)
    suspend fun getTeacherByUserId(userId: Long): TeacherEntity? = teacherDao.getByUserId(userId)

    suspend fun getClassById(classId: Long): SchoolClassEntity? = academicDao.getClassById(classId)
    suspend fun getSubjectById(subjectId: Long): SubjectEntity? = academicDao.getSubjectById(subjectId)

    // --- Performance Calculations ---
    fun getStudentMarksWithDetails(studentId: Long): Flow<List<MarkWithAssessment>> {
        return combine(
            marksDao.getMarksForStudent(studentId),
            marksDao.getAllAssessments(),
            academicDao.getAllSubjects()
        ) { marks, assessments, subjects ->
            val assessmentMap = assessments.associateBy { it.id }
            val subjectMap = subjects.associateBy { it.id }
            marks.mapNotNull { mark ->
                val assessment = assessmentMap[mark.assessmentId] ?: return@mapNotNull null
                val subject = subjectMap[assessment.subjectId]
                MarkWithAssessment(mark, assessment, subject)
            }.sortedByDescending { it.assessment.date }
        }
    }

    fun getSubjectPerformances(studentId: Long, classId: Long): Flow<List<SubjectPerformance>> {
        return combine(
            marksDao.getMarksForStudent(studentId),
            marksDao.getAssessmentsByClass(classId),
            academicDao.getAllSubjects(),
            teacherDao.getAllTeachers(),
            userDao.getAllUsers()
        ) { marks, assessments, subjects, teachers, users ->
            val assessmentMap = assessments.associateBy { it.id }
            val userMap = users.associateBy { it.id }
            val teacherMap = teachers.associateBy { it.id }

            subjects.map { subject ->
                val subjectAssessments = assessments.filter { it.subjectId == subject.id }
                val subjectAssessmentIds = subjectAssessments.map { it.id }.toSet()
                val subjectMarks = marks.filter { it.assessmentId in subjectAssessmentIds }

                val avg = if (subjectMarks.isNotEmpty()) {
                    val totalPercentage = subjectMarks.sumOf { mark ->
                        val ass = assessmentMap[mark.assessmentId]
                        val max = ass?.maxMark ?: 100.0
                        (mark.studentMark / max) * 100.0
                    }
                    totalPercentage / subjectMarks.size
                } else 0.0

                val recentMark = subjectMarks.maxByOrNull { it.updatedAt }?.let {
                    val ass = assessmentMap[it.assessmentId]
                    val max = ass?.maxMark ?: 100.0
                    (it.studentMark / max) * 100.0
                }

                SubjectPerformance(
                    subject = subject,
                    averageScore = Math.round(avg * 10.0) / 10.0,
                    assessmentCount = subjectMarks.size,
                    recentMark = recentMark?.let { Math.round(it * 10.0) / 10.0 },
                    teacherName = "Faculty Teacher"
                )
            }
        }
    }

    suspend fun calculateAttendancePercentage(studentId: Long): Double {
        val records = attendanceDao.getAttendanceForStudent(studentId).first()
        if (records.isEmpty()) return 100.0
        val presentCount = records.count { it.status == AttendanceStatus.PRESENT || it.status == AttendanceStatus.EXCUSED }
        return Math.round((presentCount.toDouble() / records.size.toDouble()) * 1000.0) / 10.0
    }

    suspend fun calculateStudentOverallAverage(studentId: Long): Double {
        val marks = marksDao.getMarksForStudent(studentId).first()
        if (marks.isEmpty()) return 0.0
        val assessments = marksDao.getAllAssessments().first().associateBy { it.id }
        val sum = marks.sumOf { mark ->
            val max = assessments[mark.assessmentId]?.maxMark ?: 100.0
            (mark.studentMark / max) * 100.0
        }
        return Math.round((sum / marks.size) * 10.0) / 10.0
    }

    suspend fun calculatePerformanceTrend(studentId: Long): PerformanceTrend {
        val marks = marksDao.getMarksForStudent(studentId).first()
        if (marks.size < 2) return PerformanceTrend.STABLE
        val assessments = marksDao.getAllAssessments().first().associateBy { it.id }
        val sorted = marks.mapNotNull { m ->
            val a = assessments[m.assessmentId] ?: return@mapNotNull null
            Pair(a.date, (m.studentMark / a.maxMark) * 100.0)
        }.sortedBy { it.first }

        if (sorted.size < 2) return PerformanceTrend.STABLE
        val firstHalf = sorted.take(sorted.size / 2).map { it.second }.average()
        val secondHalf = sorted.drop(sorted.size / 2).map { it.second }.average()

        return when {
            secondHalf - firstHalf > 2.0 -> PerformanceTrend.IMPROVING
            firstHalf - secondHalf > 2.0 -> PerformanceTrend.DECLINING
            else -> PerformanceTrend.STABLE
        }
    }

    // --- Parent Linked Children ---
    fun getLinkedChildrenOverview(parentId: Long): Flow<List<ChildOverview>> = flow {
        val rels = parentDao.getRelationshipsForParent(parentId).first()
        val list = mutableListOf<ChildOverview>()
        for (rel in rels) {
            val student = studentDao.getById(rel.studentId) ?: continue
            val user = userDao.getById(student.userId) ?: continue
            val schoolClass = academicDao.getClassById(student.classId)
            val avg = calculateStudentOverallAverage(student.id)
            val att = calculateAttendancePercentage(student.id)
            val trend = calculatePerformanceTrend(student.id)
            val pendingCount = learningDao.getAssignmentsByClass(student.classId).first().size
            list.add(
                ChildOverview(
                    student = student,
                    user = user,
                    schoolClass = schoolClass,
                    overallAverage = avg,
                    attendancePercentage = att,
                    pendingAssignmentsCount = pendingCount,
                    trend = trend
                )
            )
        }
        emit(list)
    }

    // --- Messaging ---
    fun getConversationsForParent(parentId: Long): Flow<List<ConversationEntity>> =
        messagingDao.getConversationsForParent(parentId)

    fun getConversationsForTeacher(teacherId: Long): Flow<List<ConversationEntity>> =
        messagingDao.getConversationsForTeacher(teacherId)

    fun getMessagesForConversation(convId: Long): Flow<List<MessageEntity>> =
        messagingDao.getMessagesForConversation(convId)

    suspend fun sendMessage(convId: Long, senderUserId: Long, receiverUserId: Long, content: String) {
        val now = System.currentTimeMillis()
        messagingDao.insertMessage(
            MessageEntity(
                conversationId = convId,
                senderUserId = senderUserId,
                receiverUserId = receiverUserId,
                content = content.trim(),
                timestamp = now,
                isRead = false
            )
        )
        val conv = messagingDao.getConversationById(convId)
        if (conv != null) {
            messagingDao.updateConversation(
                conv.copy(
                    lastMessageAt = now,
                    lastMessagePreview = content.trim().take(60)
                )
            )
        }
    }

    // --- Marks Entry ---
    suspend fun enterOrUpdateMark(assessmentId: Long, studentId: Long, markValue: Double, feedback: String, teacherId: Long) {
        val existing = marksDao.getMarkForStudentAssessment(assessmentId, studentId)
        if (existing != null) {
            marksDao.updateMark(
                existing.copy(
                    studentMark = markValue,
                    feedback = feedback,
                    updatedAt = System.currentTimeMillis()
                )
            )
        } else {
            marksDao.insertMark(
                MarkEntity(
                    assessmentId = assessmentId,
                    studentId = studentId,
                    studentMark = markValue,
                    feedback = feedback,
                    teacherId = teacherId
                )
            )
        }
    }

    // --- Learning materials ---
    suspend fun uploadMaterial(
        title: String,
        description: String,
        classId: Long,
        subjectId: Long,
        termId: Long,
        topic: String,
        contentType: MaterialContentType,
        url: String,
        teacherId: Long
    ) {
        learningDao.insertMaterial(
            LearningMaterialEntity(
                title = title.trim(),
                description = description.trim(),
                classId = classId,
                subjectId = subjectId,
                termId = termId,
                topic = topic.trim(),
                contentType = contentType,
                contentUrl = url.trim(),
                teacherId = teacherId
            )
        )
    }

    // --- Assignments ---
    suspend fun createAssignment(
        title: String,
        description: String,
        classId: Long,
        subjectId: Long,
        teacherId: Long,
        dueDate: String,
        maxScore: Int
    ): Long {
        return learningDao.insertAssignment(
            AssignmentEntity(
                title = title.trim(),
                description = description.trim(),
                classId = classId,
                subjectId = subjectId,
                teacherId = teacherId,
                dueDate = dueDate.trim(),
                maxScore = maxScore
            )
        )
    }

    suspend fun submitAssignment(assignmentId: Long, studentId: Long, text: String) {
        learningDao.insertSubmission(
            SubmissionEntity(
                assignmentId = assignmentId,
                studentId = studentId,
                contentText = text.trim(),
                status = "SUBMITTED"
            )
        )
    }

    // --- Notifications & Announcements ---
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>> =
        notificationDao.getNotificationsForUser(userId)

    suspend fun markNotificationRead(id: Long) = notificationDao.markAsRead(id)

    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>> = announcementDao.getAllAnnouncements()

    suspend fun createAnnouncement(title: String, content: String, targetRole: String, author: String, priority: String) {
        announcementDao.insert(
            AnnouncementEntity(
                title = title.trim(),
                content = content.trim(),
                targetRole = targetRole,
                authorName = author.trim(),
                priority = priority
            )
        )
    }

    // --- Offline Caching: Grades ---
    fun getCachedGradesForStudent(studentId: Long): Flow<List<CachedStudentGradeEntity>> =
        cachedStudentGradeDao.getCachedGradesForStudent(studentId)

    fun getAllCachedGrades(): Flow<List<CachedStudentGradeEntity>> =
        cachedStudentGradeDao.getAllCachedGrades()

    fun getCachedGradesBySubject(studentId: Long, subjectCode: String): Flow<List<CachedStudentGradeEntity>> =
        cachedStudentGradeDao.getCachedGradesBySubject(studentId, subjectCode)

    suspend fun cacheStudentGrade(grade: CachedStudentGradeEntity): Long =
        cachedStudentGradeDao.insertCachedGrade(grade)

    suspend fun cacheStudentGrades(grades: List<CachedStudentGradeEntity>) =
        cachedStudentGradeDao.insertCachedGrades(grades)

    suspend fun syncGradesToCache(studentId: Long) {
        val marks = marksDao.getMarksForStudent(studentId).first()
        val student = studentDao.getById(studentId) ?: return
        val user = userDao.getById(student.userId)
        val studentName = user?.fullName ?: "Student"
        val assessments = marksDao.getAllAssessments().first().associateBy { it.id }
        val subjects = academicDao.getAllSubjects().first().associateBy { it.id }

        val cachedList = marks.mapNotNull { mark ->
            val assessment = assessments[mark.assessmentId] ?: return@mapNotNull null
            val subject = subjects[assessment.subjectId]
            val max = if (assessment.maxMark > 0.0) assessment.maxMark else 100.0
            val pct = Math.round((mark.studentMark / max) * 1000.0) / 10.0
            val letter = when {
                pct >= 90.0 -> "A+"
                pct >= 80.0 -> "A"
                pct >= 70.0 -> "B"
                pct >= 60.0 -> "C"
                pct >= 50.0 -> "D"
                else -> "F"
            }
            CachedStudentGradeEntity(
                studentId = studentId,
                studentName = studentName,
                registrationNumber = student.registrationNumber,
                subjectName = subject?.name ?: "Subject",
                subjectCode = subject?.code ?: "SUB",
                assessmentTitle = assessment.title,
                assessmentType = assessment.type.name,
                score = mark.studentMark,
                maxScore = max,
                percentage = pct,
                gradeLetter = letter,
                teacherFeedback = mark.feedback,
                teacherName = "Faculty Teacher",
                termName = "Term 2",
                academicYear = "2025-2026",
                assessmentDate = assessment.date,
                cachedAt = System.currentTimeMillis(),
                offlineAvailable = true,
                syncStatus = "SYNCED"
            )
        }
        if (cachedList.isNotEmpty()) {
            cachedStudentGradeDao.insertCachedGrades(cachedList)
        }
    }

    // --- Offline Caching: Notifications ---
    fun getOfflineNotifications(userId: Long, role: String): Flow<List<CachedSchoolNotificationEntity>> =
        cachedSchoolNotificationDao.getOfflineNotifications(userId, role)

    fun getAllOfflineNotifications(): Flow<List<CachedSchoolNotificationEntity>> =
        cachedSchoolNotificationDao.getAllOfflineNotifications()

    fun getUnreadOfflineNotificationCount(userId: Long, role: String): Flow<Int> =
        cachedSchoolNotificationDao.getUnreadOfflineCount(userId, role)

    suspend fun cacheNotification(notification: CachedSchoolNotificationEntity): Long =
        cachedSchoolNotificationDao.insertCachedNotification(notification)

    suspend fun cacheNotifications(notifications: List<CachedSchoolNotificationEntity>) =
        cachedSchoolNotificationDao.insertCachedNotifications(notifications)

    suspend fun markOfflineNotificationRead(id: Long) =
        cachedSchoolNotificationDao.markAsRead(id)

    suspend fun markAllOfflineNotificationsRead(userId: Long) =
        cachedSchoolNotificationDao.markAllAsRead(userId)
}
