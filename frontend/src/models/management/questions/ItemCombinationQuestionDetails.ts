import QuestionDetails from '@/models/management/questions/QuestionDetails';
import Item from '@/models/management/Item';
import Association from '@/models/management/Association';


import { QuestionTypes } from '@/services/QuestionHelpers';

export default class ItemCombinationQuestionDetails extends QuestionDetails {
  groupRight: Item[] = [];
  groupLeft: Item[] = [];
  associations: Association[] = [];

  constructor(jsonObj?: ItemCombinationQuestionDetails) {
    super(QuestionTypes.ItemCombinationQuestion);
    if (jsonObj) {
      this.groupLeft = jsonObj.groupLeft.map(
        (item: Item) => new Item(item)
      );
      this.groupRight = jsonObj.groupRight.map(
        (item: Item) => new Item(item)
      );
      this.associations = jsonObj.associations.map(
        (a: Association) => new Association(a)
      );
    }
  }

  setAsNew(): void {

  }
}
