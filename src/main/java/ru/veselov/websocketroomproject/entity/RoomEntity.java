package ru.veselov.websocketroomproject.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "room")
@EqualsAndHashCode(exclude = {"urls", "id"})
@ToString(exclude = {"urls"})
public class RoomEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid", updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "is_private")
    private Boolean isPrivate;

    @Column(name = "active_url")
    private String activeUrl;

    @Column(name = "room_token")
    private String roomToken;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;
    @Column(name = "player_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlayerType playerType;

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime createdAt;

    @Column(name = "changed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime changedAt;
    @Column(name = "urls")
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UrlEntity> urls = new LinkedList<>();

    public void addUrl(UrlEntity url) {
        url.setRoom(this);
        this.urls.add(url);
    }

}