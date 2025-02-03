package com.sushiShop.onlineSushiShop.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_seq")
    @SequenceGenerator(name = "comment_seq", sequenceName = "comment_sequence", allocationSize = 1)
    @Column(name = "Comments_Id")
    private Long commentId;

    @Column(name = "Comments_text", columnDefinition = "VARCHAR(1024) DEFAULT 'Lorem ipsum dolor sit amet'")
    private String commentText;

    @CreationTimestamp
    @Column(name = "comments_created_at", nullable = false, updatable = false)
    private LocalDateTime commentCreatedAt;

    @UpdateTimestamp
    @Column(name = "comments_updated_at", nullable = false)
    private LocalDateTime commentUpdatedAt;

    @OneToOne(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private Item item;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "isHidden", column = @Column(name = "comments_is_hidden", nullable = false))
    })
    private AdditionalInformation additionalInformation;

    public Comment() {
    }

    public Comment(String commentText, LocalDateTime commentCreatedAt, LocalDateTime commentUpdatedAt, AdditionalInformation additionalInformation) {
        this.commentText = commentText;
        this.commentCreatedAt = commentCreatedAt;
        this.commentUpdatedAt = commentUpdatedAt;
        this.additionalInformation = additionalInformation;
    }

    public Comment(Long commentId, String commentText, LocalDateTime commentCreatedAt, LocalDateTime commentUpdatedAt, Item item, AdditionalInformation additionalInformation) {
        this.commentId = commentId;
        this.commentText = commentText;
        this.commentCreatedAt = commentCreatedAt;
        this.commentUpdatedAt = commentUpdatedAt;
        this.item = item;
        this.additionalInformation = additionalInformation;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "commentId=" + commentId +
                ", commentText='" + commentText + '\'' +
                ", commentCreatedAt=" + commentCreatedAt +
                ", commentUpdatedAt=" + commentUpdatedAt +
                ", item=" + item +
                ", additionalInformation=" + additionalInformation +
                '}';
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public LocalDateTime getCommentCreatedAt() {
        return commentCreatedAt;
    }

    public void setCommentCreatedAt(LocalDateTime commentCreatedAt) {
        this.commentCreatedAt = commentCreatedAt;
    }

    public LocalDateTime getCommentUpdatedAt() {
        return commentUpdatedAt;
    }

    public void setCommentUpdatedAt(LocalDateTime commentUpdatedAt) {
        this.commentUpdatedAt = commentUpdatedAt;
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
}
