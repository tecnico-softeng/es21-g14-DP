<template>
    <v-row>
        <v-textarea
          v-model="sQuestionDetails.modelAnswer"
          label="Model answer"
          :rules="[v => !!v || 'Model answer is required']"
          auto-grow
          required
          data-cy="openEndedQuestionModelAnswerInput"
          rows="4"
        />
    </v-row>
</template>

<script lang="ts">
import { Component, Model, PropSync, Vue, Watch } from 'vue-property-decorator';
import OpenEndedQuestionDetails from '@/models/management/questions/OpenEndedQuestionDetails';

@Component
export default class OpenEndedCreate extends Vue {

  @PropSync('questionDetails', { type: OpenEndedQuestionDetails })
  sQuestionDetails!: OpenEndedQuestionDetails;


}
</script>
