package pt.ulisboa.tecnico.socialsoftware.tutor.question.service.questionUpdate

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.MultipleChoiceAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.MultipleChoiceQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.MultipleChoiceQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@DataJpaTest
class UpdateQuestionTest extends SpockTest {
    def question
    def question2
    def optionOK
    def optionKO
    def optionOK2
    def optionKO2
    def optionOK3
    def optionKO3
    def user

    def setup() {
        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

        and: 'an image'
        def image = new Image()
        image.setUrl(IMAGE_1_URL)
        image.setWidth(20)
        imageRepository.save(image)

        given: "create questions"
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        question.setNumberOfAnswers(2)
        question.setNumberOfCorrect(1)
        question.setImage(image)
        def questionDetails = new MultipleChoiceQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        question2 = new Question()
        question2.setCourse(externalCourse)
        question2.setKey(2)
        question2.setTitle(QUESTION_1_TITLE)
        question2.setContent(QUESTION_1_CONTENT)
        question2.setStatus(Question.Status.AVAILABLE)
        question2.setNumberOfAnswers(4)
        question2.setNumberOfCorrect(2)
        def question2Details = new MultipleChoiceQuestion()
        question2.setQuestionDetails(question2Details)
        questionDetailsRepository.save(question2Details)
        questionRepository.save(question2)

        and: 'some options'
        optionOK = new Option()
        optionOK.setContent(OPTION_1_CONTENT)
        optionOK.setCorrect(true)
        optionOK.setSequence(0)
        optionOK.setQuestionDetails(questionDetails)
        optionRepository.save(optionOK)

        optionKO = new Option()
        optionKO.setContent(OPTION_1_CONTENT)
        optionKO.setCorrect(false)
        optionKO.setSequence(1)
        optionKO.setQuestionDetails(questionDetails)
        optionRepository.save(optionKO)

        optionOK2 = new Option()
        optionOK2.setContent(OPTION_2_CONTENT)
        optionOK2.setCorrect(true)
        optionOK2.setSequence(2)
        optionOK2.setQuestionDetails(question2Details)
        optionRepository.save(optionOK2)

        optionKO2 = new Option()
        optionKO2.setContent(OPTION_2_CONTENT)
        optionKO2.setCorrect(false)
        optionKO2.setSequence(3)
        optionKO2.setQuestionDetails(question2Details)
        optionRepository.save(optionKO2)

        optionOK3 = new Option()
        optionOK3.setContent(OPTION_3_CONTENT)
        optionOK3.setCorrect(true)
        optionOK3.setSequence(0)
        optionOK3.setQuestionDetails(question2Details)
        optionRepository.save(optionOK3)

        optionKO3 = new Option()
        optionKO3.setContent(OPTION_3_CONTENT)
        optionKO3.setCorrect(false)
        optionKO3.setSequence(1)
        optionKO3.setQuestionDetails(question2Details)
        optionRepository.save(optionKO3)
    }

    def "update a question"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: '2 changed options'
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is changed"
        questionRepository.count() == 2L
        def result = questionRepository.findAll().get(0)
        result.getId() == question.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 2
        result.getNumberOfCorrect() == 1
        result.getDifficulty() == 50
        result.getImage() != null
        and: 'an option is changed'
        result.getQuestionDetails().getOptions().size() == 2
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK.getId()}).findAny().orElse(null)
        resOptionOne.getContent() == OPTION_2_CONTENT
        !resOptionOne.isCorrect()
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionKO.getId()}).findAny().orElse(null)
        resOptionTwo.getContent() == OPTION_1_CONTENT
        resOptionTwo.isCorrect()
    }

    def "update question with missing data"() {
        given: 'a question'
        def questionDto = new QuestionDto(question)
        questionDto.setTitle('     ')

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }

    def "update correct option in a question with answers"() {
        given: "a question with answers"
        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        QuizQuestion quizQuestion= new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question)
        quizQuestionRepository.save(quizQuestion)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setUser(user)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionOK)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        questionAnswer = new QuestionAnswer()
        answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)


        def questionDto = new QuestionDto(question)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setStatus(Question.Status.DISABLED.name())
        questionDto.setNumberOfAnswers(4)
        questionDto.setNumberOfCorrect(2)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'a optionId'
        def optionDto = new OptionDto(optionOK)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)

        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_CHANGE_ANSWERED_QUESTION
    }

    def "update multiple answer question options"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question2)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: '3 changed options'
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(optionOK3)
        optionDto.setContent(OPTION_3_CONTENT)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(optionKO3)
        optionDto.setCorrect(true)
        options.add(optionDto)
        optionDto = new OptionDto(optionOK2)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question2.getId(), questionDto)

        then: "the question is changed"
        questionRepository.count() == 2L
        def result = questionRepository.findAll().get(1)
        result.getId() == question2.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 4
        result.getNumberOfCorrect() == 2
        result.getDifficulty() == 50
        and: 'an option is changed'
        result.getQuestionDetails().getOptions().size() == 4
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK3.getId()}).findAny().orElse(null)
        resOptionOne.getContent() == OPTION_3_CONTENT
        !resOptionOne.isCorrect()
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionKO3.getId()}).findAny().orElse(null)
        resOptionTwo.getContent() == OPTION_3_CONTENT
        resOptionTwo.isCorrect()
        def resOptionThree = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK2.getId()}).findAny().orElse(null)
        resOptionThree.getContent() == OPTION_2_CONTENT
        resOptionThree.isCorrect()
    }

    def "update multiple answer question correct answers"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question2)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: '4 changed options'
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(optionOK3)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(optionKO3)
        optionDto.setCorrect(true)
        options.add(optionDto)
        optionDto = new OptionDto(optionOK2)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(optionKO2)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question2.getId(), questionDto)

        then: "the question is changed"
        questionRepository.count() == 2L
        def result = questionRepository.findAll().get(1)
        result.getId() == question2.getId()
        result.getTitle() == QUESTION_2_TITLE
        result.getContent() == QUESTION_2_CONTENT
        and: 'are not changed'
        result.getStatus() == Question.Status.AVAILABLE
        result.getNumberOfAnswers() == 4
        result.getNumberOfCorrect() == 2
        result.getDifficulty() == 50
        and: 'an option is changed'
        result.getQuestionDetails().getOptions().size() == 4
        def resOptionOne = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK3.getId()}).findAny().orElse(null)
        resOptionOne.getContent() == OPTION_3_CONTENT
        !resOptionOne.isCorrect()
        def resOptionTwo = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionKO3.getId()}).findAny().orElse(null)
        resOptionTwo.getContent() == OPTION_3_CONTENT
        resOptionTwo.isCorrect()
        def resOptionThree = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionOK2.getId()}).findAny().orElse(null)
        resOptionThree.getContent() == OPTION_2_CONTENT
        !resOptionThree.isCorrect()
        def resOptionFour = result.getQuestionDetails().getOptions().stream().filter({ option -> option.getId() == optionKO2.getId()}).findAny().orElse(null)
        resOptionFour.getContent() == OPTION_2_CONTENT
        resOptionFour.isCorrect()
    }

    def "update to only one correct answer in a multiple answer question"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question2)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())
        and: '3 changed options'
        def options = new ArrayList<OptionDto>()
        def optionDto = new OptionDto(optionOK3)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(optionKO3)
        optionDto.setCorrect(false)
        options.add(optionDto)
        optionDto = new OptionDto(optionOK2)
        optionDto.setCorrect(false)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question2.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.ONE_CORRECT_OPTION_NEEDED

    }

    def "update multiple answers in a question with answers"() {
        given: "a question with answers"
        Quiz quiz = new Quiz()
        quiz.setKey(1)
        quiz.setType(Quiz.QuizType.GENERATED.toString())
        quiz.setCourseExecution(externalCourseExecution)
        quizRepository.save(quiz)

        QuizQuestion quizQuestion= new QuizQuestion()
        quizQuestion.setQuiz(quiz)
        quizQuestion.setQuestion(question2)
        quizQuestionRepository.save(quizQuestion)

        def quizAnswer = new QuizAnswer()
        quizAnswer.setCompleted(true)
        quizAnswer.setUser(user)
        quizAnswer.setQuiz(quiz)
        quizAnswerRepository.save(quizAnswer)

        def questionAnswer = new QuestionAnswer()
        def answerDetails = new MultipleChoiceAnswer(questionAnswer, optionOK3)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)

        questionAnswer = new QuestionAnswer()
        answerDetails = new MultipleChoiceAnswer(questionAnswer, optionKO3)
        questionAnswer.setAnswerDetails(answerDetails)
        questionAnswer.setQuizQuestion(quizQuestion)
        questionAnswer.setQuizAnswer(quizAnswer)
        questionAnswerRepository.save(questionAnswer)
        answerDetailsRepository.save(answerDetails)


        def questionDto = new QuestionDto(question2)
        questionDto.setTitle(QUESTION_2_TITLE)
        questionDto.setContent(QUESTION_2_CONTENT)
        questionDto.setStatus(Question.Status.DISABLED.name())
        questionDto.setNumberOfAnswers(4)
        questionDto.setNumberOfCorrect(2)
        questionDto.setQuestionDetailsDto(new MultipleChoiceQuestionDto())

        and: 'a optionId'
        def optionDto = new OptionDto(optionOK2)
        optionDto.setContent(OPTION_2_CONTENT)
        optionDto.setCorrect(false)

        def options = new ArrayList<OptionDto>()
        options.add(optionDto)
        optionDto = new OptionDto(optionKO2)
        optionDto.setContent(OPTION_1_CONTENT)
        optionDto.setCorrect(true)
        options.add(optionDto)
        questionDto.getQuestionDetailsDto().setOptions(options)

        when:
        questionService.updateQuestion(question2.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.CANNOT_CHANGE_ANSWERED_QUESTION
    }

    def "update multiple answer question with missing data"() {
        given: 'a question'
        def questionDto = new QuestionDto(question2)
        questionDto.setTitle('     ')

        when:
        questionService.updateQuestion(question2.getId(), questionDto)

        then: "the question an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.INVALID_TITLE_FOR_QUESTION
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
