package com.sushiShop.onlineSushiShop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subcategories")
public class Subcategory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subcategory_seq")
    @SequenceGenerator(name = "subcategory_seq", sequenceName = "subcategory_sequence", allocationSize = 1)
    @Column(name = "subcategories_id")
    private Long SubcategoryId;

    @Column(name = "subcategories_name")
    private String SubcategoryName;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "isHidden", column = @Column(name = "subcategories_is_hidden")),
            @AttributeOverride(name = "createdAt", column = @Column(name = "subcategories_created_at")),
            @AttributeOverride(name = "updateAt", column = @Column(name = "subcategories_update_at"))
    })
    private AdditionalInformation additionalInformation;

    public Subcategory() {
    }

    public Subcategory(Long subcategoryId, String subcategoryName, AdditionalInformation additionalInformation) {
        this.SubcategoryId = subcategoryId;
        this.SubcategoryName = subcategoryName;
        this.additionalInformation = additionalInformation;
    }

    public Long getSubcategoryId() {
        return SubcategoryId;
    }

    public void setSubcategoryId(Long subcategoryId) {
        SubcategoryId = subcategoryId;
    }

    public String getSubcategoryName() {
        return SubcategoryName;
    }

    public void setSubcategoryName(String subcategoryName) {
        SubcategoryName = subcategoryName;
    }

    public AdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    @Override
    public String toString() {
        return "Subcategory{" +
                "SubcategoryId=" + SubcategoryId +
                ", SubcategoryName='" + SubcategoryName + '\'' +
                ", additionalInformation=" + additionalInformation +
                '}';
    }
}
