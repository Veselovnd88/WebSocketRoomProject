package ru.veselov.websocketroomproject.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
@Data
@ToString(exclude = "rooms")
@EqualsAndHashCode(exclude = {"tagId", "rooms"})
@NoArgsConstructor
public class TagEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime createdAt;

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY,
            cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    private Set<RoomEntity> rooms = new HashSet<>();

    public TagEntity(String name) {
        this.name = name;
    }

    public TagEntity(String name, ZonedDateTime createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }

}
