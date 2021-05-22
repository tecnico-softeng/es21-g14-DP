package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice.questionExport

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

import static groovyx.net.http.ContentType.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExportItemCombinationQuestionWebserviceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def response
    def question
    def questionSubmission

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)

        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        def itemQuestionDto = new ItemCombinationQuestionDto()

        and: 'two groups of items'
        def itemsLeft = new ArrayList<ItemDto>()
        def itemDto = new ItemDto()
        itemDto.setContent(ITEM_1_CONTENT)
        itemsLeft.add(itemDto)
        itemDto = new ItemDto()
        itemDto.setContent(ITEM_2_CONTENT)
        itemsLeft.add(itemDto)
        def itemsRight = new ArrayList<ItemDto>()
        itemDto = new ItemDto()
        itemDto.setContent(ITEM_3_CONTENT)
        itemsRight.add(itemDto)
        itemDto = new ItemDto()
        itemDto.setContent(ITEM_4_CONTENT)
        itemsRight.add(itemDto)
        itemQuestionDto.setGroupLeft(itemsLeft)
        itemQuestionDto.setGroupRight(itemsRight)
        itemQuestionDto.setAssociationsList([[0, 1], [0, 0], [1, 1]] as ArrayList<ArrayList<Integer>>)

        questionDto.setQuestionDetailsDto(itemQuestionDto)
        questionService.createQuestion(course.getId(), questionDto)
        questionSubmission = questionRepository.findAll().get(0)

        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
    }

    def "export questions as a student"() {
        given: "a student"
        def student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when:
        response = restClient.get(path: "/courses/" + courseExecution.getId() + "/questions/export")

        then: "exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.getStatus() == 403

        cleanup:
        userRepository.deleteById(student.getId())
        authUserRepository.delete(student.getAuthUser())
    }

    def "export questions without access to the course"() {
        given: "a teacher"
        def teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        when:
        response = restClient.get(path: "/courses/" + courseExecution.getId() + "/questions/export")

        then: "exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.getStatus() == 403

        cleanup:
        userRepository.deleteById(teacher.getId())
        authUserRepository.delete(teacher.getAuthUser())
    }

    def "export questions as a teacher with access to the course"() {
        given: "a teacher"
        def teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: 'prepare request response'
        restClient.handler.failure = { resp, reader ->
            [response:resp, reader:reader]
        }
        restClient.handler.success = { resp, reader ->
            [response:resp, reader:reader]
        }

        when:
        def map = restClient.get(path: "/courses/" + courseExecution.getId() + "/questions/export")

        then: "the response status is OK"
        assert map['response'].status == 200
        assert map['reader'] != null

        cleanup:
        userRepository.deleteById(teacher.getId())
        authUserRepository.delete(teacher.getAuthUser())
    }

    def cleanup() {
        persistentCourseCleanup()
        questionRepository.deleteById(questionSubmission.getId())
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
