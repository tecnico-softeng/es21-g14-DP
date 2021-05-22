export default class Option {
  id: number | null = null;
  sequence!: number | null;
  content: string = '';
  correct: boolean = false;
  relevance: number = 0;

  constructor(jsonObj?: Option) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.sequence = jsonObj.sequence;
      this.content = jsonObj.content;
      this.relevance = jsonObj.relevance;
      this.correct = jsonObj.correct;
      if (jsonObj.correct){
        this.relevance = jsonObj.relevance;
      }
      else{
        jsonObj.relevance = 0;
        this.relevance = jsonObj.relevance;
      }
    }
  }
}
