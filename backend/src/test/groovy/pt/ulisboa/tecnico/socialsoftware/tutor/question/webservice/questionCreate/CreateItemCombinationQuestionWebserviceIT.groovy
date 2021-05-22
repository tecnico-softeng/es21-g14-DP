package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice.questionCreate

import groovy.json.JsonOutput
import groovyx.net.http.HttpResponseException
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import groovyx.net.http.RESTClient
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateItemCombinationQuestionWebserviceIT extends SpockTest{

    @LocalServerPort
    private int port

    def course
    def courseExecution
    def response
    def questionDto
    def options

    def setup() {
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        def itemQuestionDto = new ItemCombinationQuestionDto()

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

        when:
        response = restClient.post(
                path: "/courses/" + courseExecution.getId() + "/questions/",
                body: JsonOutput.toJson(questionDto),
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

        when:
        response = restClient.post(
                path: "/courses/" + courseExecution.getId() + "/questions/",
                body: JsonOutput.toJson(questionDto),
                requestContentType: 'application/json'
        )

        then: "exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.getStatus() == 403

        cleanup:
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