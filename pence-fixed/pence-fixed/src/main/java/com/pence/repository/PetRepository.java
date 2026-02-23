package com.pence.repository;

import com.pence.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    List<Pet> findByStatus(Pet.PetStatus status);

    List<Pet> findByPetTypeAndStatus(Pet.PetType petType, Pet.PetStatus status);

    List<Pet> findByOwnerPhone(String ownerPhone);

    // Bildiriş sistemi üçün — GPS radius axtarışı
    List<Pet> findByLatitudeBetweenAndLongitudeBetween(
            Double minLat, Double maxLat,
            Double minLng, Double maxLng
    );
}
