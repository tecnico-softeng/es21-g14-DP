package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemCombinationQuestionDto extends QuestionDetailsDto {
    private List<ItemDto> groupLeft = new ArrayList<>();
    private List<ItemDto> groupRight = new ArrayList<>();
    private List<AssociationDto> associations = new ArrayList<>();


    public ItemCombinationQuestionDto() {
    }

    public ItemCombinationQuestionDto(ItemCombinationQuestion question) {
        this.groupLeft = question.getGroupLeft().stream().map(ItemDto::new).collect(Collectors.toList());
        this.groupRight = question.getGroupRight().stream().map(ItemDto::new).collect(Collectors.toList());
        this.associations = question.getAssociations().stream().map(AssociationDto::new).collect(Collectors.toList());
    }

    public List<ItemDto> getGroupLeft() {
        return groupLeft;
    }

    public void setGroupLeft(List<ItemDto> groupLeft) {
        this.groupLeft = groupLeft;
    }

    public List<ItemDto> getGroupRight() {
        return groupRight;
    }

    public void setGroupRight(List<ItemDto> groupRight) {
        this.groupRight = groupRight;
    }

    public List<AssociationDto> getAssociations() {
        return this.associations;
    }

    public void setAssociations(List<AssociationDto> associations) {
        this.associations = associations;
    }

    public void setAssociationsList(ArrayList<ArrayList<Integer>> associations) {
        this.associations.clear();
        for (ArrayList<Integer> pair : associations) {
            AssociationDto associationDto = new AssociationDto(pair.get(0), pair.get(1));
            this.associations.add(associationDto);
        }
    }

    public void addAssociation(int left, int right){
        this.associations.add(new AssociationDto(left, right));
    }
    
    public void remAssociation(int left, int right){
        for (int i=0; i<this.associations.size(); i++) {
            if (this.associations.get(i).getLeft()==left && this.associations.get(i).getRight() == right){
                this.associations.remove(i);
                break;
            }
        }
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new ItemCombinationQuestion(question, this);
    }


    public void update(ItemCombinationQuestion question) {
        question.update(this);
    }

    @Override
    public String toString() {
        return "MultipleChoiceQuestionDto{" +
                "groupLeft=" + groupLeft + ", " +
                "groupRight=" + groupRight + ", " +
                "associations=" + associations + ", " +
                '}';
    }
}
