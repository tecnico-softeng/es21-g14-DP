<template>
  <span
      v-html="convertMarkDown('**[★]**' + questionDetails.modelAnswer)"
  />
</template>

<script lang="ts">

import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Image from '@/models/management/Image';
import OpenEndedQuestionDetails from '@/models/management/questions/OpenEndedQuestionDetails';
import OpenEndedQuestionAnswerDetails from '@/models/management/questions/OpenEndedAnswerDetails';


@Component
export default class OpenEndedView extends Vue {
  @Prop() readonly questionDetails!: OpenEndedQuestionDetails;
  @Prop() readonly answerDetails?: OpenEndedQuestionAnswerDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>