import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class ItemCombinationStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {

  constructor(jsonObj?: ItemCombinationStatementCorrectAnswerDetails) {
    super(QuestionTypes.ItemCombinationQuestion);
    if (jsonObj) {
    }
  }
}
