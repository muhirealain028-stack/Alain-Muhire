package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.AcademicDao
import com.example.data.local.dao.AnnouncementDao
import com.example.data.local.dao.AttendanceDao
import com.example.data.local.dao.CachedSchoolNotificationDao
import com.example.data.local.dao.CachedStudentGradeDao
import com.example.data.local.dao.FeedbackDao
import com.example.data.local.dao.LearningDao
import com.example.data.local.dao.MarksDao
import com.example.data.local.dao.MessagingDao
import com.example.data.local.dao.NotificationDao
import com.example.data.local.dao.ParentDao
import com.example.data.local.dao.StudentDao
import com.example.data.local.dao.TeacherDao
import com.example.data.local.dao.UserDao
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
import com.example.data.local.entities.EnrollmentEntity
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
import com.example.data.local.entities.TermEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        StudentEntity::class,
        ParentEntity::class,
        TeacherEntity::class,
        ParentStudentRelationshipEntity::class,
        AcademicYearEntity::class,
        TermEntity::class,
        SchoolClassEntity::class,
        SubjectEntity::class,
        TeacherSubjectAssignmentEntity::class,
        EnrollmentEntity::class,
        AssessmentEntity::class,
        MarkEntity::class,
        AssignmentEntity::class,
        SubmissionEntity::class,
        QuizEntity::class,
        QuizQuestionEntity::class,
        QuizAnswerEntity::class,
        LearningMaterialEntity::class,
        AttendanceEntity::class,
        TeacherFeedbackEntity::class,
        ConversationEntity::class,
        MessageEntity::class,
        NotificationEntity::class,
        AnnouncementEntity::class,
        CachedStudentGradeEntity::class,
        CachedSchoolNotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun studentDao(): StudentDao
    abstract fun parentDao(): ParentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun academicDao(): AcademicDao
    abstract fun marksDao(): MarksDao
    abstract fun learningDao(): LearningDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun messagingDao(): MessagingDao
    abstract fun notificationDao(): NotificationDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun cachedStudentGradeDao(): CachedStudentGradeDao
    abstract fun cachedSchoolNotificationDao(): CachedSchoolNotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myschool_connect.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(DatabaseSeeder(context.applicationContext))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class DatabaseSeeder(private val context: Context) : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        CoroutineScope(Dispatchers.IO).launch {
            seedInitialData(AppDatabase.getDatabase(context))
        }
    }

    private suspend fun seedInitialData(database: AppDatabase) {
        val academicDao = database.academicDao()
        val userDao = database.userDao()
        val studentDao = database.studentDao()
        val parentDao = database.parentDao()
        val teacherDao = database.teacherDao()
        val marksDao = database.marksDao()
        val learningDao = database.learningDao()
        val attendanceDao = database.attendanceDao()
        val feedbackDao = database.feedbackDao()
        val messagingDao = database.messagingDao()
        val notificationDao = database.notificationDao()
        val announcementDao = database.announcementDao()
        val cachedStudentGradeDao = database.cachedStudentGradeDao()
        val cachedSchoolNotificationDao = database.cachedSchoolNotificationDao()

        // 1. Academic Year & Terms
        val yearId = academicDao.insertAcademicYear(AcademicYearEntity(id = 1, name = "2025-2026", isCurrent = true))
        val term1Id = academicDao.insertTerm(TermEntity(id = 1, academicYearId = yearId, name = "Term 1", isCurrent = false))
        val term2Id = academicDao.insertTerm(TermEntity(id = 2, academicYearId = yearId, name = "Term 2", isCurrent = true))
        val term3Id = academicDao.insertTerm(TermEntity(id = 3, academicYearId = yearId, name = "Term 3", isCurrent = false))

        // 2. Classes
        val classS4 = academicDao.insertClass(SchoolClassEntity(id = 1, name = "Senior 4 MCB", gradeLevel = "S4", academicYearId = yearId))
        val classS2 = academicDao.insertClass(SchoolClassEntity(id = 2, name = "Senior 2 A", gradeLevel = "S2", academicYearId = yearId))
        val classS5 = academicDao.insertClass(SchoolClassEntity(id = 3, name = "Senior 5 PCB", gradeLevel = "S5", academicYearId = yearId))

        // 3. Subjects
        val subBio = academicDao.insertSubject(SubjectEntity(id = 1, name = "Biology", code = "BIO", iconName = "biology"))
        val subChe = academicDao.insertSubject(SubjectEntity(id = 2, name = "Chemistry", code = "CHE", iconName = "chemistry"))
        val subMat = academicDao.insertSubject(SubjectEntity(id = 3, name = "Mathematics", code = "MAT", iconName = "math"))
        val subPhy = academicDao.insertSubject(SubjectEntity(id = 4, name = "Physics", code = "PHY", iconName = "physics"))
        val subEng = academicDao.insertSubject(SubjectEntity(id = 5, name = "English", code = "ENG", iconName = "english"))
        val subKin = academicDao.insertSubject(SubjectEntity(id = 6, name = "Kinyarwanda", code = "KIN", iconName = "kinyarwanda"))

        // 4. Users
        // Student 1: Keza Ines
        val userKezaId = userDao.insert(
            UserEntity(
                id = 1,
                email = "keza.ines@student.myschool.rw",
                passwordHash = "password123",
                fullName = "Keza Ines",
                role = UserRole.STUDENT,
                phone = "+250 788 551 234"
            )
        )
        val studentKezaId = studentDao.insert(
            StudentEntity(id = 1, userId = userKezaId, registrationNumber = "RW/2026/S4/042", classId = classS4, gender = "Female")
        )

        // Student 2: Manzi David
        val userManziId = userDao.insert(
            UserEntity(
                id = 2,
                email = "manzi.david@student.myschool.rw",
                passwordHash = "password123",
                fullName = "Manzi David",
                role = UserRole.STUDENT,
                phone = "+250 788 551 235"
            )
        )
        val studentManziId = studentDao.insert(
            StudentEntity(id = 2, userId = userManziId, registrationNumber = "RW/2026/S2/118", classId = classS2, gender = "Male")
        )

        // Parent: Mukamana Claire
        val userClaireId = userDao.insert(
            UserEntity(
                id = 3,
                email = "claire.mukamana@parent.myschool.rw",
                passwordHash = "password123",
                fullName = "Mukamana Claire",
                role = UserRole.PARENT,
                phone = "+250 788 440 912"
            )
        )
        val parentClaireId = parentDao.insert(
            ParentEntity(id = 1, userId = userClaireId, occupation = "Public Health Specialist", address = "Gasabo, Kigali")
        )
        // Link Parent to both students
        parentDao.insertRelationship(ParentStudentRelationshipEntity(id = 1, parentId = parentClaireId, studentId = studentKezaId, relationshipType = "Mother"))
        parentDao.insertRelationship(ParentStudentRelationshipEntity(id = 2, parentId = parentClaireId, studentId = studentManziId, relationshipType = "Mother"))

        // Teacher 1: Murenzi Jean-Pierre
        val userTeacher1Id = userDao.insert(
            UserEntity(
                id = 4,
                email = "murenzi.jp@teacher.myschool.rw",
                passwordHash = "password123",
                fullName = "Murenzi Jean-Pierre",
                role = UserRole.TEACHER,
                phone = "+250 788 662 101"
            )
        )
        val teacher1Id = teacherDao.insert(
            TeacherEntity(id = 1, userId = userTeacher1Id, employeeNumber = "EMP-041", department = "Natural Sciences")
        )

        // Teacher 2: Uwase Chantal
        val userTeacher2Id = userDao.insert(
            UserEntity(
                id = 5,
                email = "uwase.chantal@teacher.myschool.rw",
                passwordHash = "password123",
                fullName = "Uwase Chantal",
                role = UserRole.TEACHER,
                phone = "+250 788 773 202"
            )
        )
        val teacher2Id = teacherDao.insert(
            TeacherEntity(id = 2, userId = userTeacher2Id, employeeNumber = "EMP-058", department = "Mathematics")
        )

        // Admin: Dr. Gasana Emmanuel
        userDao.insert(
            UserEntity(
                id = 6,
                email = "headmaster@myschool.rw",
                passwordHash = "password123",
                fullName = "Dr. Gasana Emmanuel",
                role = UserRole.ADMIN,
                phone = "+250 788 100 001"
            )
        )

        // Assign teachers to subjects & classes
        teacherDao.assignSubject(TeacherSubjectAssignmentEntity(id = 1, teacherId = teacher1Id, classId = classS4, subjectId = subBio))
        teacherDao.assignSubject(TeacherSubjectAssignmentEntity(id = 2, teacherId = teacher1Id, classId = classS4, subjectId = subChe))
        teacherDao.assignSubject(TeacherSubjectAssignmentEntity(id = 3, teacherId = teacher2Id, classId = classS4, subjectId = subMat))
        teacherDao.assignSubject(TeacherSubjectAssignmentEntity(id = 4, teacherId = teacher2Id, classId = classS2, subjectId = subMat))

        // 5. Assessments & Marks for Keza Ines (S4 MCB)
        // Biology CAT 1
        val assBio1 = marksDao.insertAssessment(
            AssessmentEntity(id = 1, title = "Cell Biology & Genetics CAT", type = AssessmentType.CAT_1, classId = classS4, subjectId = subBio, termId = term2Id, maxMark = 100.0, date = "2026-02-14")
        )
        marksDao.insertMark(MarkEntity(id = 1, assessmentId = assBio1, studentId = studentKezaId, studentMark = 92.0, feedback = "Exemplary understanding of Mendelian inheritance patterns.", teacherId = teacher1Id))

        // Biology Mid-Term
        val assBio2 = marksDao.insertAssessment(
            AssessmentEntity(id = 2, title = "Term 2 Mid-Term Exam", type = AssessmentType.MID_TERM, classId = classS4, subjectId = subBio, termId = term2Id, maxMark = 100.0, date = "2026-03-05")
        )
        marksDao.insertMark(MarkEntity(id = 2, assessmentId = assBio2, studentId = studentKezaId, studentMark = 88.0, feedback = "Strong work on cellular respiration diagrams.", teacherId = teacher1Id))

        // Chemistry CAT 1
        val assChe1 = marksDao.insertAssessment(
            AssessmentEntity(id = 3, title = "Chemical Bonding & Stoichiometry", type = AssessmentType.CAT_1, classId = classS4, subjectId = subChe, termId = term2Id, maxMark = 100.0, date = "2026-02-20")
        )
        marksDao.insertMark(MarkEntity(id = 3, assessmentId = assChe1, studentId = studentKezaId, studentMark = 82.0, feedback = "Accurate titration calculations, review electron configuration.", teacherId = teacher1Id))

        // Math CAT 1
        val assMat1 = marksDao.insertAssessment(
            AssessmentEntity(id = 4, title = "Advanced Trigonometry & Polynomials", type = AssessmentType.CAT_1, classId = classS4, subjectId = subMat, termId = term2Id, maxMark = 100.0, date = "2026-02-25")
        )
        marksDao.insertMark(MarkEntity(id = 4, assessmentId = assMat1, studentId = studentKezaId, studentMark = 94.0, feedback = "Outstanding accuracy and clean presentation.", teacherId = teacher2Id))

        // Physics CAT 1
        val assPhy1 = marksDao.insertAssessment(
            AssessmentEntity(id = 5, title = "Mechanics & Fluid Dynamics", type = AssessmentType.CAT_1, classId = classS4, subjectId = subPhy, termId = term2Id, maxMark = 100.0, date = "2026-03-01")
        )
        marksDao.insertMark(MarkEntity(id = 5, assessmentId = assPhy1, studentId = studentKezaId, studentMark = 85.0, feedback = "Well done on energy conservation proofs.", teacherId = teacher2Id))

        // Manzi David Marks (S2 A)
        val assManziMat = marksDao.insertAssessment(
            AssessmentEntity(id = 6, title = "Linear Equations & Coordinates CAT", type = AssessmentType.CAT_1, classId = classS2, subjectId = subMat, termId = term2Id, maxMark = 100.0, date = "2026-02-28")
        )
        marksDao.insertMark(MarkEntity(id = 6, assessmentId = assManziMat, studentId = studentManziId, studentMark = 79.0, feedback = "Good progress, keep practicing fractions.", teacherId = teacher2Id))

        // 6. Assignments
        val assign1 = learningDao.insertAssignment(
            AssignmentEntity(
                id = 1,
                title = "Genetics & DNA Replication Report",
                description = "Write a comprehensive 2-page report comparing DNA replication in prokaryotes vs eukaryotes following the Rwanda CBC curriculum guidelines.",
                classId = classS4,
                subjectId = subBio,
                teacherId = teacher1Id,
                dueDate = "2026-03-28",
                maxScore = 100
            )
        )
        learningDao.insertSubmission(
            SubmissionEntity(
                id = 1,
                assignmentId = assign1,
                studentId = studentKezaId,
                contentText = "Comprehensive comparison completed including lagging strand Okazaki fragments and DNA polymerase enzymes.",
                status = "SUBMITTED",
                obtainedScore = null,
                teacherRemarks = "Submitted on time. Pending grading."
            )
        )

        learningDao.insertAssignment(
            AssignmentEntity(
                id = 2,
                title = "Calculus Derivatives Problem Set",
                description = "Solve exercises 14 through 28 on rate of change and tangent slopes in standard textbook chapter 4.",
                classId = classS4,
                subjectId = subMat,
                teacherId = teacher2Id,
                dueDate = "2026-04-02",
                maxScore = 50
            )
        )

        // 7. Interactive Rwanda CBC Quiz
        val quiz1 = learningDao.insertQuiz(
            QuizEntity(
                id = 1,
                title = "Genetics & Cellular Biology Drill",
                description = "Secondary 4 MCB quick mastery quiz on Mendelian genetics and nucleic acids.",
                subjectId = subBio,
                classId = classS4,
                termId = term2Id,
                durationMinutes = 10,
                totalMarks = 20
            )
        )
        learningDao.insertQuizQuestion(
            QuizQuestionEntity(
                id = 1,
                quizId = quiz1,
                questionText = "Which nitrogenous base is present in RNA but absent in DNA?",
                optionA = "Thymine",
                optionB = "Uracil",
                optionC = "Guanine",
                optionD = "Cytosine",
                correctOptionIndex = 1,
                explanation = "Uracil replaces thymine in RNA molecules and pairs with adenine."
            )
        )
        learningDao.insertQuizQuestion(
            QuizQuestionEntity(
                id = 2,
                quizId = quiz1,
                questionText = "What phenotypic ratio results from a monohybrid cross of two heterozygous individuals (Bb x Bb)?",
                optionA = "1:2:1",
                optionB = "3:1",
                optionC = "9:3:3:1",
                optionD = "1:1",
                correctOptionIndex = 1,
                explanation = "A monohybrid heterozygous cross yields a 3 dominant to 1 recessive phenotypic ratio."
            )
        )
        learningDao.insertQuizQuestion(
            QuizQuestionEntity(
                id = 3,
                quizId = quiz1,
                questionText = "Where in the cell does oxidative phosphorylation occur?",
                optionA = "Cytoplasm",
                optionB = "Nucleus",
                optionC = "Inner mitochondrial membrane",
                optionD = "Endoplasmic reticulum",
                correctOptionIndex = 2,
                explanation = "The electron transport chain and ATP synthase are situated on the inner mitochondrial cristae."
            )
        )

        // 8. Learning Materials
        learningDao.insertMaterial(
            LearningMaterialEntity(
                id = 1,
                title = "Mendelian Genetics & Inheritance Patterns",
                description = "Detailed lecture slides and summary notes covering Rwanda CBC Unit 5 for S4 MCB.",
                classId = classS4,
                subjectId = subBio,
                termId = term2Id,
                topic = "Genetics & Heredity",
                contentType = MaterialContentType.PDF,
                contentUrl = "https://reb.gov.rw/curriculum/s4_bio_genetics.pdf",
                teacherId = teacher1Id
            )
        )
        learningDao.insertMaterial(
            LearningMaterialEntity(
                id = 2,
                title = "Chemical Kinetics and Catalysis Mechanisms",
                description = "Reaction rates, collision theory, and Arrhenius activation energy breakdown with worked examples.",
                classId = classS4,
                subjectId = subChe,
                termId = term2Id,
                topic = "Physical Chemistry",
                contentType = MaterialContentType.NOTE,
                contentUrl = "https://reb.gov.rw/curriculum/s4_che_kinetics.pdf",
                teacherId = teacher1Id
            )
        )
        learningDao.insertMaterial(
            LearningMaterialEntity(
                id = 3,
                title = "Differential Calculus Video Explainer",
                description = "First principles definition of derivatives and tangent gradients video masterclass.",
                classId = classS4,
                subjectId = subMat,
                termId = term2Id,
                topic = "Calculus & Limits",
                contentType = MaterialContentType.VIDEO,
                contentUrl = "https://youtube.com/watch?v=rwanda_math_calculus_intro",
                teacherId = teacher2Id
            )
        )

        // 9. Attendance
        attendanceDao.insert(AttendanceEntity(id = 1, studentId = studentKezaId, date = "2026-03-20", status = AttendanceStatus.PRESENT, termId = term2Id))
        attendanceDao.insert(AttendanceEntity(id = 2, studentId = studentKezaId, date = "2026-03-19", status = AttendanceStatus.PRESENT, termId = term2Id))
        attendanceDao.insert(AttendanceEntity(id = 3, studentId = studentKezaId, date = "2026-03-18", status = AttendanceStatus.PRESENT, termId = term2Id))
        attendanceDao.insert(AttendanceEntity(id = 4, studentId = studentKezaId, date = "2026-03-17", status = AttendanceStatus.PRESENT, termId = term2Id))
        attendanceDao.insert(AttendanceEntity(id = 5, studentId = studentKezaId, date = "2026-03-14", status = AttendanceStatus.EXCUSED, termId = term2Id, remarks = "Official school science competition participation"))
        attendanceDao.insert(AttendanceEntity(id = 6, studentId = studentKezaId, date = "2026-03-13", status = AttendanceStatus.PRESENT, termId = term2Id))

        // 10. Teacher Feedback
        feedbackDao.insert(
            TeacherFeedbackEntity(
                id = 1,
                studentId = studentKezaId,
                teacherId = teacher1Id,
                subjectId = subBio,
                termId = term2Id,
                feedbackText = "Keza has shown great maturity in scientific inquiry during our DNA isolation practical sessions. Ready for Olympiad qualifiers!",
                date = "2026-03-15",
                category = "ACADEMIC"
            )
        )
        feedbackDao.insert(
            TeacherFeedbackEntity(
                id = 2,
                studentId = studentKezaId,
                teacherId = teacher2Id,
                subjectId = subMat,
                termId = term2Id,
                feedbackText = "Active classroom contributor with clear step-by-step problem-solving rigor in trigonometry.",
                date = "2026-03-10",
                category = "EFFORT"
            )
        )

        // 11. Messaging between Parent Mukamana Claire and Teacher Murenzi Jean-Pierre
        val convId = messagingDao.insertConversation(
            ConversationEntity(
                id = 1,
                studentId = studentKezaId,
                parentId = parentClaireId,
                teacherId = teacher1Id,
                subjectId = subBio,
                lastMessageAt = System.currentTimeMillis() - 3600000,
                lastMessagePreview = "Keza is well prepared for the upcoming science practicals."
            )
        )
        messagingDao.insertMessage(
            MessageEntity(
                id = 1,
                conversationId = convId,
                senderUserId = userClaireId,
                receiverUserId = userTeacher1Id,
                content = "Good afternoon Teacher Murenzi. I wanted to follow up on Keza's preparation for the regional biology exhibition.",
                timestamp = System.currentTimeMillis() - 7200000,
                isRead = true
            )
        )
        messagingDao.insertMessage(
            MessageEntity(
                id = 2,
                conversationId = convId,
                senderUserId = userTeacher1Id,
                receiverUserId = userClaireId,
                content = "Good afternoon Mme Mukamana! Keza is well prepared for the upcoming science practicals. Her laboratory write-ups have been top of the class.",
                timestamp = System.currentTimeMillis() - 3600000,
                isRead = true
            )
        )

        // 12. Notifications
        notificationDao.insert(
            NotificationEntity(
                id = 1,
                userId = userKezaId,
                title = "New Marks Published",
                message = "Your grade for Term 2 Mid-Term Biology (88%) has been posted.",
                type = NotificationType.MARKS
            )
        )
        notificationDao.insert(
            NotificationEntity(
                id = 2,
                userId = userClaireId,
                title = "Academic Progress Update",
                message = "Keza Ines scored 92% in Genetics CAT. Overall average stands at 88.2%.",
                type = NotificationType.MARKS
            )
        )
        notificationDao.insert(
            NotificationEntity(
                id = 3,
                userId = userTeacher1Id,
                title = "New Message from Parent",
                message = "Mukamana Claire sent you a message regarding Keza Ines.",
                type = NotificationType.MESSAGE
            )
        )

        // 13. Announcements
        announcementDao.insert(
            AnnouncementEntity(
                id = 1,
                title = "Term 2 Mid-Term Parent-Teacher Consultation Day",
                content = "Dear parents and guardians, our mid-term consultation day will take place on Saturday, March 29th, from 8:30 AM to 2:00 PM on campus. Detailed grade reports will be reviewed with respective class patrons and subject teachers.",
                targetRole = "ALL",
                authorName = "Office of the Headmaster",
                date = "2026-03-18",
                priority = "URGENT"
            )
        )
        announcementDao.insert(
            AnnouncementEntity(
                id = 2,
                title = "Rwanda National Examinations Board (NESA) Registration Notice",
                content = "All S3 and S6 candidates must confirm their biodata and registration codes with the director of studies by Friday next week.",
                targetRole = "STUDENTS",
                authorName = "Director of Studies",
                date = "2026-03-12",
                priority = "NORMAL"
            )
        )

        // 14. Seed Local Cached Student Grades (for fast local / offline retrieval)
        cachedStudentGradeDao.insertCachedGrades(
            listOf(
                CachedStudentGradeEntity(
                    id = 1,
                    studentId = studentKezaId,
                    studentName = "Keza Ines",
                    registrationNumber = "RW/2026/S4/042",
                    subjectName = "Mathematics",
                    subjectCode = "MAT",
                    assessmentTitle = "Advanced Trigonometry & Polynomials CAT",
                    assessmentType = "CAT 1",
                    score = 94.0,
                    maxScore = 100.0,
                    percentage = 94.0,
                    gradeLetter = "A+",
                    teacherFeedback = "Outstanding accuracy and clean presentation.",
                    teacherName = "Uwase Chantal",
                    termName = "Term 2",
                    academicYear = "2025-2026",
                    assessmentDate = "2026-02-25",
                    syncStatus = "SYNCED",
                    offlineAvailable = true
                ),
                CachedStudentGradeEntity(
                    id = 2,
                    studentId = studentKezaId,
                    studentName = "Keza Ines",
                    registrationNumber = "RW/2026/S4/042",
                    subjectName = "Biology",
                    subjectCode = "BIO",
                    assessmentTitle = "Cell Biology & Genetics CAT",
                    assessmentType = "CAT 1",
                    score = 92.0,
                    maxScore = 100.0,
                    percentage = 92.0,
                    gradeLetter = "A",
                    teacherFeedback = "Exemplary understanding of Mendelian inheritance patterns.",
                    teacherName = "Murenzi Jean-Pierre",
                    termName = "Term 2",
                    academicYear = "2025-2026",
                    assessmentDate = "2026-02-14",
                    syncStatus = "SYNCED",
                    offlineAvailable = true
                ),
                CachedStudentGradeEntity(
                    id = 3,
                    studentId = studentKezaId,
                    studentName = "Keza Ines",
                    registrationNumber = "RW/2026/S4/042",
                    subjectName = "Biology",
                    subjectCode = "BIO",
                    assessmentTitle = "Term 2 Mid-Term Exam",
                    assessmentType = "MID_TERM",
                    score = 88.0,
                    maxScore = 100.0,
                    percentage = 88.0,
                    gradeLetter = "A-",
                    teacherFeedback = "Strong work on cellular respiration diagrams.",
                    teacherName = "Murenzi Jean-Pierre",
                    termName = "Term 2",
                    academicYear = "2025-2026",
                    assessmentDate = "2026-03-05",
                    syncStatus = "SYNCED",
                    offlineAvailable = true
                ),
                CachedStudentGradeEntity(
                    id = 4,
                    studentId = studentKezaId,
                    studentName = "Keza Ines",
                    registrationNumber = "RW/2026/S4/042",
                    subjectName = "Physics",
                    subjectCode = "PHY",
                    assessmentTitle = "Mechanics & Fluid Dynamics CAT",
                    assessmentType = "CAT 1",
                    score = 85.0,
                    maxScore = 100.0,
                    percentage = 85.0,
                    gradeLetter = "B+",
                    teacherFeedback = "Well done on energy conservation proofs.",
                    teacherName = "Uwase Chantal",
                    termName = "Term 2",
                    academicYear = "2025-2026",
                    assessmentDate = "2026-03-01",
                    syncStatus = "SYNCED",
                    offlineAvailable = true
                ),
                CachedStudentGradeEntity(
                    id = 5,
                    studentId = studentKezaId,
                    studentName = "Keza Ines",
                    registrationNumber = "RW/2026/S4/042",
                    subjectName = "Chemistry",
                    subjectCode = "CHE",
                    assessmentTitle = "Chemical Bonding & Stoichiometry CAT",
                    assessmentType = "CAT 1",
                    score = 82.0,
                    maxScore = 100.0,
                    percentage = 82.0,
                    gradeLetter = "B",
                    teacherFeedback = "Accurate titration calculations, review electron configuration.",
                    teacherName = "Murenzi Jean-Pierre",
                    termName = "Term 2",
                    academicYear = "2025-2026",
                    assessmentDate = "2026-02-20",
                    syncStatus = "SYNCED",
                    offlineAvailable = true
                ),
                CachedStudentGradeEntity(
                    id = 6,
                    studentId = studentManziId,
                    studentName = "Manzi David",
                    registrationNumber = "RW/2026/S2/118",
                    subjectName = "Mathematics",
                    subjectCode = "MAT",
                    assessmentTitle = "Linear Equations & Coordinates CAT",
                    assessmentType = "CAT 1",
                    score = 79.0,
                    maxScore = 100.0,
                    percentage = 79.0,
                    gradeLetter = "B-",
                    teacherFeedback = "Good progress, keep practicing fractions.",
                    teacherName = "Uwase Chantal",
                    termName = "Term 2",
                    academicYear = "2025-2026",
                    assessmentDate = "2026-02-28",
                    syncStatus = "SYNCED",
                    offlineAvailable = true
                )
            )
        )

        // 15. Seed Offline School Notifications (cached for offline viewing)
        cachedSchoolNotificationDao.insertCachedNotifications(
            listOf(
                CachedSchoolNotificationEntity(
                    id = 1,
                    remoteNotificationId = 101L,
                    userId = userKezaId,
                    targetRole = "STUDENT",
                    title = "Term 2 Mid-Term Report Available",
                    message = "Your official Term 2 mid-term report card has been processed. Overall grade average: 88.2%. Stored locally for offline verification.",
                    category = "ACADEMIC",
                    priority = "NORMAL",
                    publishedDate = "2026-03-21",
                    authorName = "Director of Studies",
                    isRead = false,
                    offlineAvailable = true,
                    downloadedForOffline = true
                ),
                CachedSchoolNotificationEntity(
                    id = 2,
                    remoteNotificationId = 102L,
                    userId = userKezaId,
                    targetRole = "STUDENT",
                    title = "Genetics Laboratory Practical Schedule",
                    message = "Reminder: Biology DNA extraction practical session is set for Tuesday in Science Lab 2 at 10:00 AM. Bring your lab coats and manuals.",
                    category = "ANNOUNCEMENT",
                    priority = "NORMAL",
                    publishedDate = "2026-03-20",
                    authorName = "Murenzi Jean-Pierre",
                    isRead = false,
                    offlineAvailable = true,
                    downloadedForOffline = true
                ),
                CachedSchoolNotificationEntity(
                    id = 3,
                    remoteNotificationId = 103L,
                    userId = null,
                    targetRole = "ALL",
                    title = "Term 2 Parent-Teacher Consultation Day",
                    message = "Mid-Term Consultation Day scheduled for Saturday, March 29th, from 8:30 AM to 2:00 PM on campus. Detailed grade slips available offline.",
                    category = "ANNOUNCEMENT",
                    priority = "URGENT",
                    publishedDate = "2026-03-18",
                    authorName = "Office of the Headmaster",
                    isRead = true,
                    offlineAvailable = true,
                    downloadedForOffline = true
                ),
                CachedSchoolNotificationEntity(
                    id = 4,
                    remoteNotificationId = 104L,
                    userId = userClaireId,
                    targetRole = "PARENT",
                    title = "Academic Progress Alert: Keza Ines",
                    message = "Keza scored 92% in Genetics CAT and 94% in Advanced Trigonometry. Class standing: 1st in S4 MCB.",
                    category = "ACADEMIC",
                    priority = "NORMAL",
                    publishedDate = "2026-03-19",
                    authorName = "Class Patron (S4 MCB)",
                    isRead = false,
                    offlineAvailable = true,
                    downloadedForOffline = true
                ),
                CachedSchoolNotificationEntity(
                    id = 5,
                    remoteNotificationId = 105L,
                    userId = null,
                    targetRole = "ALL",
                    title = "National Science & Robotics Competition",
                    message = "Rwanda inter-school STEM Olympiad qualifiers commence April 15th. Registered students can review preparation materials offline.",
                    category = "EXAM",
                    priority = "NORMAL",
                    publishedDate = "2026-03-15",
                    authorName = "STEM Department",
                    isRead = true,
                    offlineAvailable = true,
                    downloadedForOffline = true
                )
            )
        )
    }
}
