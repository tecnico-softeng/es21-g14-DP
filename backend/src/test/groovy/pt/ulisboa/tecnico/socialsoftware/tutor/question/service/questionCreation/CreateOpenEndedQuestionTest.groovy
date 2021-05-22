package pt.ulisboa.tecnico.socialsoftware.tutor.question.service.questionCreation

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto


@DataJpaTest
class CreateOpenEndedQuestionTest extends SpockTest {

    def "create an open ended question with a model answer"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def openEndedQuestionDto = new OpenEndedQuestionDto()
        openEndedQuestionDto.setModelAnswer(MODEL_ANSWER_1)
        questionDto.setQuestionDetailsDto(openEndedQuestionDto)

        when: "we create a question"
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct question is inside the repository"
        questionRepository.count() == 1L
        def repoResult = questionRepository.findAll().get(0)
        repoResult.getId() != null
        repoResult.getKey() == 1
        repoResult.getStatus() == Question.Status.AVAILABLE
        repoResult.getTitle() == QUESTION_1_TITLE
        repoResult.getContent() == QUESTION_1_CONTENT
        repoResult.getImage() == null
        repoResult.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(repoResult)

        def repoQuestion = (OpenEndedQuestion) repoResult.getQuestionDetails()
        repoQuestion.getModelAnswer() == MODEL_ANSWER_1;
    }

    def "cannot create an open ended question without a model answer"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        questionDto.setQuestionDetailsDto(new OpenEndedQuestionDto())

        when: "we create a question"
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_MODEL_ANSWER
    }

    def "cannot create an open ended question with a blank model answer"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        def openEndedQuestionDto = new OpenEndedQuestionDto()
        openEndedQuestionDto.setModelAnswer("")
        questionDto.setQuestionDetailsDto(openEndedQuestionDto)

        when: "we create a question"
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_MODEL_ANSWER
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
