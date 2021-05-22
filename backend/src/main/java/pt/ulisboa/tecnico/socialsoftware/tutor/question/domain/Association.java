package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.AssociationDto;

import javax.persistence.*;


@Entity
@Table(name = "associations")
public class Association implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer leftIndex;

    @Column(nullable = false)
    private Integer rightIndex;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_details_id")
    private ItemCombinationQuestion questionDetails;

    public Association() {
    };

    public Association(Integer left, Integer right) {
        this.leftIndex = left;
        this.rightIndex = right;
    }

    public Association(AssociationDto associationDto) {
        setLeft(associationDto.getLeft());
        setRight(associationDto.getRight());
    }

    public Integer getId() {
        return id;
    }

    public int getLeft() {
        return leftIndex;
    }

    public void setLeft(int left) {
        this.leftIndex = left;
    }

    public int getRight() {
        return rightIndex;
    }

    public void setRight(int right) {
        this.rightIndex = right;
    }

    public ItemCombinationQuestion getQuestionDetails() {
        return questionDetails;
    }

    public void setQuestionDetails(ItemCombinationQuestion question) {
        this.questionDetails = question;
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visitAssociation(this);
    }

    public void remove(){
        this.questionDetails = null;
    }
}
