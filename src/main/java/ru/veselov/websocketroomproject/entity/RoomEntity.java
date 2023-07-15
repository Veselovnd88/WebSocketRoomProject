package ru.veselov.websocketroomproject.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Table(name = "room")
@EqualsAndHashCode(exclude = {"urls", "id", "tags"})
@ToString(exclude = {"urls", "tags"})
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

    @Column(name = "user_qnt")
    private Integer userQnt = 0;

    @Column(name = "max_user_qnt")
    private Integer maxUserQnt = 0;

    @Column(name = "users")
    @ElementCollection(targetClass = String.class)
    @CollectionTable(
            name = "room_users",
            joinColumns = @JoinColumn(name = "room_id", referencedColumnName = "id")
    )
    private Set<String> users = new HashSet<>();

    @Column(name = "created_at", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime createdAt;

    @Column(name = "changed_at")
    @Temporal(TemporalType.TIMESTAMP)
    private ZonedDateTime changedAt;

    @Column(name = "urls")
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UrlEntity> urls = new LinkedList<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.REFRESH, CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "room_tag",
            joinColumns = {@JoinColumn(name = "room_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private Set<TagEntity> tags = new HashSet<>();
    //this is owning side, deleting tag from this entity also deleted tag from join table

    public void addUrl(UrlEntity url) {
        url.setRoom(this);
        this.urls.add(url);
    }

    public void addTag(TagEntity tagEntity) {
        this.tags.add(tagEntity);
    }

    public void removeTag(TagEntity tagEntity) {
        this.tags.remove(tagEntity);
    }

}
