package com.romander.bookingapp.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString
@SQLDelete(sql = "UPDATE accommodation Set is_deleted = true WHERE id=?")
@SQLRestriction("is_deleted = false")
@Table(name = "accommodation")
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;
    @Column(nullable = false)
    @Embedded
    private Address location;
    @Column(nullable = false)
    private String size;
    @ElementCollection
    @CollectionTable(name = "accommodation_amenities",
            joinColumns = @JoinColumn(name = "accommodation_id"))
    @Column(name = "amenity", nullable = false)
    private List<String> amenities = new ArrayList<>();
    @Column(nullable = false)
    private BigDecimal dailyRate;
    @Column(nullable = false)
    private Integer availability;
    @Column(nullable = false)
    private boolean isDeleted;

    public enum Type {
        HOUSE, APARTMENT, CONDO, VACATION_HOME
    }
}
