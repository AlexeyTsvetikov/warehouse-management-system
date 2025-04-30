package com.example.wms.model.db.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "warehouses")
public class Warehouse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", length = 100, unique = true, nullable = false)
    private String name;

    @Column(name = "address", length = 500, nullable = false)
    private String address;

    @Column(name = "capacity", precision = 10, scale = 2, nullable = false)
    private BigDecimal capacity;

    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "warehouse-location")
    private List<Location> locations = new ArrayList<>();

}