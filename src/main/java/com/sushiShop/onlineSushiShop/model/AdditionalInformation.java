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
    private IsHidden isHidden;

    public AdditionalInformation() {
    }

    public AdditionalInformation(IsHidden isHidden) {
        this.isHidden = isHidden;
    }

    public IsHidden getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(IsHidden isHidden) {
        this.isHidden = isHidden;
    }

    @Override
    public String toString() {
        return "AdditionalInformation{" +
                "isHidden=" + isHidden +
                '}';
    }
}
