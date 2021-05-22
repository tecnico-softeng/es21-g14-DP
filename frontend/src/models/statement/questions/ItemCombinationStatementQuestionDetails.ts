import Association from '@/models/management/Association';
import Item from '@/models/management/Item';
import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class ItemCombinationStatementQuestionDetails extends StatementQuestionDetails {
  groupLeft: Item[] = [];
  groupRight: Item[] = [];
  associations: Association[] = [];


  constructor(jsonObj?: ItemCombinationStatementQuestionDetails) {
    super(QuestionTypes.ItemCombinationQuestion);
    if (jsonObj) {
      if (jsonObj?.groupLeft) {  
        this.groupLeft = jsonObj.groupLeft.map(
          (item: Item) => new Item(item)
        );
      }
      if (jsonObj?.groupRight) {  
        this.groupRight = jsonObj.groupRight.map(
          (item: Item) => new Item(item)
        );
      }
      if (jsonObj?.associations) {  
        this.associations = jsonObj.associations.map(
          (a: Association) => new Association(a)
        );
      }
    }
  }
}
