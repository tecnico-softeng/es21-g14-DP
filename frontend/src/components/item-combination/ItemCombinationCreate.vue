<template>
  <v-row style="position: relative">
    <v-col cols="3" offset="1" style="text-align: center">
      <h3>Group Left</h3>
    </v-col>
    <v-col cols="3" offset="4" style="text-align: center">
      <h3>Group Right</h3>
    </v-col>

    <v-col cols="3" offset="1">
      <v-row
        v-for="(item, index) in sQuestionDetails.groupLeft"
        :key="index"
        data-cy="questionOptionsInput"
      >
        <v-btn icon class="remove-btn" color="red" @click="removeItem(0, index)"
          ><v-icon style="padding-left: 0px">close</v-icon>
        </v-btn>
        <v-card class="item" elevation="2" style="margin-right: 10px" outlined
          ><v-card-subtitle>{{ item.content }}</v-card-subtitle></v-card
        >
        <v-btn
          fab
          dark
          small
          color="primary"
          class="bullet"
          style="height: 14px; width: 14px; padding-left: px"
          :id="'item-0-' + index"
          @click="alert($event, 0, index)"
        >
        </v-btn>
      </v-row>
      <v-row
        >
                <v-text-field   label="Item Content" style="z-index: 1; margin-right: 10px" v-model="left"></v-text-field>
<v-btn
          class="add"
          color="green"
          @click="addItem(0)"
          data-cy="addOptionMultipleChoice"
          >Add item</v-btn
        ></v-row
      >
    </v-col>

    <v-col cols="3" offset="4">
      <v-row
        v-for="(item, index) in sQuestionDetails.groupRight"
        :key="index"
        data-cy="questionOptionsInput"
        style="position: relative"
      >
        <v-btn
          fab
          dark
          small
          color="primary"
          class="bullet"
          style="height: 14px; width: 14px"
          :id="'item-1-' + index"
          @click="alert($event, 1, index)"
        >
        </v-btn>
        <v-card class="item" style="margin-left: 10px" elevation="2" outlined
          ><v-card-subtitle
            >{{ item.content }}</v-card-subtitle
          ></v-card
        >
        <v-btn icon class="remove-btn" color="red" @click="removeItem(1, index)"
          ><v-icon style="padding-left: 0px">close</v-icon>
        </v-btn>
      </v-row>
      <v-row>
        <v-text-field   label="Item Content" style="z-index: 1; margin-right: 10px" v-model="right"></v-text-field>
        <v-btn
          class="add"
          color="green"
          @click="addItem(1)"
          data-cy="addOptionMultipleChoice"
          >Add item</v-btn
        ></v-row
      >
    </v-col>
    <canvas
      id="canvas"
      @mousemove="mousemove($event)"
      @click="click($event)"
    ></canvas>
  </v-row>
</template>

<style scoped>
.add {
  z-index: 1;
  margin-left: auto;
  margin-right: auto;
  margin-top: 15px;
}
.v-btn {
  min-width: 0;
  min-height: 0;
}

div.card__actions .btn {
  min-width: 0;
}

#canvas {
  border: 0px solid red;
  position: absolute;
  left: 0;
  top: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.bullet {
  transform: translateY(150%);
  z-index: 1;
}

.remove-btn {
  z-index: 1;
  -ms-transform: translateY(25%);
  transform: translateY(25%);
}
.item {
  margin-bottom: 10px;
  text-align: center;
  width: calc(100% - 36px - 24px);
}
</style>

<script lang="ts">
import { Component, Model, PropSync, Vue, Watch } from 'vue-property-decorator';
import ItemCombinationQuestionDetails from '@/models/management/questions/ItemCombinationQuestionDetails';
import Association from '@/models/management/Association';
import Item from '@/models/management/Item';

@Component
export default class ItemCombinationCreate extends Vue {
  @PropSync('questionDetails', { type: ItemCombinationQuestionDetails })
  sQuestionDetails!: ItemCombinationQuestionDetails;

  left: string = "";
  right: string = "";

  drawing: boolean = false;
  originGroup: number = -1;
  originIndex: number = -1;
  hovered: number[] = [];

  mounted() {

      
    setTimeout(() => {this.clearCanvas();this.clearCanvas(); this.drawAssocations();}, 350);

  }

  clearCanvas() {
    var c = document.getElementById('canvas');
    c.style.width = '100%';
    c.style.height = '100%';
    c.width = c.offsetWidth;
    c.height = c.offsetHeight;
  }

  click(e) {
    var newConnections = [];
    for (var i = 0; i < this.sQuestionDetails.associations.length; i++) {
      if (this.hovered.indexOf(i) < 0) {
        newConnections.push(this.sQuestionDetails.associations[i]);
      }
    }
    this.hovered = [];
    this.sQuestionDetails.associations = newConnections;
    this.clearCanvas();
    this.drawAssocations();
  }
  mousemove(e) {
    this.clearCanvas();

    var c = document.getElementById('canvas');
    var cr = c.getBoundingClientRect();
    var ctx = c.getContext('2d');
    var rect = c.getBoundingClientRect(),
      x = e.clientX - rect.left,
      y = e.clientY - rect.top;

    if (this.drawing) {
      var i1 = document
        .getElementById('item-' + this.originGroup + '-' + this.originIndex)
        .getBoundingClientRect();
      var x1 = i1.left - cr.left + 7;
      var y1 = i1.top - cr.top + 7;
      ctx.beginPath();
      ctx.moveTo(x1, y1);
      ctx.lineTo(x, y);
      ctx.lineWidth = 6;
      ctx.strokeStyle = 'black';
      ctx.stroke();
    }

    for (var i = 0; i < this.sQuestionDetails.associations.length; i++) {
      this.drawAssocation(
        this.sQuestionDetails.associations[i].left,
        this.sQuestionDetails.associations[i].right,
        0,
        false
      );

      if (ctx.isPointInStroke(x, y)) {
        if (this.hovered.indexOf(i) < 0) {
          this.hovered.push(i);
        }
      } else {
        var index = this.hovered.indexOf(i);
        if (index >= 0) {
          this.hovered.splice(index, 1);
        }
      }
    }
    this.drawAssocations();
  }

  drawAssocations() {
    for (var i = 0; i < this.sQuestionDetails.associations.length; i++) {
      if (this.hovered.indexOf(i) >= 0) {
        var state = 1;
      } else {
        var state = 0;
      }
      console.log(i)
      this.drawAssocation(
        this.sQuestionDetails.associations[i].left,
        this.sQuestionDetails.associations[i].right,
        state,
        true
      );
    }
  }

  drawAssocation(origin, destination, state, stroke) {
    var c = document.getElementById('canvas');
    var cr = c.getBoundingClientRect();
    var ctx = c.getContext('2d');

    var i1 = document
      .getElementById('item-0-' + origin)
      .getBoundingClientRect();
    var i2 = document
      .getElementById('item-1-' + destination)
      .getBoundingClientRect();
    var x1 = i1.left - cr.left + 7;
    var y1 = i1.top - cr.top + 7;
    var x2 = i2.left - cr.left + 7;
    var y2 = i2.top - cr.top + 7;

    if (state === 0) {
      ctx.strokeStyle = 'black';
    } else {
      ctx.strokeStyle = 'red';
    }

    ctx.beginPath();
    ctx.moveTo(x1, y1);
    ctx.lineTo(x2, y2);
    ctx.lineWidth = 6;

    if (stroke) {
      ctx.stroke();
    }
  }

  alert(e, group, index) {
    if (!this.drawing) {
      this.drawing = true;
      this.originGroup = group;
      this.originIndex = index;
    } else {
      if (this.originGroup != group) {
        var a = new Association();
        if (this.originGroup == 0) {
          a.left = this.originIndex;
          a.right = index;
        } else {
          a.right = this.originIndex;
          a.left = index;
        }

        this.sQuestionDetails.associations.push(a);
        this.drawing = false;
      } else {
        this.drawing = false;
      }
    }
  }

  addItem(group) {
    this.clearCanvas();

    if (group === 0) {
      if (this.left.length > 0) {
        var i = new Item();
        i.content = this.left;
        this.left = "";
        this.sQuestionDetails.groupLeft.push(i);
      }
    } else {
      if (this.right.length > 0) {
        var i = new Item();
        i.content = this.right;
        this.right = "";
        this.sQuestionDetails.groupRight.push(i);}
    }
    this.$nextTick(() => {
      this.clearCanvas();
      this.drawAssocations();
    });
  }

  removeItem(group, index) {
    if (group === 0) {
      this.sQuestionDetails.groupLeft.splice(index, 1);
    } else {
      this.sQuestionDetails.groupRight.splice(index, 1);
    }

    var nc = [];
    for (var i = 0; i < this.sQuestionDetails.associations.length; i++) {
      var a = this.sQuestionDetails.associations[i];
      if (group === 0) {
        if (index == a.left) {
          this.sQuestionDetails.associations.splice(i, 1);
        }
        if (a.left > index) {
          a.left = a.left - 1;
        }
      } else {
        if (index == a.right) {
          this.sQuestionDetails.associations.splice(i, 1);
        }
        if (a.right > index) {
          a.right = a.right - 1;
        }
      }
    }

    this.$nextTick(() => {
      this.clearCanvas();
      this.drawAssocations();
    });
  }
}
</script>
