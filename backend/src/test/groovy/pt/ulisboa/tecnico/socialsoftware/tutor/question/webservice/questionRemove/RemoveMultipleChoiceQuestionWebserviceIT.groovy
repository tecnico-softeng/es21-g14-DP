package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice.questionRemove

import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RemoveMultipleChoiceQuestionWebserviceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def response
    def questionDto
    def optionDto
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
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def optionDto = new OptionDto()
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(true)
        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        optionDto = new OptionDto()
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)
    }

    def "remove a question as a student"() {
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

        when:
        response = restClient.delete(
            path: '/questions/' + questionSubmission.getId(),
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

    def "remove a question without access to the course"() {
        given: "a teacher"
        def teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        questionService.createQuestion(courseExecution.getId(), questionDto)
        def questionSubmission = questionRepository.findAll().get(0)

        when:
        response = restClient.delete(
                path: '/questions/' + questionSubmission.getId(),
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

    def "remove a question as a teacher with access to the course"() {
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

        when:
        response = restClient.delete(
                path: '/questions/' + questionSubmission.getId(),
                requestContentType: 'application/json'
        )

        then: "the question was removed"
        response.status == 200
        questionRepository.count() == 0L
        imageRepository.count() == 0L
        optionRepository.count() == 0L

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
