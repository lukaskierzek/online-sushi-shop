package com.sushiShop.onlineSushiShop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "Comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
    @SequenceGenerator(name = "comment_seq", sequenceName = "comment_sequence", allocationSize = 1)
    @Column(name = "Comments_Id")
    private Long CommentId;

    @Column(name = "Comments_text")
    private String CommentText;

    @OneToOne(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Item item;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "isHidden", column = @Column(name = "comments_is_hidden")),
            @AttributeOverride(name = "createdAt", column = @Column(name = "comments_created_at")),
            @AttributeOverride(name = "updateAt", column = @Column(name = "comments_update_at"))
    })
    private AdditionalInformation additionalInformation;

    public Comment() {
    }

    public Comment(String commentText, AdditionalInformation additionalInformation) {
        this.CommentText = commentText;
        this.additionalInformation = additionalInformation;
    }

    public Comment(Long commentId, String commentText, Item item, AdditionalInformation additionalInformation) {
        this.CommentId = commentId;
        this.CommentText = commentText;
        this.item = item;
        this.additionalInformation = additionalInformation;
    }

    public Long getCommentId() {
        return CommentId;
    }

    public void setCommentId(Long commentId) {
        CommentId = commentId;
    }

    public String getCommentText() {
        return CommentText;
    }

    public void setCommentText(String commentText) {
        CommentText = commentText;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public AdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "CommentId=" + CommentId +
                ", CommentText='" + CommentText + '\'' +
                ", item=" + item +
                ", additionalInformation=" + additionalInformation +
                '}';
    }
}
