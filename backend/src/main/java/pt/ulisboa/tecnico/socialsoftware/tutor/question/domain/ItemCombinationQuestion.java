package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.AnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.CorrectAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementAnswerDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.StatementQuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.NO_EMPTY_GROUP_ALLOWED;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AT_LEAST_TWO_ITEMS_NEEDED;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.VALID_CORRECT_ANSWER_NEEDED;


@Entity
@DiscriminatorValue(Question.QuestionTypes.ITEM_COMBINATION_QUESTION)
public class ItemCombinationQuestion extends QuestionDetails {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<Association> associations = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<Item> groupLeft = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.LAZY, orphanRemoval = true)
    private final List<Item> groupRight = new ArrayList<>();


    public ItemCombinationQuestion() {
        super();
    }

    public ItemCombinationQuestion(Question question, ItemCombinationQuestionDto itemCombinationQuestionDto)  {
        super(question);
        List<ItemDto> left = itemCombinationQuestionDto.getGroupLeft();
        List<ItemDto> right = itemCombinationQuestionDto.getGroupRight();
        setGroups(left, right);

        setAssociations(itemCombinationQuestionDto.getAssociations());
    }

    public List<Item> getGroupLeft() {
        return this.groupLeft;
    }

    public List<Item> getGroupRight() {
        return this.groupRight;
    }


    public void setGroups(List<ItemDto> left, List<ItemDto> right) {
        if (left.isEmpty() || right.isEmpty()) {
            throw new TutorException(NO_EMPTY_GROUP_ALLOWED);
        }

        if (left.size() == 1 && right.size() == 1) {
            throw new TutorException(AT_LEAST_TWO_ITEMS_NEEDED);
        }

        this.groupLeft.clear();
        this.groupRight.clear();

        for (ItemDto itemDto : left) {
            Item item = new Item(itemDto);
            item.setQuestionDetails(this);
            this.groupLeft.add(item);
        }

        for (ItemDto itemDto : right) {
            Item item = new Item(itemDto);
            item.setQuestionDetails(this);
            this.groupRight.add(item);
        }
        validateConnections();
    }


    public List<Association> getAssociations() {
        return this.associations;
    }

    public void setAssociations(List<AssociationDto> associations) {
        if (associations.isEmpty()) {
            throw new TutorException(VALID_CORRECT_ANSWER_NEEDED);
        }

        this.associations.clear();

        for (AssociationDto associationDto : associations) {
            Association association = new Association(associationDto);
            association.setQuestionDetails(this);
            this.associations.add(association);
        }

        validateConnections();
    }

    public void visitGroupLeft(Visitor visitor) {
        for (Item item : this.getGroupLeft()) {
            item.accept(visitor);
        }
    }

    public void visitGroupRight(Visitor visitor) {
        for (Item item : this.getGroupRight()) {
            item.accept(visitor);
        }
    }

    public void visitAssociations(Visitor visitor) {
        for (Association association : this.getAssociations()) {
            association.accept(visitor);
        }
    }

    private boolean isInvalid(Association association) {
        if (association.getLeft() < 0 || association.getRight() < 0) {
            return true;
        }

        return association.getLeft() > getGroupLeft().size() || association.getRight() > getGroupRight().size();
    }

    private void validateConnections() {
        for (Association association : this.associations) {
            if (isInvalid(association)) {
                throw new TutorException(VALID_CORRECT_ANSWER_NEEDED);
            }
        }
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return null;
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return null;
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return null;
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return null;
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new ItemCombinationQuestionDto(this);
    }

    public void update(ItemCombinationQuestionDto questionDetails) {
        setGroups(questionDetails.getGroupLeft(), questionDetails.getGroupRight());
        setAssociations(questionDetails.getAssociations());
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        return null;
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return null;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    @Override
    public void delete() {
        super.delete();
        for (Association association : this.associations) {
            association.remove();
        }
        this.associations.clear();
        for (Item item : this.groupLeft) {
            item.remove();
        }
        this.groupLeft.clear();
        for (Item item : this.groupRight) {
            item.remove();
        }
        this.groupRight.clear();
    }
}
