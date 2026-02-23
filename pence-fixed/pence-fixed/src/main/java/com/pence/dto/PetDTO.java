package com.pence.dto;

import com.pence.model.Pet;
import java.time.LocalDateTime;

public class PetDTO {

    private Long id;
    private String petType;
    private String status;
    private String ownerName;
    private String ownerPhone;
    private String description;
    private String location;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private LocalDateTime createdAt;

    // ------------------------------------------------
    // CONSTRUCTOR-LAR
    // ------------------------------------------------

    public PetDTO() {}

    public PetDTO(Long id, String petType, String status,
                  String ownerName, String ownerPhone,
                  String description, String location,
                  String imageUrl, Double latitude, Double longitude,
                  LocalDateTime createdAt) {
        this.id          = id;
        this.petType     = petType;
        this.status      = status;
        this.ownerName   = ownerName;
        this.ownerPhone  = ownerPhone;
        this.description = description;
        this.location    = location;
        this.imageUrl    = imageUrl;
        this.latitude    = latitude;
        this.longitude   = longitude;
        this.createdAt   = createdAt;
    }

    // ------------------------------------------------
    // GETTER-lər
    // ------------------------------------------------

    public Long getId()                  { return id; }
    public String getPetType()           { return petType; }
    public String getStatus()            { return status; }
    public String getOwnerName()         { return ownerName; }
    public String getOwnerPhone()        { return ownerPhone; }
    public String getDescription()       { return description; }
    public String getLocation()          { return location; }
    public String getImageUrl()          { return imageUrl; }
    public Double getLatitude()          { return latitude; }
    public Double getLongitude()         { return longitude; }
    public LocalDateTime getCreatedAt()  { return createdAt; }

    // ------------------------------------------------
    // SETTER-lər
    // ------------------------------------------------

    public void setId(Long id)                       { this.id = id; }
    public void setPetType(String petType)            { this.petType = petType; }
    public void setStatus(String status)              { this.status = status; }
    public void setOwnerName(String ownerName)        { this.ownerName = ownerName; }
    public void setOwnerPhone(String ownerPhone)      { this.ownerPhone = ownerPhone; }
    public void setDescription(String description)    { this.description = description; }
    public void setLocation(String location)          { this.location = location; }
    public void setImageUrl(String imageUrl)          { this.imageUrl = imageUrl; }
    public void setLatitude(Double latitude)          { this.latitude = latitude; }
    public void setLongitude(Double longitude)        { this.longitude = longitude; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // ------------------------------------------------
    // Pet entity → PetDTO çevirmə
    // ------------------------------------------------

    public static PetDTO fromEntity(Pet pet, String baseUrl) {
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setPetType(pet.getPetType().name());
        dto.setStatus(pet.getStatus().name());
        dto.setOwnerName(pet.getOwnerName());
        dto.setOwnerPhone(pet.getOwnerPhone());
        dto.setDescription(pet.getDescription());
        dto.setLocation(pet.getLocation());
        dto.setImageUrl(baseUrl + "/api/pets/image/" + pet.getId());
        dto.setLatitude(pet.getLatitude());
        dto.setLongitude(pet.getLongitude());
        dto.setCreatedAt(pet.getCreatedAt());
        return dto;
    }

    @Override
    public String toString() {
        return "PetDTO{id=" + id + ", type=" + petType + ", status=" + status + "}";
    }
}
