package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ItemDto;

import javax.persistence.*;
import java.util.*;


@Entity
@Table(name = "items")
public class Item implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_details_id")
    private ItemCombinationQuestion questionDetails;

    public Item() {
    }

    public Item(ItemDto item) {
        setContent(item.getContent());
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitItem(this);
    }

    public Integer getId() {
        return id;
    }

    public void setContent(String content) {
        this.content = "";
    }

    public String getContent() {
        return content;
    }

    public void setQuestionDetails(ItemCombinationQuestion question) {
        this.questionDetails = question;
    }

    public ItemCombinationQuestion getQuestionDetails() {
        return this.questionDetails;
    }

    public void remove(){
        this.questionDetails = null;
    }

}
