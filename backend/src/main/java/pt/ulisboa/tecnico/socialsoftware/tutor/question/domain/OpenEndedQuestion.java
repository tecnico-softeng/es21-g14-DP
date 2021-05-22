package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;


import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OpenEndedQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;

import javax.persistence.*;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NO_MODEL_ANSWER;

@Entity
@DiscriminatorValue(Question.QuestionTypes.OPEN_ENDED_QUESTION)
public class OpenEndedQuestion extends QuestionDetails {
    @Column(columnDefinition = "TEXT")
    private String modelAnswer;

    public OpenEndedQuestion() {
        super();
    }

    public OpenEndedQuestion(Question question, OpenEndedQuestionDto questionDto) {
        super(question);
        update(questionDto);
    }

    public String getModelAnswer() {
        return modelAnswer;
    }

    public void setModelAnswer(String modelAnswer) {
        if (modelAnswer == null || modelAnswer.trim().isEmpty()) {
            throw new TutorException(NO_MODEL_ANSWER);
        }
        this.modelAnswer = "";
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    public void update(OpenEndedQuestionDto questionDetails) {
        setModelAnswer(questionDetails.getModelAnswer());
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new OpenEndedQuestionDto(this);
    }

    // TODO - Everything under this comment needs to be implemented
    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        // Will be implemented in the final project hand in
        return null;
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        // Will be implemented in the final project hand in
        return null;
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        // Will be implemented in the final project hand in
        return null;
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        // Will be implemented in the final project hand in
        return null;
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        // Will be implemented in the final project hand in
        return null;
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        // Will be implemented in the final project hand in
        return null;
    }

    @Override
    public String toString() {
        return "OpenEndedQuestion{" +
                "modelAnswer=" + modelAnswer +
                '}';
    }
}
