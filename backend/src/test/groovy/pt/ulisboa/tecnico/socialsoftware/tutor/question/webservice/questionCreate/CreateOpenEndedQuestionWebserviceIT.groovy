package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice.questionCreate

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonOutput
import groovyx.net.http.HttpResponseException
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import groovyx.net.http.RESTClient
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateOpenEndedQuestionWebserviceIT extends SpockTest{

    @LocalServerPort
    private int port

    def course
    def courseExecution
    def response
    def questionDto

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)

        def openEndedQuestionDto = new OpenEndedQuestionDto()
        openEndedQuestionDto.setModelAnswer(MODEL_ANSWER_1)
        questionDto.setQuestionDetailsDto(openEndedQuestionDto)
    }

    def "create a question as a student"() {
        given: "a student"
        def student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "an object mapper to de-serialize the questionDetailsDto correctly"
        def objectMapper = new ObjectMapper()

        when:
        response = restClient.post(
                path: "/courses/" + courseExecution.getId() + "/questions/",
                body: objectMapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.getStatus() == 403

        cleanup:
        userRepository.deleteById(student.getId())
        authUserRepository.delete(student.getAuthUser())
    }

    def "create a question without access to the course"() {
        given: "a teacher"
        def teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "an object mapper to de-serialize the questionDetailsDto correctly"
        def objectMapper = new ObjectMapper()

        when:
        response = restClient.post(
                path: "/courses/" + courseExecution.getId() + "/questions/",
                body: objectMapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.getStatus() == 403

        cleanup:
        userRepository.deleteById(teacher.getId())
        authUserRepository.delete(teacher.getAuthUser())
    }

    def "create a question as a teacher with access to the course"() {
        given: "a teacher"
        def teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "an object mapper to de-serialize the questionDetailsDto correctly"
        def objectMapper = new ObjectMapper()

        when:
        response = restClient.post(
                path: "/courses/" + courseExecution.getId() + "/questions/",
                body: objectMapper.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then: "a new question was added to the database"
        response.status == 200
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_1_TITLE
        repoResult.getContent() == QUESTION_1_CONTENT
        repoResult.getImage() == null

        def repoQuestion = (OpenEndedQuestion) repoResult.getQuestionDetails()
        repoQuestion.getModelAnswer() == MODEL_ANSWER_1

        cleanup:
        questionRepository.deleteById(repoResult.getId())
        userRepository.deleteById(teacher.getId())
        authUserRepository.delete(teacher.getAuthUser())
    }

    def cleanup() {
        persistentCourseCleanup()
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
