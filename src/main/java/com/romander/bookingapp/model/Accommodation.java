package com.romander.bookingapp.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;

import java.lang.annotation.Retention;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    @CollectionTable(name = "accommodation_amenities", joinColumns = @JoinColumn(name = "accommodation_id"))
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
