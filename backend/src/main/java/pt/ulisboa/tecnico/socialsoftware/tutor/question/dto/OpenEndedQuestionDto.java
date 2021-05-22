package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.OpenEndedQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;


public class OpenEndedQuestionDto extends QuestionDetailsDto{
    private String modelAnswer;

    public OpenEndedQuestionDto() {
    }

    public OpenEndedQuestionDto(OpenEndedQuestion question) {
        modelAnswer = question.getModelAnswer();
    }

    public String getModelAnswer() {
        return modelAnswer;
    }

    public void setModelAnswer(String modelAnswer) {
        this.modelAnswer = modelAnswer;
    }

    @Override
    public void update(OpenEndedQuestion question) {
        question.update(this);
    }

    @Override
    public String toString() {
        return "OpenEndedQuestionDto{" +
                "modelAnswer='" + modelAnswer + "'";
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new OpenEndedQuestion(question, this);
    }
}
