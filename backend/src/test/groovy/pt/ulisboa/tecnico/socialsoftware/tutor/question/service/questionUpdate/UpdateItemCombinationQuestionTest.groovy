package pt.ulisboa.tecnico.socialsoftware.tutor.question.service.questionUpdate

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssociationDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class UpdateItemCombinationQuestionTest extends SpockTest {
    QuestionDto questionDto

    def setup() {
        given: "a questionDto"
        questionDto = new QuestionDto()
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

        questionDto = questionService.createQuestion(externalCourse.getId(), questionDto)
    }

    def "update item combination question add an option"(){
        given: "a changed questionDto"
        def itemQuestionDto = (ItemCombinationQuestionDto) questionDto.getQuestionDetailsDto()
        def itemsRight = itemQuestionDto.getGroupRight()

        def itemDto = new ItemDto()
        itemDto.setContent(ITEM_5_CONTENT)
        itemsRight.add(itemDto)
        itemQuestionDto.setGroupRight(itemsRight)
        itemQuestionDto.addAssociation(1, 2)

        questionDto.setQuestionDetailsDto(itemQuestionDto)

        when:
        questionService.updateQuestion(questionDto.getId(), questionDto)

        then: "an item is added"
        questionRepository.count() == 1L
        def result = (Question) questionRepository.findAll().get(0)
        def resultQuestionDetail = (ItemCombinationQuestion) result.getQuestionDetails()
        result.getId() == questionDto.getId()
        resultQuestionDetail.getAssociations().get(3).getLeft() == 1
        resultQuestionDetail.getAssociations().get(3).getRight() == 2
        resultQuestionDetail.getGroupRight().size() == 3
        resultQuestionDetail.getAssociations().size() == 4

        and: 'are not changed'
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getStatus() == Question.Status.AVAILABLE
        result.getImage() == null
        result.getQuestionDetailsDto().getGroupLeft().size() == 2
        result.getQuestionDetailsDto().getGroupLeft().get(0).getContent() == ITEM_1_CONTENT
        result.getQuestionDetailsDto().getGroupRight().get(1).getContent() == ITEM_4_CONTENT
        result.getQuestionDetailsDto().getAssociations().get(0).getLeft() == 0
        result.getQuestionDetailsDto().getAssociations().get(0).getRight() == 1
        result.getQuestionDetailsDto().getAssociations().get(1).getLeft() == 0
        result.getQuestionDetailsDto().getAssociations().get(1).getRight() == 0
        result.getQuestionDetailsDto().getAssociations().get(2).getLeft() == 1
        result.getQuestionDetailsDto().getAssociations().get(2).getRight() == 1



    }

    def "update correct matching in an item combination question"(){
        given: "a changed questionDto"
        def itemQuestionDto = (ItemCombinationQuestionDto) questionDto.getQuestionDetailsDto()
        def itemsRight = itemQuestionDto.getGroupRight()

        def itemDto = new ItemDto()
        itemDto.setContent(ITEM_5_CONTENT)
        itemsRight.add(itemDto)
        itemQuestionDto.setGroupRight(itemsRight)
        itemQuestionDto.remAssociation(0,1)
        itemQuestionDto.addAssociation(1, 0)

        when:
        questionService.updateQuestion(questionDto.getId(), questionDto)

        then: "matching is updated"
        questionRepository.count() == 1L
        def result = (Question) questionRepository.findAll().get(0)
        result.getQuestionDetailsDto().getAssociations().get(2).getLeft() == 1
        result.getQuestionDetailsDto().getAssociations().get(2).getRight() == 0

        and: 'are not changed'
        result.getId() != null
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetailsDto().getGroupLeft().size() == 2
        result.getQuestionDetailsDto().getGroupRight().size() == 3
        result.getQuestionDetailsDto().getGroupLeft().get(0).getContent() == ITEM_1_CONTENT
        result.getQuestionDetailsDto().getGroupRight().get(1).getContent() == ITEM_4_CONTENT
        result.getQuestionDetailsDto().getAssociations().size() == 3
        result.getQuestionDetailsDto().getAssociations().get(0).getLeft() == 0
        result.getQuestionDetailsDto().getAssociations().get(0).getRight() == 0
        result.getQuestionDetailsDto().getAssociations().get(1).getLeft() == 1
        result.getQuestionDetailsDto().getAssociations().get(1).getRight() == 1

    }

    def "cannot update item combination question making both group have less than 2 items"(){
        given: "a changed questionDto"
        def itemQuestionDto = (ItemCombinationQuestionDto) questionDto.getQuestionDetailsDto()
        def itemsLeft = (List<ItemDto>) itemQuestionDto.getGroupLeft()
        def itemsRight = (List<ItemDto>) itemQuestionDto.getGroupRight()

        itemsLeft.remove(0)
        itemQuestionDto.setGroupLeft(itemsLeft)
        itemsRight.remove(0)
        itemQuestionDto.setGroupRight(itemsRight)


        questionDto.setQuestionDetailsDto(itemQuestionDto)

        when:
        questionService.updateQuestion(questionDto.getId(), questionDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_TWO_ITEMS_NEEDED

    }

    def "cannot update item combination question making one group have 0 item"(){
        given: "a changed questionDto"
        def itemQuestionDto = (ItemCombinationQuestionDto) questionDto.getQuestionDetailsDto()
        def itemsLeft = (List<ItemDto>) itemQuestionDto.getGroupLeft()

        itemsLeft.clear()
        itemQuestionDto.setGroupLeft(itemsLeft)

        questionDto.setQuestionDetailsDto(itemQuestionDto)

        when:
        questionService.updateQuestion(questionDto.getId(), questionDto)

        then: "an exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.NO_EMPTY_GROUP_ALLOWED
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
