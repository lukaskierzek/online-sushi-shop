package com.sushiShop.onlineSushiShop.model;

import com.sushiShop.onlineSushiShop.enums.IsHidden;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Embeddable
public class AdditionalInformation {

    @Enumerated
    @Column(name = "Items_is_hidden", nullable = false)
    private IsHidden isHidden;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updateAt;

    public AdditionalInformation() {
    }

    public AdditionalInformation(IsHidden isHidden, LocalDateTime createdAt, LocalDateTime updateAt) {
        this.isHidden = isHidden;
        this.createdAt = createdAt;
        this.updateAt = updateAt;
    }

    public IsHidden getIsHidden() {
        return isHidden;
    }

    public void setIsDeleted(IsHidden isHidden) {
        this.isHidden = isHidden;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    @Override
    public String toString() {
        return "AdditionalInformation{" +
                "isHidden=" + isHidden +
                ", createdAt=" + createdAt +
                ", updateAt=" + updateAt +
                '}';
    }
}
