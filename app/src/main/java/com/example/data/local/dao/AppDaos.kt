package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.AcademicYearEntity
import com.example.data.local.entities.AnnouncementEntity
import com.example.data.local.entities.AssessmentEntity
import com.example.data.local.entities.AssignmentEntity
import com.example.data.local.entities.AttendanceEntity
import com.example.data.local.entities.ConversationEntity
import com.example.data.local.entities.EnrollmentEntity
import com.example.data.local.entities.LearningMaterialEntity
import com.example.data.local.entities.MarkEntity
import com.example.data.local.entities.MessageEntity
import com.example.data.local.entities.NotificationEntity
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
import com.example.data.local.entities.TermEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = :role")
    fun getUsersByRole(role: UserRole): Flow<List<UserEntity>>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users WHERE role = :role")
    fun countByRole(role: UserRole): Flow<Int>
}

@Dao
interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: StudentEntity): Long

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun getById(id: Long): StudentEntity?

    @Query("SELECT * FROM students WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: Long): StudentEntity?

    @Query("SELECT * FROM students WHERE classId = :classId")
    fun getStudentsByClassId(classId: Long): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Query("SELECT COUNT(*) FROM students")
    fun countStudents(): Flow<Int>
}

@Dao
interface ParentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(parent: ParentEntity): Long

    @Query("SELECT * FROM parents WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: Long): ParentEntity?

    @Query("SELECT * FROM parents WHERE id = :id")
    suspend fun getById(id: Long): ParentEntity?

    @Query("SELECT * FROM parents")
    fun getAllParents(): Flow<List<ParentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelationship(rel: ParentStudentRelationshipEntity): Long

    @Query("SELECT * FROM parent_student_relationships WHERE parentId = :parentId")
    fun getRelationshipsForParent(parentId: Long): Flow<List<ParentStudentRelationshipEntity>>

    @Query("SELECT * FROM parent_student_relationships WHERE studentId = :studentId")
    fun getRelationshipsForStudent(studentId: Long): Flow<List<ParentStudentRelationshipEntity>>

    @Query("SELECT COUNT(*) FROM parents")
    fun countParents(): Flow<Int>
}

@Dao
interface TeacherDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(teacher: TeacherEntity): Long

    @Query("SELECT * FROM teachers WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: Long): TeacherEntity?

    @Query("SELECT * FROM teachers WHERE id = :id")
    suspend fun getById(id: Long): TeacherEntity?

    @Query("SELECT * FROM teachers")
    fun getAllTeachers(): Flow<List<TeacherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun assignSubject(assignment: TeacherSubjectAssignmentEntity): Long

    @Query("SELECT * FROM teacher_subject_assignments WHERE teacherId = :teacherId")
    fun getAssignmentsForTeacher(teacherId: Long): Flow<List<TeacherSubjectAssignmentEntity>>

    @Query("SELECT * FROM teacher_subject_assignments WHERE classId = :classId")
    fun getAssignmentsForClass(classId: Long): Flow<List<TeacherSubjectAssignmentEntity>>

    @Query("SELECT COUNT(*) FROM teachers")
    fun countTeachers(): Flow<Int>
}

@Dao
interface AcademicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcademicYear(year: AcademicYearEntity): Long

    @Query("SELECT * FROM academic_years")
    fun getAcademicYears(): Flow<List<AcademicYearEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTerm(term: TermEntity): Long

    @Query("SELECT * FROM terms WHERE academicYearId = :yearId")
    fun getTerms(yearId: Long): Flow<List<TermEntity>>

    @Query("SELECT * FROM terms WHERE isCurrent = 1 LIMIT 1")
    suspend fun getCurrentTerm(): TermEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClass(schoolClass: SchoolClassEntity): Long

    @Query("SELECT * FROM classes")
    fun getAllClasses(): Flow<List<SchoolClassEntity>>

    @Query("SELECT * FROM classes WHERE id = :id")
    suspend fun getClassById(id: Long): SchoolClassEntity?

    @Query("SELECT COUNT(*) FROM classes")
    fun countClasses(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: SubjectEntity): Long

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Long): SubjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity): Long
}

@Dao
interface MarksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: AssessmentEntity): Long

    @Query("SELECT * FROM assessments WHERE classId = :classId")
    fun getAssessmentsByClass(classId: Long): Flow<List<AssessmentEntity>>

    @Query("SELECT * FROM assessments WHERE id = :id")
    suspend fun getAssessmentById(id: Long): AssessmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMark(mark: MarkEntity): Long

    @Update
    suspend fun updateMark(mark: MarkEntity)

    @Query("SELECT * FROM marks WHERE studentId = :studentId")
    fun getMarksForStudent(studentId: Long): Flow<List<MarkEntity>>

    @Query("SELECT * FROM marks WHERE assessmentId = :assessmentId")
    fun getMarksForAssessment(assessmentId: Long): Flow<List<MarkEntity>>

    @Query("SELECT * FROM marks WHERE assessmentId = :assessmentId AND studentId = :studentId LIMIT 1")
    suspend fun getMarkForStudentAssessment(assessmentId: Long, studentId: Long): MarkEntity?

    @Query("SELECT * FROM assessments")
    fun getAllAssessments(): Flow<List<AssessmentEntity>>
}

@Dao
interface LearningDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMaterial(material: LearningMaterialEntity): Long

    @Query("SELECT * FROM learning_materials ORDER BY createdAt DESC")
    fun getAllMaterials(): Flow<List<LearningMaterialEntity>>

    @Query("SELECT * FROM learning_materials WHERE classId = :classId ORDER BY createdAt DESC")
    fun getMaterialsByClass(classId: Long): Flow<List<LearningMaterialEntity>>

    @Query("SELECT * FROM learning_materials WHERE classId = :classId AND subjectId = :subjectId ORDER BY createdAt DESC")
    fun getMaterialsBySubject(classId: Long, subjectId: Long): Flow<List<LearningMaterialEntity>>

    // Assignments
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssignment(assignment: AssignmentEntity): Long

    @Query("SELECT * FROM assignments WHERE classId = :classId ORDER BY createdAt DESC")
    fun getAssignmentsByClass(classId: Long): Flow<List<AssignmentEntity>>

    @Query("SELECT * FROM assignments WHERE id = :id")
    suspend fun getAssignmentById(id: Long): AssignmentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: SubmissionEntity): Long

    @Update
    suspend fun updateSubmission(submission: SubmissionEntity)

    @Query("SELECT * FROM submissions WHERE studentId = :studentId")
    fun getSubmissionsForStudent(studentId: Long): Flow<List<SubmissionEntity>>

    @Query("SELECT * FROM submissions WHERE assignmentId = :assignmentId")
    fun getSubmissionsForAssignment(assignmentId: Long): Flow<List<SubmissionEntity>>

    // Quizzes
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuiz(quiz: QuizEntity): Long

    @Query("SELECT * FROM quizzes WHERE classId = :classId")
    fun getQuizzesByClass(classId: Long): Flow<List<QuizEntity>>

    @Query("SELECT * FROM quizzes WHERE id = :id")
    suspend fun getQuizById(id: Long): QuizEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizQuestion(question: QuizQuestionEntity): Long

    @Query("SELECT * FROM quiz_questions WHERE quizId = :quizId")
    suspend fun getQuestionsForQuiz(quizId: Long): List<QuizQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuizAnswer(answer: QuizAnswerEntity): Long

    @Query("SELECT * FROM quiz_answers WHERE studentId = :studentId")
    fun getQuizAnswersForStudent(studentId: Long): Flow<List<QuizAnswerEntity>>
}

@Dao
interface AttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(attendance: AttendanceEntity): Long

    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY date DESC")
    fun getAttendanceForStudent(studentId: Long): Flow<List<AttendanceEntity>>

    @Query("SELECT * FROM attendance")
    fun getAllAttendance(): Flow<List<AttendanceEntity>>
}

@Dao
interface FeedbackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(feedback: TeacherFeedbackEntity): Long

    @Query("SELECT * FROM teacher_feedback WHERE studentId = :studentId ORDER BY date DESC")
    fun getFeedbackForStudent(studentId: Long): Flow<List<TeacherFeedbackEntity>>
}

@Dao
interface MessagingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Query("SELECT * FROM conversations WHERE parentId = :parentId ORDER BY lastMessageAt DESC")
    fun getConversationsForParent(parentId: Long): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE teacherId = :teacherId ORDER BY lastMessageAt DESC")
    fun getConversationsForTeacher(teacherId: Long): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversationById(id: Long): ConversationEntity?

    @Update
    suspend fun updateConversation(conversation: ConversationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("SELECT * FROM messages WHERE conversationId = :convId ORDER BY timestamp ASC")
    fun getMessagesForConversation(convId: Long): Flow<List<MessageEntity>>

    @Query("UPDATE messages SET isRead = 1 WHERE conversationId = :convId AND receiverUserId = :userId")
    suspend fun markMessagesAsRead(convId: Long, userId: Long)
}

@Dao
interface NotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity): Long

    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUser(userId: Long): Flow<List<NotificationEntity>>

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE userId = :userId")
    suspend fun markAllAsRead(userId: Long)
}

@Dao
interface AnnouncementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(announcement: AnnouncementEntity): Long

    @Query("SELECT * FROM announcements ORDER BY date DESC, id DESC")
    fun getAllAnnouncements(): Flow<List<AnnouncementEntity>>
}
