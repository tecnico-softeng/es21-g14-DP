package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice.questionUpdate

import com.fasterxml.jackson.databind.ObjectMapper
import groovy.json.JsonOutput
import groovyx.net.http.RESTClient
import groovyx.net.http.HttpResponseException
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateItemCombinationQuestionWebserviceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def questionDto
    def response

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

    def "update an item combination question as a teacher with access to the course"() {
        given: "a teacher"
        def teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        questionService.createQuestion(courseExecution.getId(), questionDto)
        def questionSubmission = questionRepository.findAll().get(0)

        and: "a new ItemCombinationQuestionDto"
        def newQuestionDto = new QuestionDto()
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        newQuestionDto.setStatus(Question.Status.AVAILABLE.name())

        def itemQuestionDto = new ItemCombinationQuestionDto()
        def itemsLeft = new ArrayList<ItemDto>()
        def itemDto = new ItemDto()
        itemDto.setContent(ITEM_1_CONTENT)
        itemsLeft.add(itemDto)
        def itemsRight = new ArrayList<ItemDto>()
        itemDto = new ItemDto()
        itemDto.setContent(ITEM_3_CONTENT)
        itemsRight.add(itemDto)
        itemQuestionDto.setGroupLeft(itemsLeft)
        itemQuestionDto.setGroupRight(itemsRight)
        itemQuestionDto.setAssociationsList([[0, 0]] as ArrayList<ArrayList<Integer>>)
        newQuestionDto.setQuestionDetailsDto(itemQuestionDto)

        when:
        response = restClient.put(
                path: '/questions/' + questionSubmission.getId(),
                body: JsonOutput.toJson(newQuestionDto),
                requestContentType: 'application/json'
        )


        then: "the  question was updated"
        response.status == 200
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getKey() == 1
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_2_TITLE
        repoResult.getContent() == QUESTION_2_CONTENT
        repoResult.getImage() == null

        cleanup:
        questionRepository.deleteById(questionSubmission.getId())
        userRepository.deleteById(teacher.getId())
        authUserRepository.delete(teacher.getAuthUser())
    }

    def "update an item combination question as a teacher without access to the course"() {
        given: "a teacher"
        def teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        questionService.createQuestion(courseExecution.getId(), questionDto)
        def questionSubmission = questionRepository.findAll().get(0)

        and: "a new ItemCombinationQuestionDto"
        def newQuestionDto = new QuestionDto()
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        newQuestionDto.setStatus(Question.Status.AVAILABLE.name())

        def itemQuestionDto = new ItemCombinationQuestionDto()
        def itemsLeft = new ArrayList<ItemDto>()
        def itemDto = new ItemDto()
        itemDto.setContent(ITEM_1_CONTENT)
        itemsLeft.add(itemDto)
        def itemsRight = new ArrayList<ItemDto>()
        itemDto = new ItemDto()
        itemDto.setContent(ITEM_3_CONTENT)
        itemsRight.add(itemDto)
        itemQuestionDto.setGroupLeft(itemsLeft)
        itemQuestionDto.setGroupRight(itemsRight)
        itemQuestionDto.setAssociationsList([[0, 0]] as ArrayList<ArrayList<Integer>>)
        newQuestionDto.setQuestionDetailsDto(itemQuestionDto)


        when:
        response = restClient.put(
                path: '/questions/' + questionSubmission.getId(),
                body: JsonOutput.toJson(newQuestionDto),
                requestContentType: 'application/json'
        )


        then: "exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.getStatus() == 403

        cleanup:
        questionRepository.deleteById(questionSubmission.getId())
        userRepository.deleteById(teacher.getId())
        authUserRepository.delete(teacher.getAuthUser())
    }

    def "cannot update an item combination question as a student with access to the course"() {
        given: "a student"
        def student = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.EXTERNAL)
        student.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        questionService.createQuestion(courseExecution.getId(), questionDto)
        def questionSubmission = questionRepository.findAll().get(0)

        and: "a new ItemCombinationQuestionDto"
        def newQuestionDto = new QuestionDto()
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        newQuestionDto.setStatus(Question.Status.AVAILABLE.name())

        def itemQuestionDto = new ItemCombinationQuestionDto()
        def itemsLeft = new ArrayList<ItemDto>()
        def itemDto = new ItemDto()
        itemDto.setContent(ITEM_1_CONTENT)
        itemsLeft.add(itemDto)
        def itemsRight = new ArrayList<ItemDto>()
        itemDto = new ItemDto()
        itemDto.setContent(ITEM_3_CONTENT)
        itemsRight.add(itemDto)
        itemQuestionDto.setGroupLeft(itemsLeft)
        itemQuestionDto.setGroupRight(itemsRight)
        itemQuestionDto.setAssociationsList([[0, 0]] as ArrayList<ArrayList<Integer>>)
        newQuestionDto.setQuestionDetailsDto(itemQuestionDto)

        when:
        response = restClient.put(
                path: '/questions/' + questionSubmission.getId(),
                body: JsonOutput.toJson(newQuestionDto),
                requestContentType: 'application/json'
        )


        then: "exception is thrown"
        def exception = thrown(HttpResponseException)
        exception.response.getStatus() == 403

        cleanup:
        questionRepository.deleteById(questionSubmission.getId())
        userRepository.deleteById(student.getId())
        authUserRepository.delete(student.getAuthUser())
    }

    def cleanup() {
        persistentCourseCleanup()
        courseExecutionRepository.dissociateCourseExecutionUsers(courseExecution.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}