package com.example.wms.model.db.entity;

import com.example.wms.model.enums.LocationType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "locations")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", unique = true, length = 100, nullable = false)
    private String name;

    @Column(name = "type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private LocationType locationType;

    @Column(name = "max_capacity")
    private Long maxCapacity;

    @Column(name = "dimensions", length = 100)
    private String dimensions;

    @Column(name = "description", length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @ColumnDefault("true")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @JsonBackReference(value = "warehouse-location")
    private Warehouse warehouse;

    @OneToMany(mappedBy = "fromLocation", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "location-outgoing")
    private List<OperationDetail> outgoingOperations = new ArrayList<>();

    @OneToMany(mappedBy = "toLocation", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "location-incoming")
    private List<OperationDetail> incomingOperations = new ArrayList<>();

    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
    @JsonManagedReference(value = "location-stock")
    private List<Stock> stocks = new ArrayList<>();

}