package com.pence.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetType petType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PetStatus status;

    @Column(nullable = false)
    private String ownerName;

    @Column(nullable = false)
    private String ownerPhone;

    private String description;
    private String location;

    @Column(nullable = false)
    private String imagePath;

    @Column(nullable = false, length = 64)
    private String imageHash;

    private Double latitude;
    private Double longitude;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // ------------------------------------------------
    // CONSTRUCTOR-LAR
    // ------------------------------------------------

    public Pet() {}

    public Pet(Long id, PetType petType, PetStatus status,
               String ownerName, String ownerPhone,
               String description, String location,
               String imagePath, String imageHash,
               Double latitude, Double longitude,
               LocalDateTime createdAt) {
        this.id          = id;
        this.petType     = petType;
        this.status      = status;
        this.ownerName   = ownerName;
        this.ownerPhone  = ownerPhone;
        this.description = description;
        this.location    = location;
        this.imagePath   = imagePath;
        this.imageHash   = imageHash;
        this.latitude    = latitude;
        this.longitude   = longitude;
        this.createdAt   = createdAt;
    }

    // DB-ə yazılmadan əvvəl tarix otomatik yazılır
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // ------------------------------------------------
    // GETTER-lər
    // ------------------------------------------------

    public Long getId()                  { return id; }
    public PetType getPetType()          { return petType; }
    public PetStatus getStatus()         { return status; }
    public String getOwnerName()         { return ownerName; }
    public String getOwnerPhone()        { return ownerPhone; }
    public String getDescription()       { return description; }
    public String getLocation()          { return location; }
    public String getImagePath()         { return imagePath; }
    public String getImageHash()         { return imageHash; }
    public Double getLatitude()          { return latitude; }
    public Double getLongitude()         { return longitude; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    // ------------------------------------------------
    // SETTER-lər
    // ------------------------------------------------

    public void setId(Long id)                       { this.id = id; }
    public void setPetType(PetType petType)           { this.petType = petType; }
    public void setStatus(PetStatus status)           { this.status = status; }
    public void setOwnerName(String ownerName)        { this.ownerName = ownerName; }
    public void setOwnerPhone(String ownerPhone)      { this.ownerPhone = ownerPhone; }
    public void setDescription(String description)    { this.description = description; }
    public void setLocation(String location)          { this.location = location; }
    public void setImagePath(String imagePath)        { this.imagePath = imagePath; }
    public void setImageHash(String imageHash)        { this.imageHash = imageHash; }
    public void setLatitude(Double latitude)          { this.latitude = latitude; }
    public void setLongitude(Double longitude)        { this.longitude = longitude; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ------------------------------------------------
    // BUILDER — Design Pattern
    // ------------------------------------------------

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private PetType petType;
        private PetStatus status;
        private String ownerName;
        private String ownerPhone;
        private String description;
        private String location;
        private String imagePath;
        private String imageHash;
        private Double latitude;
        private Double longitude;
        private LocalDateTime createdAt;

        public Builder id(Long id)                   { this.id = id;                 return this; }
        public Builder petType(PetType v)             { this.petType = v;             return this; }
        public Builder status(PetStatus v)            { this.status = v;              return this; }
        public Builder ownerName(String v)            { this.ownerName = v;           return this; }
        public Builder ownerPhone(String v)           { this.ownerPhone = v;          return this; }
        public Builder description(String v)          { this.description = v;         return this; }
        public Builder location(String v)             { this.location = v;            return this; }
        public Builder imagePath(String v)            { this.imagePath = v;           return this; }
        public Builder imageHash(String v)            { this.imageHash = v;           return this; }
        public Builder latitude(Double v)             { this.latitude = v;            return this; }
        public Builder longitude(Double v)            { this.longitude = v;           return this; }
        public Builder createdAt(LocalDateTime v)     { this.createdAt = v;           return this; }

        public Pet build() {
            Pet p = new Pet();
            p.id          = this.id;
            p.petType     = this.petType;
            p.status      = this.status;
            p.ownerName   = this.ownerName;
            p.ownerPhone  = this.ownerPhone;
            p.description = this.description;
            p.location    = this.location;
            p.imagePath   = this.imagePath;
            p.imageHash   = this.imageHash;
            p.latitude    = this.latitude;
            p.longitude   = this.longitude;
            p.createdAt   = this.createdAt;
            return p;
        }
    }

    // ------------------------------------------------
    // ENUM-lər
    // ------------------------------------------------

    public enum PetType {
        DOG, CAT, BIRD, RABBIT, OTHER
    }

    public enum PetStatus {
        LOST,   // itmiş
        FOUND   // tapılan
    }

    @Override
    public String toString() {
        return "Pet{id=" + id + ", type=" + petType + ", status=" + status + ", owner=" + ownerName + "}";
    }
}
