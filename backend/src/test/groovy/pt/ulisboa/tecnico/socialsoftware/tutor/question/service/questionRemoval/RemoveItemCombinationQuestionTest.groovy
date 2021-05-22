package pt.ulisboa.tecnico.socialsoftware.tutor.question.service.questionRemoval

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class RemoveItemCombinationQuestionTest extends SpockTest {
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

    def "remove a item combination question"() {
        when:
        questionService.removeQuestion(questionDto.getId())

        then: "the question is removed"
        questionRepository.count() == 0L
        associationRepository.count() == 0L
        itemRepository.count() == 0L
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
