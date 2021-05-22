<template>
  <ul>
    <h3 id="groupLeftH">Group Left:</h3>
    <li id="groupLeft" v-for="item in questionDetails.groupLeft" :key="item.id">
      <span
          v-html="
          convertMarkDown(item.content)
        "
      />
    </li>
    <h3 id="groupRightH">Group Right:</h3>
    <li id="groupRight" v-for="item in questionDetails.groupRight" :key="item.id">
      <span
          v-html="
          convertMarkDown(
            item.content
          )
        "
      />
    </li>
    <h3>Associations:</h3>
    <li v-for="association in questionDetails.associations" :key="association.id">
      <span
          v-html=" questionDetails.groupLeft[association.left].content + ' -> ' + questionDetails.groupRight[association.right].content"
      />
    </li>
  </ul>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';
import ItemCombinationAnswerDetails from '@/models/management/questions/ItemCombinationAnswerDetails';
@Component
export default class ItemCombinationView extends Vue {
  @Prop() readonly questionDetails!: ItemCombinationQuestionDetails;
  @Prop() readonly answerDetails?: ItemCombinationAnswerDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
