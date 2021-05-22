import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenEndedStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  modelAnswer!: string;

  constructor(jsonObj?: OpenEndedStatementCorrectAnswerDetails) {
    super(QuestionTypes.OpenEndedQuestion);
    if (jsonObj?.modelAnswer) {
      this.modelAnswer = jsonObj.modelAnswer || '';
    }
  }
}
