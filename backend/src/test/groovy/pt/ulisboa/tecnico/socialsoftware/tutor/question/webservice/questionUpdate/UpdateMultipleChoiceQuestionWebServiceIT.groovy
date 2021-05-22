package pt.ulisboa.tecnico.socialsoftware.tutor.question.webservice.questionUpdate

import com.fasterxml.jackson.databind.ObjectMapper
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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateMultipleChoiceQuestionWebServiceIT extends SpockTest{

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

    def "update a question as a student"() {
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

        and: "a new MultipleChoiceQuestionDto"
        def newQuestionDto = new QuestionDto()
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        newQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        newQuestionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def newOptionDto = new OptionDto()
        newOptionDto.setContent(OPTION_1_CONTENT)
        newOptionDto.setCorrect(false)
        def newOptions = new ArrayList<OptionDto>()
        newOptions.add(newOptionDto)
        newOptionDto = new OptionDto()
        newOptionDto.setContent(OPTION_2_CONTENT)
        newOptionDto.setCorrect(true)
        newOptions.add(newOptionDto)
        newOptionDto = new OptionDto()
        newOptionDto.setContent(OPTION_2_CONTENT)
        newOptionDto.setCorrect(true)
        newOptions.add(newOptionDto)
        newQuestionDto.getQuestionDetailsDto().setOptions(newOptions)        

        and: "an object mapper to de-serialize the questionDetailsDto correctly"
        def objectMapper = new ObjectMapper()

        when:
        response = restClient.put(
            path: '/questions/' + questionSubmission.getId(),
            body: objectMapper.writeValueAsString(newQuestionDto),
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

    def "update a question without access to the course"() {
        given: "a teacher"
        def teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.EXTERNAL)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        userRepository.save(teacher)

        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        questionService.createQuestion(courseExecution.getId(), questionDto)
        def questionSubmission = questionRepository.findAll().get(0)

        and: "a new MultipleChoiceQuestionDto"
        def newQuestionDto = new QuestionDto()
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        newQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        newQuestionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def newOptionDto = new OptionDto()
        newOptionDto.setContent(OPTION_1_CONTENT)
        newOptionDto.setCorrect(false)
        def newOptions = new ArrayList<OptionDto>()
        newOptions.add(newOptionDto)
        newOptionDto = new OptionDto()
        newOptionDto.setContent(OPTION_2_CONTENT)
        newOptionDto.setCorrect(true)
        newOptions.add(newOptionDto)
        newOptionDto = new OptionDto()
        newOptionDto.setContent(OPTION_2_CONTENT)
        newOptionDto.setCorrect(true)
        newOptions.add(newOptionDto)
        newQuestionDto.getQuestionDetailsDto().setOptions(newOptions)  

        and: "an object mapper to de-serialize the questionDetailsDto correctly"
        def objectMapper = new ObjectMapper()

        when:
        response = restClient.put(
                path: '/questions/' + questionSubmission.getId(),
                body: objectMapper.writeValueAsString(newQuestionDto),
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

    def "update a question as a teacher with access to the course"() {
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

        and: "a new MultipleChoiceQuestionDto"
        def newQuestionDto = new QuestionDto()
        newQuestionDto.setTitle(QUESTION_2_TITLE)
        newQuestionDto.setContent(QUESTION_2_CONTENT)
        newQuestionDto.setStatus(Question.Status.AVAILABLE.name())
        newQuestionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        def newOptionDto = new OptionDto()
        newOptionDto.setContent(OPTION_1_CONTENT)
        newOptionDto.setCorrect(false)
        def newOptions = new ArrayList<OptionDto>()
        newOptions.add(newOptionDto)
        newOptionDto = new OptionDto()
        newOptionDto.setContent(OPTION_2_CONTENT)
        newOptionDto.setCorrect(true)
        newOptions.add(newOptionDto)
        newOptionDto = new OptionDto()
        newOptionDto.setContent(OPTION_2_CONTENT)
        newOptionDto.setCorrect(true)
        newOptions.add(newOptionDto)
        newQuestionDto.getQuestionDetailsDto().setOptions(newOptions)  

        and: "an object mapper to de-serialize the questionDetailsDto correctly"
        def objectMapper = new ObjectMapper()

        when:
        response = restClient.put(
                path: '/questions/' + questionSubmission.getId(),
                body: objectMapper.writeValueAsString(newQuestionDto),
                requestContentType: 'application/json'
        )

        then: "the question was updated"
        response.status == 200
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_2_TITLE
        repoResult.getContent() == QUESTION_2_CONTENT
        repoResult.getImage() == null


        repoResult.getQuestionDetails().getOptions().size() == 6
        def resOption = repoResult.getQuestionDetails().getOptions()
        resOption.get(3).getContent() == OPTION_1_CONTENT
        !resOption.get(3).isCorrect()
        resOption.get(4).getContent() == OPTION_2_CONTENT
        resOption.get(4).isCorrect()
        resOption.get(5).getContent() == OPTION_2_CONTENT
        resOption.get(5).isCorrect()

        cleanup:
        questionRepository.deleteById(questionSubmission.getId())
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