package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Item;

import java.io.Serializable;

public class ItemDto implements Serializable {
    private Integer id;
    private String content;

    public ItemDto() {
    }

    public ItemDto(Item item) {
        this.id = item.getId();
        this.content = item.getContent();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getId() {
        return id;
    }

}