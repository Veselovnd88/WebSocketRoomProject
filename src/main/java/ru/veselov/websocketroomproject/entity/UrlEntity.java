package ru.veselov.websocketroomproject.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Table(name = "url")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UrlEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "url_id")
    private Long urlId;

    @Column(name = "source_url")
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    private RoomEntity room;

    public UrlEntity(String url) {
        this.url = url;
    }

}
