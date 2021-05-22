import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import ItemCombinationQuestionDetails from './ItemCombinationQuestionDetails';
import OpenEndedQuestionDetails from './OpenEndedQuestionDetails';

export default class ItemCombinationAnswerDetails extends AnswerDetails {
  modelAnswer = '';

  constructor(jsonObj?: ItemCombinationAnswerDetails) {
    super(QuestionTypes.ItemCombinationQuestion);
    if (jsonObj) {
    }
  }

  isCorrect(questionDetails: ItemCombinationQuestionDetails): boolean {
    return true;
  }

  answerRepresentation(questionDetails: ItemCombinationQuestionDetails): string {
    return "";
  }
}
