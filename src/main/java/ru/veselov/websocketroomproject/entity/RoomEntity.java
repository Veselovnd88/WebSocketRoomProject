package ru.veselov.websocketroomproject.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room")
@ToString
public class RoomEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "is_private")
    private Boolean isPrivate;

    @Column(name = "active_url")
    private String activeUrl;

    @Column(name = "room_token")
    private String roomToken;

    @Column(name = "owner_name")
    private String ownerName;
    @Column(name = "player_type")
    @Enumerated(EnumType.STRING)
    private PlayerType playerType;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime createdAt;

    @Column(name = "changed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime changedAt;
    @Column(name = "urls")
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UrlEntity> urls = new LinkedList<>();

    public void addUrl(String url) {
        UrlEntity urlEntity = new UrlEntity(url);
        urlEntity.setRoom(this);
        this.urls.add(urlEntity);
    }
}
