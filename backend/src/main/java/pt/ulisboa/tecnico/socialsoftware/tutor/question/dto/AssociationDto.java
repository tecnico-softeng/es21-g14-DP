package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Association;

import java.io.Serializable;

public class AssociationDto implements Serializable {
    private Integer id;
    private int left;
    private int right;

    public AssociationDto() {
    }

    public AssociationDto(int left, int right) {
        this.left = left;
        this.right = right;
    }

    public AssociationDto(Association association) {
        this.left = association.getLeft();
        this.right = association.getRight();
        this.id = association.getId();
    }

    public Integer getId() {
        return id;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }
}
