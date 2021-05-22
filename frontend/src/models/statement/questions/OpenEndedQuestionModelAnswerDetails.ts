import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenEndedQuestionModelAnswerDetails extends StatementCorrectAnswerDetails {
  public modelAnswer!: string;

  constructor(jsonObj?: OpenEndedQuestionModelAnswerDetails) {
    super(QuestionTypes.OpenEndedQuestion);
    if (jsonObj) {
      this.modelAnswer = jsonObj.modelAnswer || '';
    }
  }
}
