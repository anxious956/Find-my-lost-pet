package com.pence.controller;

import com.pence.dto.MatchResultDTO;
import com.pence.dto.PetDTO;
import com.pence.model.Pet;
import com.pence.service.PetService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
public class PetController {

    private static final Logger log = Logger.getLogger(PetController.class.getName());

    private final PetService petService;

    public PetController(PetService petService) {
        this.petService = petService;
    }

    // GET /api/pets/lost
    @GetMapping("/lost")
    public ResponseEntity<List<PetDTO>> getLostPets(HttpServletRequest req) {
        return ResponseEntity.ok(petService.getAllLostPets(baseUrl(req)));
    }

    // GET /api/pets/found
    @GetMapping("/found")
    public ResponseEntity<List<PetDTO>> getFoundPets(HttpServletRequest req) {
        return ResponseEntity.ok(petService.getAllFoundPets(baseUrl(req)));
    }

    // GET /api/pets/image/{id}
    @GetMapping("/image/{id}")
    public ResponseEntity<Resource> getImage(@PathVariable Long id) {
        String path = petService.getImagePath(id);
        File   file = new File(path);
        if (!file.exists()) return ResponseEntity.notFound().build();
        String type = path.endsWith(".png") ? "image/png" : "image/jpeg";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(type))
                .body(new FileSystemResource(file));
    }

    // POST /api/pets/lost
    @PostMapping(value = "/lost", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PetDTO> registerLost(
            @RequestParam("image")                                 MultipartFile image,
            @RequestParam("ownerName")                             String ownerName,
            @RequestParam("ownerPhone")                            String ownerPhone,
            @RequestParam("petType")                               String petType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "location",    required = false) String location,
            @RequestParam(value = "latitude",    required = false) Double latitude,
            @RequestParam(value = "longitude",   required = false) Double longitude,
            HttpServletRequest req) {
        try {
            return ResponseEntity.ok(petService.registerLostPet(
                    image, ownerName, ownerPhone, petType,
                    description, location, latitude, longitude, baseUrl(req)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // POST /api/pets/found
    @PostMapping(value = "/found", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PetDTO> registerFound(
            @RequestParam("image")                                 MultipartFile image,
            @RequestParam("reporterName")                          String reporterName,
            @RequestParam("reporterPhone")                         String reporterPhone,
            @RequestParam("petType")                               String petType,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "location",    required = false) String location,
            @RequestParam(value = "latitude",    required = false) Double latitude,
            @RequestParam(value = "longitude",   required = false) Double longitude,
            HttpServletRequest req) {
        try {
            return ResponseEntity.ok(petService.registerFoundPet(
                    image, reporterName, reporterPhone, petType,
                    description, location, latitude, longitude, baseUrl(req)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // POST /api/pets/match
    @PostMapping(value = "/match", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MatchResultDTO<PetDTO>> match(
            @RequestParam("image")                                    MultipartFile image,
            @RequestParam(value = "searchIn", defaultValue = "FOUND") String searchIn,
            HttpServletRequest req) {
        try {
            Pet.PetStatus status = Pet.PetStatus.valueOf(searchIn);
            return ResponseEntity.ok(petService.findMatches(image, status, baseUrl(req)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private String baseUrl(HttpServletRequest req) {
        return req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort();
    }
}
