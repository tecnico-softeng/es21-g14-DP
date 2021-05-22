import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import ItemCombinationStatementCorrectAnswerDetails from './ItemCombinationStatementCorrectAnswerDetails';

export default class ItemCombinationStatementAnswerDetails extends StatementAnswerDetails {
    modelAnswer: string | null = null;

  constructor(jsonObj?: ItemCombinationStatementAnswerDetails) {
    super(QuestionTypes.ItemCombinationQuestion);
    if (jsonObj?.modelAnswer) {
    }
  }

  isQuestionAnswered(): boolean {
    return this.modelAnswer != null;
  }

  isAnswerCorrect(
    correctAnswerDetails: ItemCombinationStatementCorrectAnswerDetails 
  ): boolean {
    return true;
  }
}
