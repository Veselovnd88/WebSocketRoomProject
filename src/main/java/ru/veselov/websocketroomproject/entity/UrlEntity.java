package ru.veselov.websocketroomproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "url")
@Data
@ToString(exclude = "room")
@EqualsAndHashCode(exclude = {"urlId", "room"})
@NoArgsConstructor
public class UrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "url_id", nullable = false)
    private Long urlId;

    @Column(name = "source_url", nullable = false)
    private String url;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private RoomEntity room;

    public UrlEntity(String url, ZonedDateTime createdAt) {
        this.url = url;
        this.createdAt = createdAt;
    }

}