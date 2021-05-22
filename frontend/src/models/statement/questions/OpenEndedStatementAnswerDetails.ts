import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import OpenEndedStatementCorrectAnswerDetails from '@/models/statement/questions/OpenEndedStatementCorrectAnswerDetails';

export default class OpenEndedStatementAnswerDetails extends StatementAnswerDetails {
    modelAnswer: string | null = null;

  constructor(jsonObj?: OpenEndedStatementAnswerDetails) {
    super(QuestionTypes.OpenEndedQuestion);
    if (jsonObj?.modelAnswer) {
      this.modelAnswer = jsonObj.modelAnswer;
    }
  }

  isQuestionAnswered(): boolean {
    return this.modelAnswer != null;
  }

  isAnswerCorrect(
    correctAnswerDetails: OpenEndedStatementCorrectAnswerDetails
  ): boolean {
    return (
      !!correctAnswerDetails &&
      this.modelAnswer === correctAnswerDetails.modelAnswer
    );
  }
}
