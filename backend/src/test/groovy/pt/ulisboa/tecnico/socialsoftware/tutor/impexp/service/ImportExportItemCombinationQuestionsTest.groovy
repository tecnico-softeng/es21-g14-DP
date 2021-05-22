package pt.ulisboa.tecnico.socialsoftware.tutor.impexp.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.ItemCombinationQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemCombinationQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto

@DataJpaTest
class ImportExportItemCombinationQuestionsTest extends SpockTest {
    Integer questionId

    def setup() {
        def questionDto = new QuestionDto()
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())

        ItemCombinationQuestionDto itemCombinationQuestionDto = new ItemCombinationQuestionDto()

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

        itemCombinationQuestionDto.setGroupLeft(itemsLeft)
        itemCombinationQuestionDto.setGroupRight(itemsRight)
        itemCombinationQuestionDto.setAssociations([[0, 1], [0, 0], [1, 1]] as ArrayList<ArrayList<Integer>>)

        questionDto.setQuestionDetailsDto(itemCombinationQuestionDto)
        questionId = questionService.createQuestion(externalCourse.getId(), questionDto).getId()

    }

    def 'export and import questions to xml'() {
        given: 'a xml with questions'
        def questionsXml = questionService.exportQuestionsToXml()
        print questionsXml

        and: 'a clean database'
        questionService.removeQuestion(questionId)

        when:
        questionService.importQuestionsFromXml(questionsXml)

        then:
        questionRepository.findQuestions(externalCourse.getId()).size() == 1
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
        repoCode.getQuestionDetailsDto().getAssociations().size() == 3
        repoCode.getQuestionDetailsDto().getAssociations().get(0).getLeft() == 0
        repoCode.getQuestionDetailsDto().getAssociations().get(0).getRight() == 1
        repoCode.getQuestionDetailsDto().getAssociations().get(1).getLeft() == 0
        repoCode.getQuestionDetailsDto().getAssociations().get(1).getRight() == 0
        repoCode.getQuestionDetailsDto().getAssociations().get(2).getLeft() == 1
        repoCode.getQuestionDetailsDto().getAssociations().get(2).getRight() == 1

    }

    def 'export to latex'() {
        when:
        def questionsLatex = questionService.exportQuestionsToLatex()

        then:
        questionsLatex != null
    }

        @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
