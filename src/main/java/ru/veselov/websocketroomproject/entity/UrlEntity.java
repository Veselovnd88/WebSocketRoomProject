package ru.veselov.websocketroomproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "url")
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "room")
public class UrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "url_id", nullable = false)
    private Long urlId;

    @Column(name = "source_url", nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    private RoomEntity room;

    public UrlEntity(String url) {
        this.url = url;
    }

}