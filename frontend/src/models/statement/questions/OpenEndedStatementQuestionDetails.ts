import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenEndedQuestionModelAnswerDetails extends StatementQuestionDetails {
  modelAnswer = '';

  constructor(jsonObj?: OpenEndedQuestionModelAnswerDetails) {
    super(QuestionTypes.OpenEndedQuestion);
    if (jsonObj?.modelAnswer) {
      this.modelAnswer = jsonObj.modelAnswer;
    }
  }
}
