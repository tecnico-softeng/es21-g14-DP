package pt.ulisboa.tecnico.socialsoftware.tutor.question.service.questionCreation

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto


@DataJpaTest
class CreateItemCombinationQuestionTest extends SpockTest {

    def "create an item combination question"() {
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

        when:
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct data is sent back"
        rawResult instanceof QuestionDto
        def result = (QuestionDto) rawResult
        result.getId() != null
        result.getStatus() == Question.Status.AVAILABLE.toString()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetailsDto().getGroupLeft().size() == 2
        result.getQuestionDetailsDto().getGroupRight().size() == 2
        result.getQuestionDetailsDto().getGroupLeft().get(0).getContent() == ITEM_1_CONTENT
        result.getQuestionDetailsDto().getGroupRight().get(1).getContent() == ITEM_4_CONTENT
        result.getQuestionDetailsDto().getAssociations().size() == 3
        result.getQuestionDetailsDto().getAssociations().get(0).getLeft() == 0
        result.getQuestionDetailsDto().getAssociations().get(0).getRight() == 1
        result.getQuestionDetailsDto().getAssociations().get(1).getLeft() == 0
        result.getQuestionDetailsDto().getAssociations().get(1).getRight() == 0
        result.getQuestionDetailsDto().getAssociations().get(2).getLeft() == 1
        result.getQuestionDetailsDto().getAssociations().get(2).getRight() == 1


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

        def repoCode = (ItemCombinationQuestion) repoResult.getQuestionDetails()
        repoCode.getQuestionDetailsDto().getGroupLeft().size() == 2
        repoCode.getQuestionDetailsDto().getGroupRight().size() == 2
        repoCode.getQuestionDetailsDto().getGroupLeft().get(0).getContent() == ITEM_1_CONTENT
        repoCode.getQuestionDetailsDto().getGroupLeft().get(1).getContent() == ITEM_2_CONTENT
        repoCode.getQuestionDetailsDto().getGroupRight().get(0).getContent() == ITEM_3_CONTENT
        repoCode.getQuestionDetailsDto().getGroupRight().get(1).getContent() == ITEM_4_CONTENT
    }

    def "create an item combination question with a group of 2 items and another with 3"() {
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
        itemDto = new ItemDto()
        itemDto.setContent(ITEM_3_CONTENT)
        itemsLeft.add(itemDto)

        def itemsRight = new ArrayList<ItemDto>()
        itemDto = new ItemDto()
        itemDto.setContent(ITEM_4_CONTENT)
        itemsRight.add(itemDto)
        itemDto = new ItemDto()
        itemDto.setContent(ITEM_5_CONTENT)
        itemsRight.add(itemDto)

        itemQuestionDto.setGroupLeft(itemsLeft)
        itemQuestionDto.setGroupRight(itemsRight)
        itemQuestionDto.setAssociationsList([[0, 0], [0, 1], [0, 2], [1, 1]] as ArrayList<ArrayList<Integer>>)

        questionDto.setQuestionDetailsDto(itemQuestionDto)

        when:
        def rawResult = questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the correct data is sent back"
        rawResult instanceof QuestionDto
        def result = (QuestionDto) rawResult
        result.getId() != null
        result.getStatus() == Question.Status.AVAILABLE.toString()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetailsDto().getGroupLeft().size() == 3
        result.getQuestionDetailsDto().getGroupRight().size() == 2
        result.getQuestionDetailsDto().getGroupLeft().get(2).getContent() == ITEM_3_CONTENT
        result.getQuestionDetailsDto().getGroupRight().get(1).getContent() == ITEM_5_CONTENT
        result.getQuestionDetailsDto().getAssociations().size() == 4

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

        def repoCode = (ItemCombinationQuestion) repoResult.getQuestionDetails()
        repoCode.getQuestionDetailsDto().getGroupLeft().size() == 3
        repoCode.getQuestionDetailsDto().getGroupRight().size() == 2

    }

    def "cannot create an item combination question with an empty item group"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        def itemQuestionDto = new ItemCombinationQuestionDto()

        and: 'a group with one item and another empty'
        def itemsLeft = new ArrayList<ItemDto>()

        def itemDto = new ItemDto()
        itemDto.setContent(ITEM_1_CONTENT)
        itemsLeft.add(itemDto)
        def itemsRight = new ArrayList<ItemDto>()

        itemQuestionDto.setGroupLeft(itemsLeft)
        itemQuestionDto.setGroupRight(itemsRight)
        itemQuestionDto.setAssociationsList([])
        questionDto.setQuestionDetailsDto(itemQuestionDto)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_EMPTY_GROUP_ALLOWED
    }

    def "cannot create an item combination question without at least one of the groups having two or more items"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        def itemQuestionDto = new ItemCombinationQuestionDto()

        and: 'a group with one item and another empty'
        def itemsLeft = new ArrayList<ItemDto>()
        def itemDto = new ItemDto()
        itemDto.setContent(ITEM_1_CONTENT)
        itemsLeft.add(itemDto)

        def itemsRight = new ArrayList<ItemDto>()
        itemDto = new ItemDto()
        itemDto.setContent(ITEM_2_CONTENT)
        itemsRight.add(itemDto)

        itemQuestionDto.setGroupLeft(itemsLeft)
        itemQuestionDto.setGroupRight(itemsRight)
        itemQuestionDto.setAssociationsList([[0,0]] as ArrayList<ArrayList<Integer>>)
        questionDto.setQuestionDetailsDto(itemQuestionDto)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_TWO_ITEMS_NEEDED
    }

    def "cannot create an item combination question without a valid correct answer"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        def itemQuestionDto = new ItemCombinationQuestionDto()

        and: 'a group with one item and another empty'
        def itemsLeft = new ArrayList<ItemDto>()
        def itemDto = new ItemDto()
        itemDto.setContent(ITEM_1_CONTENT)
        itemsLeft.add(itemDto)

        def itemsRight = new ArrayList<ItemDto>()
        itemDto = new ItemDto()
        itemDto.setContent(ITEM_2_CONTENT)
        itemsRight.add(itemDto)
        itemDto = new ItemDto()
        itemDto.setContent(ITEM_3_CONTENT)
        itemsRight.add(itemDto)

        itemQuestionDto.setGroupLeft(itemsLeft)
        itemQuestionDto.setGroupRight(itemsRight)
        itemQuestionDto.setAssociationsList([])
        questionDto.setQuestionDetailsDto(itemQuestionDto)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.VALID_CORRECT_ANSWER_NEEDED
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
