export default class Association {
  id: number | null = null;
  left: number = 0;
  right: number = 0;

  constructor(jsonObj?: Association) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.left = jsonObj.left;
      this.right = jsonObj.right;
    }
  }
  
}
