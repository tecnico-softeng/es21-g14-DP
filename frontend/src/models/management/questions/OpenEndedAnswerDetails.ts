import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import OpenEndedQuestionDetails from './OpenEndedQuestionDetails';

export default class OpenEndedAnswerDetails extends AnswerDetails {
  modelAnswer = '';

  constructor(jsonObj?: OpenEndedAnswerDetails) {
    super(QuestionTypes.OpenEndedQuestion);
    if (jsonObj?.modelAnswer) {
      this.modelAnswer = jsonObj.modelAnswer;
    }
  }

  isCorrect(questionDetails: OpenEndedQuestionDetails): boolean {
    return questionDetails.modelAnswer === this.modelAnswer;
  }

  answerRepresentation(questionDetails: OpenEndedQuestionDetails): string {
    return questionDetails.modelAnswer;
  }
}
