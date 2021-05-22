import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class OpenEndedQuestionDetails extends QuestionDetails {
  modelAnswer: string = '';

  constructor(jsonObj?: OpenEndedQuestionDetails) {
    super(QuestionTypes.OpenEndedQuestion);
    if (jsonObj) {
      this.modelAnswer = jsonObj.modelAnswer;
    }
  }

  setAsNew(): void {
    this.modelAnswer = '';
  }
}
